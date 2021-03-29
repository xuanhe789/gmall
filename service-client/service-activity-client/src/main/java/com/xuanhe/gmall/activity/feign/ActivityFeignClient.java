package com.xuanhe.gmall.activity.feign;

import com.xuanhe.gmall.activity.feign.impl.ActivityFeignClientImpl;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.model.activity.SeckillGoods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value ="service-activity" ,fallback = ActivityFeignClientImpl.class,path = "/api/activity/seckill")
public interface ActivityFeignClient {
    /*
     * 返回秒杀所有列表
     * */
    @GetMapping("/findAll")
    Result<List<SeckillGoods>> findAll();
    /*
     * 获取秒杀商品详情
     * */
    @GetMapping("/getSeckillGoods/{skuId}")
    Result<SeckillGoods> getSeckillGoodById(@PathVariable("skuId") Long skuId);
}
