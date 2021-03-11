package com.xuanhe.gmall.payment.service.impl;

import com.xuanhe.gmall.model.enums.PaymentStatus;
import com.xuanhe.gmall.model.order.OrderInfo;
import com.xuanhe.gmall.model.payment.PaymentInfo;
import com.xuanhe.gmall.payment.mapper.PaymentMapper;
import com.xuanhe.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    PaymentMapper paymentMapper;

    @Override
    public void savePaymentInfo(OrderInfo orderInfo, String paymentType) {
        Integer count=paymentMapper.selectByOrderIdAndPaymentType(orderInfo.getId(),paymentType);
        if (count>0)
            return;
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID.name());
        paymentInfo.setSubject(orderInfo.getTradeBody());
        //paymentInfo.setSubject("test");
        paymentInfo.setTotalAmount(orderInfo.getTotalAmount());
        paymentMapper.insert(paymentInfo);
    }
}
