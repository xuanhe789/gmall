package com.xuanhe.gmall.cart.feign;

import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.model.cart.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(value ="service-cart" )
public interface CartFeignClient {
    @PostMapping("/api/cart/addToCart/{skuId}/{skuNum}")
    Result addToCart(@PathVariable("skuId") Long skuId, @PathVariable("skuNum") Integer skuNum);

    /**
     * 根据用户Id 查询购物车列表
     * @param userId
     * @return
     */
    @GetMapping("/api/cart/getCartCheckedList/{userId}")
    List<CartInfo> getCartCheckedList(@PathVariable("userId") String userId);

    //下完单后删除购物车数据
    @GetMapping("/deleteAllCart/{userId}")
    public Result<Boolean> deleteAllCart(@PathVariable("userId") String userId);
}
