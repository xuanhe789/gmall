package com.xuanhe.gmall.all.controller;
import com.xuanhe.gmall.model.order.OrderInfo;
import com.xuanhe.gmall.order.feign.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PaymentController {
    @Autowired
    OrderFeignClient orderFeignClient;

    /**
     * 支付页
     * @param request
     * @return
     */
    @GetMapping("pay.html")
    public String success(HttpServletRequest request, Model model) {
        String orderId = request.getParameter("orderId");
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(Long.parseLong(orderId));
        model.addAttribute("orderInfo", orderInfo);
        return "payment/pay";
    }


    /**
     * 支付成功页
     * @return
     */
    @GetMapping("/paySuccess")
    public String success() {
        return "payment/success";
    }

    /*
    * 退款成功页
    * */
    @GetMapping("refundSuccess.html")
    public String refundSuccess(){
        return "payment/refundSuccess";
    }
}
