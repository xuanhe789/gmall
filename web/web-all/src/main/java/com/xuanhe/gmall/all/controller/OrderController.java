package com.xuanhe.gmall.all.controller;

import com.xuanhe.gmall.order.feign.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class OrderController {
    @Autowired
    private OrderFeignClient orderFeignClient;

    /**
     * 确认订单
     * @param model
     * @return
     */
    @GetMapping("trade.html")
    public String trade(Model model) {

        Map<String, Object> result = orderFeignClient.trade().getData();
        model.addAllAttributes(result);
        return "order/trade";
    }

    @GetMapping("myOrder.html")
    public String orderList(){
        return "order/myOrder";
    }
}
