package com.xuanhe.gmall.cart.controller;

import com.alibaba.fastjson.JSONObject;
import com.xuanhe.gmall.cart.service.CartService;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.model.user.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CartController {
    @Autowired
    CartService cartService;
    @PostMapping("/addCart/{skuId}/{skuNum}")
    public Result addToCart(@PathVariable("skuId") Long skuId,
                            @PathVariable("skuNum") Integer skuNum,
                            HttpServletRequest request) {
        //获取用户信息
        String userInfoString = request.getHeader("userInfo");
        String token = request.getHeader("token");
        System.out.println(token);
        UserInfo userInfo = JSONObject.parseObject(userInfoString, UserInfo.class);
        String userId=null;
        if (userInfo==null) {
            //获取用户临时id
            String userTempId = request.getHeader("userTempId");
            System.out.println(userTempId);
            userId=userTempId;
        }else {
            userId=String.valueOf(userId);
        }
        cartService.addToCart(skuId, userId, skuNum);
        return Result.ok();
    }
}
