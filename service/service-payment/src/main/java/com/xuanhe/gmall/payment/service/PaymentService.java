package com.xuanhe.gmall.payment.service;

import com.xuanhe.gmall.model.order.OrderInfo;

public interface PaymentService {

    void savePaymentInfo(OrderInfo orderInfo, String paymentType);
}
