package com.xuanhe.gmall.activity.consumer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbitmq.client.Channel;
import com.xuanhe.gmall.activity.mapper.ActivityMapper;
import com.xuanhe.gmall.common.constant.RedisConst;
import com.xuanhe.gmall.model.activity.SeckillGoods;
import com.xuanhe.gmall.rabbit.constant.MqConst;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.query.ExampleQueryMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class ActivityConsumer {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    ActivityMapper activityMapper;

    @RabbitListener(queues = MqConst.QUEUE_TASK_1)
    public void putGoods(Channel channel, Message message,String str) throws IOException {
        try {

            QueryWrapper<SeckillGoods> queryWrapper = new QueryWrapper<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            queryWrapper.eq("status", 1).gt("stock_count", 0)
                    .eq("DATE_FORMAT(start_time,'%Y-%m-%d')", simpleDateFormat.format(new Date()));
            List<SeckillGoods> seckillGoods = activityMapper.selectList(queryWrapper);
            for (SeckillGoods seckillGood : seckillGoods) {
                Boolean flag = redisTemplate.boundHashOps(RedisConst.SECKILL_GOODS).hasKey(seckillGood.getSkuId().toString());
                if (flag) {
                    continue;
                }
                //将秒杀商品信息放入redis的hash类型中
                redisTemplate.boundHashOps(RedisConst.SECKILL_GOODS).put(seckillGood.getSkuId().toString(), seckillGood);
                for (int i = 0; i < seckillGood.getStockCount(); i++) {
                    //使用redis的list类型存放商品库存，有多少库存就push多少次
                    redisTemplate.boundListOps(RedisConst.SECKILL_STOCK_PREFIX + seckillGood.getSkuId()).leftPush(seckillGood.getSkuId().toString());
                }
                redisTemplate.convertAndSend("seckillpush",seckillGood.getSkuId()+"1");
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            e.printStackTrace();
            //重复入列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}
