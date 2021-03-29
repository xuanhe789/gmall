package com.xuanhe.gmall.activity.service;

import com.xuanhe.gmall.model.activity.SeckillGoods;

import java.util.List;

public interface SeckillService {
    List<SeckillGoods> findAll();
    SeckillGoods getSeckillGodd(Long id);

    void saveOrder(Long skuId, Long id);
}
