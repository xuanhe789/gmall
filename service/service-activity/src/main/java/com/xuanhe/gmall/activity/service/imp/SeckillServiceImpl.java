package com.xuanhe.gmall.activity.service.imp;

import com.xuanhe.gmall.activity.mapper.ActivityMapper;
import com.xuanhe.gmall.activity.service.SeckillService;
import com.xuanhe.gmall.common.constant.RedisConst;
import com.xuanhe.gmall.model.activity.SeckillGoods;
import com.xuanhe.gmall.model.user.UserRecode;
import com.xuanhe.gmall.rabbit.constant.MqConst;
import com.xuanhe.gmall.rabbit.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
}
