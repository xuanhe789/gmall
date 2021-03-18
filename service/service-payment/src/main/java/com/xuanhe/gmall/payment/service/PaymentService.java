package com.xuanhe.gmall.payment.service;

import com.xuanhe.gmall.model.order.OrderInfo;
import com.xuanhe.gmall.model.payment.PaymentInfo;

public interface PaymentService {

    void savePaymentInfo(OrderInfo orderInfo, String paymentType);

    void updatePayment(PaymentInfo paymentInfo);

    void sendMessageQuery(String outTradeNo, Integer count,long delayTime);
}
