package com.xuanhe.gmall.payment.service;

import com.xuanhe.gmall.model.order.OrderInfo;
import com.xuanhe.gmall.model.payment.PaymentInfo;

public interface PaymentService {

    void savePaymentInfo(OrderInfo orderInfo, String paymentType);

    void paySucess(PaymentInfo paymentInfo);

    void sendMessageQuery(String outTradeNo, Integer count,long delayTime);

    void updateStatusByOutTradeNo(String out_trade_no);

    void updateStatus(String outTradeNo, String status);
}
