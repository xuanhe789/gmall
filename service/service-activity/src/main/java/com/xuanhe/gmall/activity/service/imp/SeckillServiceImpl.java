package com.xuanhe.gmall.activity.service.imp;

import com.xuanhe.gmall.activity.config.CacheHelper;
import com.xuanhe.gmall.activity.mapper.ActivityMapper;
import com.xuanhe.gmall.activity.service.SeckillService;
import com.xuanhe.gmall.common.constant.RedisConst;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.common.result.ResultCodeEnum;
import com.xuanhe.gmall.model.activity.OrderRecode;
import com.xuanhe.gmall.model.activity.SeckillGoods;
import com.xuanhe.gmall.model.order.OrderDetail;
import com.xuanhe.gmall.model.user.UserRecode;
import com.xuanhe.gmall.rabbit.constant.MqConst;
import com.xuanhe.gmall.rabbit.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    ActivityMapper activityMapper;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RabbitService rabbitService;

    @Override
    public List<SeckillGoods> findAll() {
        List<SeckillGoods> seckillGoodsList= redisTemplate.boundHashOps(RedisConst.SECKILL_GOODS).values();
        return seckillGoodsList;
    }

    @Override
    public SeckillGoods getSeckillGodd(Long id) {
        SeckillGoods seckillGoods= (SeckillGoods) redisTemplate.boundHashOps(RedisConst.SECKILL_GOODS).get(id+"");
        return seckillGoods;
    }

    @Override
    public void saveOrder(Long skuId, Long id) {
        //用户的分布式锁，防止一个用户在多台机器上抢购
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(RedisConst.SECKILL_USER + id, skuId, 1L, TimeUnit.MINUTES);
        if (aBoolean){
            UserRecode userRecode=new UserRecode(skuId,id+"");
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_SECKILL_USER,MqConst.ROUTING_SECKILL_USER,userRecode);
        }
    }

    @Override
    public Result checkOrder(Long id, Long skuId) {
        OrderRecode orderRecode= (OrderRecode) redisTemplate.boundHashOps(RedisConst.SECKILL_ORDERS+skuId).get(id+"");
        //判断用户是否已下单
        if (orderRecode!=null){
            String orderId= (String) redisTemplate.boundHashOps(RedisConst.SECKILL_ORDERS_USERS+skuId).get(skuId+"_"+id);
            if (!StringUtils.isEmpty(orderId)) {
                return Result.build(orderId, ResultCodeEnum.SECKILL_ORDER_SUCCESS);
            }
        }
        //判断用户是否秒杀成功
        if (orderRecode!=null&& orderRecode.getSeckillGoods().getSkuId().equals(skuId)){
            return Result.build(orderRecode,ResultCodeEnum.SECKILL_SUCCESS);
        }
        //判断是否已售罄
        String status = (String) CacheHelper.get(skuId.toString());
        if ("0".equals(status)){
            return Result.build(null,ResultCodeEnum.SECKILL_FINISH);
        }
        //排队中
        return Result.build(null,ResultCodeEnum.SECKILL_RUN);
    }

    @Override
    public List<OrderDetail> getOrderDetaliList(String skuId, Long id) {
        OrderRecode orderRecode= (OrderRecode) redisTemplate.boundHashOps(RedisConst.SECKILL_ORDERS+skuId).get(id+"");
        if (orderRecode!=null){
            List<OrderDetail> orderDetailList=new ArrayList<>();
            SeckillGoods seckillGoods = orderRecode.getSeckillGoods();
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setSkuId(Long.parseLong(skuId));
            orderDetail.setSkuNum(orderRecode.getNum());
            orderDetail.setImgUrl(seckillGoods.getSkuDefaultImg());
            orderDetail.setSkuName(seckillGoods.getSkuName());
            orderDetail.setOrderPrice(seckillGoods.getCostPrice());
            orderDetailList.add(orderDetail);
            return orderDetailList;
        }
        return null;
    }

    @Override
    public void deleteOrderRecode(String userId,Long skuId) {
        redisTemplate.boundHashOps(RedisConst.SECKILL_ORDERS+skuId).delete(userId);
    }

    @Override
    public void createOrderSucess(String userId, Long skuId,String orderId) {
        redisTemplate.boundHashOps(RedisConst.SECKILL_ORDERS_USERS+skuId).put(userId,orderId);
    }
}
