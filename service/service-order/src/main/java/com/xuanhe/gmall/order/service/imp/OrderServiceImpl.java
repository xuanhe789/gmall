package com.xuanhe.gmall.order.service.imp;

import com.xuanhe.gmall.cart.feign.CartFeignClient;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.model.cart.CartInfo;
import com.xuanhe.gmall.model.order.OrderDetail;
import com.xuanhe.gmall.model.order.OrderInfo;
import com.xuanhe.gmall.model.user.UserAddress;
import com.xuanhe.gmall.order.mapper.OrderMapper;
import com.xuanhe.gmall.order.service.OrderService;
import com.xuanhe.gmall.user.feign.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserFeignClient userFeignClient;
    @Autowired
    CartFeignClient cartFeignClient;
    @Autowired
    RedisTemplate redisTemplate;

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
        return null;
    }

    /*
    * 检查订单流水号
    * */

    @Override
    public Boolean checkTradeNo(String userId, String tradeNo){
        String tradeNoKey="user:"+ userId + ":tradeCode";
        String value= (String) redisTemplate.opsForValue().get(tradeNoKey);
        if (null!=value&&value.equals(tradeNo)){
            redisTemplate.delete(tradeNoKey);
            return true;
        }else {
            return false;
        }
    }
}
