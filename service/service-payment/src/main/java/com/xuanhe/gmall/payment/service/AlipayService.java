package com.xuanhe.gmall.payment.service;

import com.alipay.api.AlipayApiException;

import java.util.Map;

public interface AlipayService {
    String createAlipay(Long orderId) throws AlipayApiException;

    Map queryStatus(String outTradeNo);

    boolean closePay(Long orderId) throws AlipayApiException;
}
