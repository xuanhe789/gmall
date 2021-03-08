package com.xuanhe.gmall.order.service;

import com.xuanhe.gmall.model.order.OrderInfo;

import java.util.Map;

public interface OrderService {
    String createTradeNo(String tokenUserId);

    Map<String, Object> getTradeData(String tokenUserId);

    Long saveOrderInfo(OrderInfo orderInfo);

    public Boolean checkTradeNo(String userId,String tradeNo);
}
