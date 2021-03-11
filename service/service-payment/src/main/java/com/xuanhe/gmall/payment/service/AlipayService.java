package com.xuanhe.gmall.payment.service;

import com.alipay.api.AlipayApiException;

public interface AlipayService {
    String createAlipay(Long orderId) throws AlipayApiException;
}
