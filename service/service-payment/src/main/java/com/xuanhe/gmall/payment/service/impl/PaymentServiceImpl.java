package com.xuanhe.gmall.payment.service.impl;

import com.alipay.api.request.AlipayTradeRefundRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xuanhe.gmall.model.enums.OrderStatus;
import com.xuanhe.gmall.model.enums.PaymentStatus;
import com.xuanhe.gmall.model.order.OrderInfo;
import com.xuanhe.gmall.model.payment.PaymentInfo;
import com.xuanhe.gmall.order.feign.OrderFeignClient;
import com.xuanhe.gmall.payment.mapper.PaymentMapper;
import com.xuanhe.gmall.payment.service.PaymentService;
import com.xuanhe.gmall.rabbit.constant.MqConst;
import com.xuanhe.gmall.rabbit.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    PaymentMapper paymentMapper;
    @Autowired
    RabbitService rabbitService;
    @Autowired
    OrderFeignClient orderFeignClient;

    @Override
    public void savePaymentInfo(OrderInfo orderInfo, String paymentType) {
        Integer count=paymentMapper.selectByOrderIdAndPaymentType(orderInfo.getId(),paymentType);
        if (count>0) {
            return;
        }
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

    @Override
    public void paySucess(PaymentInfo paymentInfo) {
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("out_trade_no",paymentInfo.getOutTradeNo());
        paymentMapper.update(paymentInfo,queryWrapper);
        //修改订单表的支付状态
        OrderInfo orderInfo = orderFeignClient.getOrderInfoByOutTradeNo(paymentInfo.getOutTradeNo());
        orderInfo.setOrderStatus(OrderStatus.PAID.name());
        orderInfo.setProcessStatus(PaymentStatus.PAID.name());
        orderFeignClient.updateByOutTradeNo(orderInfo);
    }

    @Override
    public void sendMessageQuery(String outTradeNo, Integer count,long delayTime) {
        System.out.println("发送检查队列");
        Map<String,Object> map=new HashMap<>();
        map.put("out_trade_no",outTradeNo);
        map.put("count",count);
        map.put("delayTime",delayTime);
        rabbitService.sendDelayMessage(MqConst.ROUTING_ORDER_CANCEL,MqConst.ROUTING_PAYMENT_PAY,map,delayTime);
    }

    @Override
    public void updateStatusByOutTradeNo(String out_trade_no) {
        //修改支付表的支付状态
        paymentMapper.updateStatus(out_trade_no,PaymentStatus.PAID.name());
        //修改订单表的支付状态
        OrderInfo orderInfo = orderFeignClient.getOrderInfoByOutTradeNo(out_trade_no);
        orderInfo.setOrderStatus(OrderStatus.PAID.name());
        orderInfo.setProcessStatus(PaymentStatus.PAID.name());
        orderFeignClient.updateByOutTradeNo(orderInfo);
    }

    @Override
    public void updateStatus(String outTradeNo, String status) {
        paymentMapper.closePayMent(outTradeNo,status);
    }


}
