package com.xuanhe.gmall.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.xuanhe.gmall.cart.feign.CartFeignClient;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.model.order.OrderInfo;
import com.xuanhe.gmall.model.user.UserInfo;
import com.xuanhe.gmall.order.service.OrderService;
import com.xuanhe.gmall.user.feign.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/api/order")
public class OrderApiController {
    @Autowired
    OrderService orderService;

    /**
     * 确认订单
     * @param request
     * @return
     */
    @GetMapping("/auth/trade")
    public Result<Map<String, Object>> trade(HttpServletRequest request) {
        String tokenUserId = getTokenUserId(request);
        Map<String, Object> result =orderService.getTradeData(tokenUserId)
        return null;
    }

    /**
     * 提交订单
     * @param orderInfo
     * @param request
     * @return
     */
    @PostMapping("/auth/submitOrder")
    public Result submitOrder(@RequestBody OrderInfo orderInfo, HttpServletRequest request) {
        // 获取到用户Id
        String userId = getTokenUserId(request);
        orderInfo.setUserId(Long.parseLong(userId));
        // 验证通过，保存订单！
        Long orderId = orderService.saveOrderInfo(orderInfo);
        return Result.ok(orderId);
    }

    public String getTokenUserId(HttpServletRequest request){
        //获取用户信息
        String userInfoString = request.getHeader("userInfo");
        String token = request.getHeader("token");
        System.out.println(token);
        UserInfo userInfo = JSONObject.parseObject(userInfoString, UserInfo.class);
        String userId=null;
        if (userInfo!=null){
            userId=String.valueOf(userInfo.getId());
        }
        return userId;
    }
}
