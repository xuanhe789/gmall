package com.xuanhe.gmall.payment.controller;

import com.alipay.api.AlipayApiException;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.model.enums.PaymentStatus;
import com.xuanhe.gmall.model.order.OrderInfo;
import com.xuanhe.gmall.model.payment.PaymentInfo;
import com.xuanhe.gmall.order.feign.OrderFeignClient;
import com.xuanhe.gmall.payment.service.AlipayService;
import com.xuanhe.gmall.payment.service.PaymentService;
import com.xuanhe.gmall.rabbit.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentApiController {
    @Autowired
    RabbitService rabbitService;
    @Autowired
    AlipayService alipayService;
    @Autowired
    PaymentService paymentService;
    @Autowired
    OrderFeignClient orderFeignClient;

    /*
    * 提交订单号生成支付宝支付界面
    * */
    @GetMapping("/alipay/submit/{orderId}")
    public String submit(@PathVariable("orderId") Long orderId) throws AlipayApiException {
        String alipay = alipayService.createAlipay(orderId);
        //生成支付页面时，开始查询订单的支付情况
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderId);
        paymentService.sendMessageQuery(orderInfo.getOutTradeNo(),0,30);
        return alipay;
    }

    /**
     * 支付宝回调
     * @return
     */
    @RequestMapping("/alipay/callback/return")
    public String callBack(HttpServletRequest request) {
        //待做，根据返回的请求参数修改支付订单的状态等
        String out_trade_no = request.getParameter("out_trade_no");
        String trade_no = request.getParameter("trade_no");
        String callback_content = request.getQueryString();

        // 根据支付宝回调结果更新支付服务
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(out_trade_no);
        paymentInfo.setPaymentStatus(PaymentStatus.PAID.toString());
        paymentInfo.setTradeNo("trade_no");
        paymentInfo.setCallbackContent(callback_content);
        paymentInfo.setCallbackTime(new Date());
        paymentService.paySucess(paymentInfo);
        // 同步回调给用户展示信息
        return "<form action=\"http://payment.gmall.com/paySuccess\">\n" +
                "</form>\n" +
                "<script>\n" +
                "\tdocument.forms[0].submit();\n" +
                "</script>";
    }
    /*
    * 退款
    * */
    @PostMapping("/refund/{orderId}")
    @ResponseBody
    public Result refund(@PathVariable(value = "orderId")Long orderId) throws AlipayApiException {
        Boolean flag = alipayService.closePay(orderId);
        if (flag){
            return Result.ok();
        }
        Result fail = Result.fail();
        return fail;
    }
}
