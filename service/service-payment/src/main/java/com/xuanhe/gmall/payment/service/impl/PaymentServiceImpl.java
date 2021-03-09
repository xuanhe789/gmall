package com.xuanhe.gmall.payment.service.impl;

import com.xuanhe.gmall.payment.mapper.PaymentMapper;
import com.xuanhe.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    PaymentMapper paymentMapper;
}
