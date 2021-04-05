package com.xuanhe.gmall.order.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xuanhe.gmall.cart.feign.CartFeignClient;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.common.util.HttpClientUtil;
import com.xuanhe.gmall.model.cart.CartInfo;
import com.xuanhe.gmall.model.enums.OrderStatus;
import com.xuanhe.gmall.model.enums.PaymentWay;
import com.xuanhe.gmall.model.enums.ProcessStatus;
import com.xuanhe.gmall.model.order.OrderDetail;
import com.xuanhe.gmall.model.order.OrderInfo;
import com.xuanhe.gmall.model.user.UserAddress;
import com.xuanhe.gmall.order.bean.WareOrderTask;
import com.xuanhe.gmall.order.bean.WareOrderTaskDetail;
import com.xuanhe.gmall.order.mapper.OrderMapper;
import com.xuanhe.gmall.order.service.OrderService;
import com.xuanhe.gmall.product.feign.ProductFeignClient;
import com.xuanhe.gmall.rabbit.constant.MqConst;
import com.xuanhe.gmall.rabbit.service.RabbitService;
import com.xuanhe.gmall.user.feign.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    @Value("$(ware.url)")
    private String WARE_URL;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserFeignClient userFeignClient;
    @Autowired
    CartFeignClient cartFeignClient;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    ProductFeignClient productFeignClient;
    @Autowired
    RabbitService rabbitService;

    @Override
    public String createTradeNo(String tokenUserId) {
        String prefix=new StringBuffer("user:"+ tokenUserId + ":tradeCode").toString();
        String tradeNo= UUID.randomUUID().toString().replace("-","");
        redisTemplate.opsForValue().set(prefix,tradeNo);
        return tradeNo;
    }
    /*
    * 获取确认订单页面的数据
    * */
    @Override
    public Map<String, Object> getTradeData(String tokenUserId) {
        Map<String, Object> result=new HashMap<>();
        //获取用户地址列表
        List<UserAddress> userAddressListByUserId = userFeignClient.findUserAddressListByUserId(tokenUserId);
        //获取选中的购物项
        List<CartInfo> cartCheckedList = cartFeignClient.getCartCheckedList(tokenUserId);
        //声明一个变量保存总金额
        BigDecimal totalAmount=new BigDecimal("0");
        // 声明一个集合来存储订单明细
        List<OrderDetail> detailArrayList = new ArrayList<>();
        cartCheckedList.stream().forEach(cartInfo -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setOrderPrice(cartInfo.getSkuPrice());
            detailArrayList.add(orderDetail);
        });

        // 计算总金额
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderDetailList(detailArrayList);
        orderInfo.sumTotalAmount();
        //获取订单流水号，防止用户恶意提交订单
        String tradeNo = createTradeNo(tokenUserId);
        result.put("userAddressList", userAddressListByUserId);
        result.put("detailArrayList", detailArrayList);
        result.put("totalNum", detailArrayList.size());
        result.put("totalAmount", orderInfo.getTotalAmount());
        result.put("tradeNo",tradeNo);
        return result;

    }
    /*
    * 用户提交订单，保存订单数据
    * */
    @Override
    @Transactional
    public Long saveOrderInfo(OrderInfo orderInfo) {
        orderInfo.sumTotalAmount();
        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());
        String outTradeNo = "GMALL"+ System.currentTimeMillis() + ""+ new Random().nextInt(1000);
        //设置订单交易编号
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo.setCreateTime(new Date());
        // 定义为1天
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        orderInfo.setExpireTime(calendar.getTime());
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
//        验价格
        orderDetailList.stream().forEach(orderDetail -> {
            BigDecimal skuPrice = productFeignClient.getSkuPrice(orderDetail.getSkuId());
            if (skuPrice.compareTo(orderDetail.getOrderPrice())!=0){
                throw new RuntimeException("价格不符");
            }
        });
        //验库存
        for (OrderDetail orderDetail : orderDetailList) {
            Integer skuNum = orderDetail.getSkuNum();
            Long skuId = orderDetail.getSkuId();
            String result = HttpClientUtil.doGet(WARE_URL + "/hasStock?skuId=" + skuId + "&num=" + skuNum);
            if ("0".equals(result)){
                String skuName = orderDetail.getSkuName();
                throw new RuntimeException(skuName+"  库存不足！");
            }
        }
        //订单存入数据库
        orderMapper.insert(orderInfo);
        //保存订单项
        orderDetailList.stream().forEach(orderDetail -> orderDetail.setOrderId(orderInfo.getId()));
        orderMapper.saveOrderDetailList(orderDetailList);
        //删除购物车
        cartFeignClient.deleteAllCart(orderInfo.getUserId().toString());
        //删除流水号
        String tradeNoKey="user:"+ orderInfo.getUserId() + ":tradeCode";
        redisTemplate.delete(tradeNoKey);
        //将库存信息发送到消息队列，库存系统锁定库存
        WareOrderTask wareOrderTask = new WareOrderTask();
        List<WareOrderTaskDetail> orderTaskDetails = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            WareOrderTaskDetail wareOrderTaskDetail = new WareOrderTaskDetail();
            wareOrderTaskDetail.setSkuId(orderDetail.getSkuId()+"");
            wareOrderTaskDetail.setSkuNum(orderDetail.getSkuNum());
            wareOrderTaskDetail.setSkuName(orderDetail.getSkuName());
            orderTaskDetails.add(wareOrderTaskDetail);
        }

        wareOrderTask.setDetails(orderTaskDetails);
        wareOrderTask.setOrderId(orderInfo.getId()+"");
        wareOrderTask.setConsignee(orderInfo.getConsignee());
        wareOrderTask.setConsigneeTel(orderInfo.getConsigneeTel());
        wareOrderTask.setCreateTime(new Date());
        wareOrderTask.setPaymentWay(PaymentWay.ONLINE.getComment());
        wareOrderTask.setDeliveryAddress(orderInfo.getDeliveryAddress());
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_WARE_STOCK,MqConst.ROUTING_WARE_STOCK, JSONObject.toJSONString(wareOrderTask));
        //发送订单Id到延迟队列，超时删除订单
        rabbitService.sendDelayMessage(MqConst.ROUTING_ORDER_CANCEL,MqConst.ROUTING_ORDER_CANCEL, orderInfo.getId(), 7200L);
        return orderInfo.getId();
    }

    /*
    * 检查订单流水号
    * */

    @Override
    public Boolean checkTradeNo(String userId, String tradeNo){
        String tradeNoKey="user:"+ userId + ":tradeCode";
        String value= (String) redisTemplate.opsForValue().get(tradeNoKey);
        if (null!=value&&value.equals(tradeNo)){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public OrderInfo getOrderInfo(Long orderId) {
        OrderInfo orderInfo=orderMapper.getOrderInfoById(orderId);
        if (orderInfo==null){
            return null;
        }
        List<OrderDetail> orderDetailList=orderMapper.getOrderDetailList(orderId);
        orderInfo.setOrderDetailList(orderDetailList);
        return orderInfo;
    }

    @Override
    public OrderInfo getById(Long orderId) {
        return orderMapper.getOrderInfoById(orderId);
    }

    @Override
    public void deleteOrder(Long orderId) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("id",orderId);
        orderMapper.delete(queryWrapper);
    }

    @Override
    public OrderInfo getOrderInfoByOutTradeNo(String outTradeNo) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("out_trade_no",outTradeNo);
        OrderInfo orderInfo = orderMapper.selectOne(queryWrapper);
        return orderInfo;
    }

    @Override
    public void update(OrderInfo orderInfo) {
        orderMapper.updateById(orderInfo);
    }

    @Override
    @Transactional
    public Long saveSeckillOrderInfo(OrderInfo orderInfo) {
        orderInfo.sumTotalAmount();
        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());
        String outTradeNo = "GMALL"+ System.currentTimeMillis() + ""+ new Random().nextInt(1000);
        //设置订单交易编号
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo.setCreateTime(new Date());
        // 定义为1天
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        orderInfo.setExpireTime(calendar.getTime());
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        //验库存
        for (OrderDetail orderDetail : orderDetailList) {
            Integer skuNum = orderDetail.getSkuNum();
            Long skuId = orderDetail.getSkuId();
            String result = HttpClientUtil.doGet(WARE_URL + "/hasStock?skuId=" + skuId + "&num=" + skuNum);
            if ("0".equals(result)){
                String skuName = orderDetail.getSkuName();
                throw new RuntimeException(skuName+"  库存不足！");
            }
        }
        //订单存入数据库
        orderMapper.insert(orderInfo);
        //保存订单项
        orderDetailList.stream().forEach(orderDetail -> orderDetail.setOrderId(orderInfo.getId()));
        orderMapper.saveOrderDetailList(orderDetailList);
        //删除流水号
        String tradeNoKey="user:"+ orderInfo.getUserId() + ":tradeCode";
        redisTemplate.delete(tradeNoKey);
        //将库存信息发送到消息队列，库存系统锁定库存
        WareOrderTask wareOrderTask = new WareOrderTask();
        List<WareOrderTaskDetail> orderTaskDetails = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            WareOrderTaskDetail wareOrderTaskDetail = new WareOrderTaskDetail();
            wareOrderTaskDetail.setSkuId(orderDetail.getSkuId()+"");
            wareOrderTaskDetail.setSkuNum(orderDetail.getSkuNum());
            wareOrderTaskDetail.setSkuName(orderDetail.getSkuName());
            orderTaskDetails.add(wareOrderTaskDetail);
        }

        wareOrderTask.setDetails(orderTaskDetails);
        wareOrderTask.setOrderId(orderInfo.getId()+"");
        wareOrderTask.setConsignee(orderInfo.getConsignee());
        wareOrderTask.setConsigneeTel(orderInfo.getConsigneeTel());
        wareOrderTask.setCreateTime(new Date());
        wareOrderTask.setPaymentWay(PaymentWay.ONLINE.getComment());
        wareOrderTask.setDeliveryAddress(orderInfo.getDeliveryAddress());
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_WARE_STOCK,MqConst.ROUTING_WARE_STOCK, JSONObject.toJSONString(wareOrderTask));
        //发送订单Id到延迟队列，超时删除订单
        rabbitService.sendDelayMessage(MqConst.ROUTING_ORDER_CANCEL,MqConst.ROUTING_ORDER_CANCEL, orderInfo.getId(), 7200L);
        return orderInfo.getId();
    }
}
