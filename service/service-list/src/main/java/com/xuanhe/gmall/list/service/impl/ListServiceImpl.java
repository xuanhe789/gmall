package com.xuanhe.gmall.list.service.impl;

import com.xuanhe.gmall.list.repository.GoodsRespository;
import com.xuanhe.gmall.list.service.ListService;
import com.xuanhe.gmall.model.list.Goods;
import com.xuanhe.gmall.product.feign.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ListServiceImpl implements ListService {
    @Autowired
    ProductFeignClient productFeignClient;
    @Autowired
    GoodsRespository goodsRespository;
    @Autowired
    RedisTemplate redisTemplate;
    @Override
    public void onSale(Long skuId) {
        Goods goods=productFeignClient.getGoodsBySkuId(skuId);
        goodsRespository.save(goods);
    }

    @Override
    public void cancelSale(Long skuId) {
        Goods goods=productFeignClient.getGoodsBySkuId(skuId);
        goodsRespository.delete(goods);
    }

    @Override
    public void incrHotScore(Long skuId) {
        //查询缓存中的热度值
        Long hotScore= (Long) redisTemplate.opsForValue().increment("skuId_hotScore:"+skuId);
        if (hotScore%20==0){
            Optional<Goods> goodOpt = goodsRespository.findById(skuId);
            Goods goods = goodOpt.get();
            goods.setHotScore(hotScore);
            goodsRespository.save(goods);
        }
    }
}
