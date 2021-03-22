package com.xuanhe.gmall.payment.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.xuanhe.gmall.model.enums.OrderStatus;
import com.xuanhe.gmall.model.enums.PaymentStatus;
import com.xuanhe.gmall.model.enums.PaymentType;
import com.xuanhe.gmall.model.order.OrderInfo;
import com.xuanhe.gmall.model.payment.PaymentInfo;
import com.xuanhe.gmall.order.feign.OrderFeignClient;
import com.xuanhe.gmall.payment.config.AlipayConfig;
import com.xuanhe.gmall.payment.service.AlipayService;
import com.xuanhe.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class AlipayServiceImpl implements AlipayService {
    @Autowired
    AlipayClient alipayClient;
    @Autowired
    OrderFeignClient orderFeignClient;
    @Autowired
    PaymentService paymentService;


    @Override
    public String createAlipay(Long orderId) throws AlipayApiException {
        //获取订单
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderId);
        //保存交易记录
        paymentService.savePaymentInfo(orderInfo, PaymentType.ALIPAY.name());
        //创建支付宝支付请求
        AlipayTradePagePayRequest alipayTradePagePayRequest = new AlipayTradePagePayRequest();
        alipayTradePagePayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayTradePagePayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);
        AlipayTradeQueryResponse alipayTradeQueryResponse=null;
        //封装请求参数
        Map<String,Object> map= new HashMap<>();
        //商户订单号
        map.put("out_trade_no",orderInfo.getOutTradeNo());
        map.put("product_code","FAST_INSTANT_TRADE_PAY");
        map.put("total_amount",new BigDecimal("0.01"));
        map.put("subject","iphone11 128G");
        //设置请求参数
        alipayTradePagePayRequest.setBizContent(JSONObject.toJSONString(map));
        //调用sdk生成表单
        AlipayTradePagePayResponse alipayTradePagePayResponse= alipayClient.pageExecute(alipayTradePagePayRequest);
        String body = alipayTradePagePayResponse.getBody();
        return body;
    }

    @Override
    public Map<String, Object> queryStatus(String outTradeNo) {
        AlipayTradeQueryRequest alipayTradeQueryRequest = new AlipayTradeQueryRequest();
        Map<String,Object> map= new HashMap<>();
        map.put("out_trade_no",outTradeNo);
        alipayTradeQueryRequest.setBizContent(JSONObject.toJSONString(map));
        AlipayTradeQueryResponse response=null;
        try {
            response=alipayClient.execute(alipayTradeQueryRequest);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        Map<String, Object> result = null;
        if (response.isSuccess()){
            result = new HashMap<>();
            System.out.println("查询接口调用成功");
            String tradeStatus=response.getTradeStatus();
            result.put("trade_status",tradeStatus);
        }else {
            System.out.println("调用失败");
        }
        return result;
    }

    @Override
    public boolean closePay(Long orderId) throws AlipayApiException {
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderId);
        if (orderInfo==null||!orderInfo.getOrderStatus().equals(OrderStatus.PAID.name())){
            return false;
        }
        AlipayTradeRefundRequest alipayTradeRefundRequest = new AlipayTradeRefundRequest();
        Map<Object, Object> map = new HashMap<>();
        map.put("out_trade_no",orderInfo.getOutTradeNo());
        map.put("refund_amount",new BigDecimal("0.01"));
        alipayTradeRefundRequest.setBizContent(JSONObject.toJSONString(map));
        AlipayTradeRefundResponse response = null;
        try {
            response = alipayClient.execute(alipayTradeRefundRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (response.isSuccess()&&response.getMsg().equals("Success")) {
            // 更新交易记录 ： 关闭
            paymentService.updateStatus(orderInfo.getOutTradeNo(), PaymentStatus.ClOSED.name());
            orderInfo.setOrderStatus(OrderStatus.CLOSED.name());
            orderFeignClient.updateByOutTradeNo(orderInfo);
            return true;
        } else {
            return false;
        }
    }
}
