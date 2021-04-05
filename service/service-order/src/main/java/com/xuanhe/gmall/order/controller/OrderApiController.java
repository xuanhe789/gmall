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
        Map<String, Object> result =orderService.getTradeData(tokenUserId);
        return Result.ok(result);
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
        // 验证流水号
        String tradeNo = request.getParameter("tradeNo");
        Boolean checked = orderService.checkTradeNo(userId, tradeNo);
        if (!checked){
            Result<Object> fail = Result.fail();
            fail.setMessage("流水号异常， 无法提交订单");
            return fail;
        }
        orderInfo.setUserId(Long.parseLong(userId));
        // 验证通过，保存订单！
        Long orderId = orderService.saveOrderInfo(orderInfo);
        return Result.ok(orderId);
    }


    /*
    * 更加订单id获取订单信息
    * */
    @GetMapping("/getOrderInfo/{orderId}")
    OrderInfo getOrderInfo(@PathVariable("orderId") Long orderId){
        if (orderId==null){
            return null;
        }
        OrderInfo orderInfo=orderService.getOrderInfo(orderId);
        return orderInfo;
    }
    @GetMapping("/getOrderInfoByOutTradeNo/{outTradeNo}")
    OrderInfo getOrderInfoByOutTradeNo(@PathVariable("outTradeNo") String outTradeNo){
        if (outTradeNo==null){
            return null;
        }
        OrderInfo orderInfo=orderService.getOrderInfoByOutTradeNo(outTradeNo);
        return orderInfo;
    }

    @PostMapping("/update")
    void updateByOutTradeNo(@RequestBody OrderInfo orderInfo){
        if (orderInfo!=null){
            orderService.update(orderInfo);
        }
    }

    @GetMapping("/getTradeNo/{userId}")
    public String getTradeNo(@PathVariable("userId") String userId){
        return orderService.createTradeNo(userId);
    }

    /**
     * 提交秒杀订单
     * @param orderInfo
     * @return
     */
    @PostMapping("/auth/submitSeckillOrder/{tradeNo}")
    public Result<String> submitSeckillOrder(@RequestBody OrderInfo orderInfo, @PathVariable("tradeNo") String tradeNo) {
        // 验证流水号
        Boolean checked = orderService.checkTradeNo(orderInfo.getUserId()+"", tradeNo);
        if (!checked){
            Result<String> fail = Result.fail();
            fail.setMessage("流水号异常， 无法提交订单");
            return fail;
        }
        // 验证通过，保存订单！
        Long orderId = orderService.saveSeckillOrderInfo(orderInfo);
        return Result.ok(orderId+"");
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
