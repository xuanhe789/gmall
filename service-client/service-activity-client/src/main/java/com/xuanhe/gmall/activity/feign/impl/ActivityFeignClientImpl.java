package com.xuanhe.gmall.activity.feign.impl;

import com.xuanhe.gmall.activity.feign.ActivityFeignClient;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.model.activity.SeckillGoods;
import com.xuanhe.gmall.model.order.OrderDetail;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class ActivityFeignClientImpl implements ActivityFeignClient {
    @Override
    public Result<List<SeckillGoods>> findAll() {
        return Result.fail();
    }

    @Override
    public Result<SeckillGoods> getSeckillGoodById(Long skuId) {
        return  Result.fail();
    }

    @Override
    public List<OrderDetail> getOrderDetaliList(String skuId) {
        return null;
    }
}
