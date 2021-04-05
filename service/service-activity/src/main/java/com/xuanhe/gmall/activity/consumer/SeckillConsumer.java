package com.xuanhe.gmall.activity.consumer;

import com.rabbitmq.client.Channel;
import com.xuanhe.gmall.common.constant.RedisConst;
import com.xuanhe.gmall.model.activity.OrderRecode;
import com.xuanhe.gmall.model.activity.SeckillGoods;
import com.xuanhe.gmall.model.user.UserRecode;
import com.xuanhe.gmall.rabbit.constant.MqConst;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Component
public class SeckillConsumer {
    @Autowired
    RedisTemplate redisTemplate;
    @RabbitListener(queues = MqConst.QUEUE_SECKILL_USER)
    public void process(Channel channel, Message message, UserRecode userRecode) throws IOException {
        try {
            Object stock = redisTemplate.opsForList().leftPop(RedisConst.SECKILL_STOCK_PREFIX + userRecode.getSkuId());
            //抢库存成功
            if (null != stock) {
                OrderRecode orderRecode = new OrderRecode();
                orderRecode.setNum(1);
                orderRecode.setUserId(userRecode.getUserId());
                SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(RedisConst.SECKILL_GOODS).get(userRecode.getSkuId() + "");
                orderRecode.setSeckillGoods(seckillGoods);
                redisTemplate.boundHashOps(RedisConst.SECKILL_ORDERS+seckillGoods.getSkuId()).put(userRecode.getUserId(), orderRecode);
                //将用户秒杀成功的信息存入redis，后续用来判断用户是否重复抢单
                redisTemplate.opsForValue().setIfAbsent(RedisConst.SECKILL_USER+userRecode.getUserId(),userRecode.getSkuId(),RedisConst.SECKILL__TIMEOUT, TimeUnit.SECONDS);
            } else {
                //库存为空，通知抢购服务更新状态
                redisTemplate.convertAndSend("seckillpush", userRecode.getSkuId() + ":0");
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            e.printStackTrace();
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}
