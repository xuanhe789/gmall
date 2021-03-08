package com.xuanhe.gmall.order.controller;

import com.xuanhe.gmall.cart.feign.CartFeignClient;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.order.service.OrderService;
import com.xuanhe.gmall.user.feign.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
@RestController
@RequestMapping("/api/order")
public class OrderApiController {
    @Autowired
    UserFeignClient userFeignClient;
    @Autowired
    CartFeignClient cartFeignClient;
    @Autowired
    OrderService orderService;

    /**
     * 确认订单
     * @param request
     * @return
     */
    @GetMapping("/auth/trade")
    public Result<Map<String, Object>> trade(HttpServletRequest request) {
        return null;
    }
}
