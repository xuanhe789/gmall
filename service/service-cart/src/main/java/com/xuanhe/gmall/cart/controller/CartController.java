package com.xuanhe.gmall.cart.controller;

import com.alibaba.fastjson.JSONObject;
import com.xuanhe.gmall.cart.service.CartService;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.model.cart.CartInfo;
import com.xuanhe.gmall.model.user.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    CartService cartService;
    @PostMapping("/addToCart/{skuId}/{skuNum}")
    public Result addToCart(@PathVariable("skuId") Long skuId,
                            @PathVariable("skuNum") Integer skuNum,
                            HttpServletRequest request) {
        String userId=getTokenUserId(request);
        if (!StringUtils.isEmpty(userId)){
            cartService.addToCart(skuId, userId, skuNum,false);
        }
        else {
            String userTempId=getUserTempId(request);
            cartService.addToCart(skuId,userTempId,skuNum,true);
        }
        return Result.ok();
    }
    /**
     * 查询购物车
     *
     * @param request
     * @return
     */
    @GetMapping("cartList")
    public Result getCartList(HttpServletRequest request){
        String userId = getTokenUserId(request);
        String userTempId = getUserTempId(request);
        List<CartInfo> cartInfoList=cartService.getCartList(userId,userTempId);
        return Result.ok(cartInfoList);
    }
    /*
    * 更改购物项勾选状态
    * */
    @GetMapping("checkCart/{skuId}/{isChecked}")
    public Result checkCart(@PathVariable("skuId") Long skuId, @PathVariable("isChecked") Integer isChecked, HttpServletRequest request){
        String userId = getTokenUserId(request);
        if (!StringUtils.isEmpty(userId)){
            cartService.checkCart(userId,skuId,isChecked,false);
        }else {
            String userTempId = getUserTempId(request);
            cartService.checkCart(userTempId,skuId,isChecked,true);
        }
        return Result.ok();
    }
    /**
     * 删除
     *
     * @param skuId
     * @param request
     * @return
     */
    @DeleteMapping("deleteCart/{skuId}")
    public Result deleteCart(@PathVariable("skuId") Long skuId,
                             HttpServletRequest request) {
// 如何获取userId
        String userId = getTokenUserId(request);
        if (!StringUtils.isEmpty(userId)){
            cartService.deleteCartItemByUserId(skuId, userId,false);
        }else {
            String userTempId = getUserTempId(request);
            cartService.deleteCartItemByUserId(skuId,userTempId,true);
        }
        return Result.ok();
    }

//    public String getUserId(HttpServletRequest request) {
//        //获取用户信息
//        String userInfoString = request.getHeader("userInfo");
//        String token = request.getHeader("token");
//        System.out.println(token);
//        UserInfo userInfo = JSONObject.parseObject(userInfoString, UserInfo.class);
//        String userId=null;
//        if (userInfo==null) {
//            //获取用户临时id
//            String userTempId = request.getHeader("userTempId");
//            System.out.println(userTempId);
//            userId=userTempId;
//        }else {
//            userId=String.valueOf(userInfo.getId());
//        }
//        return userId;
//    }

    public String getUserTempId(HttpServletRequest request){
        String userTempId = request.getHeader("userTempId");
        System.out.println(userTempId);
        return userTempId;
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
