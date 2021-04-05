package com.xuanhe.gmall.activity.service;

import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.model.activity.SeckillGoods;
import com.xuanhe.gmall.model.order.OrderDetail;

import java.util.List;

public interface SeckillService {
    List<SeckillGoods> findAll();
    SeckillGoods getSeckillGodd(Long id);

    void saveOrder(Long skuId, Long id);

    Result checkOrder(Long id, Long skuId);

    List<OrderDetail> getOrderDetaliList(String skuId, Long id);

    void deleteOrderRecode(String userId,Long skuId);

    void createOrderSucess(String userId, Long skuId,String orderId);
}
