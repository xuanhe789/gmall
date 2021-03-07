package com.xuanhe.gmall.cart.service;

import com.xuanhe.gmall.model.cart.CartInfo;

import java.util.List;

public interface CartService {
    void addToCart(Long skuId, String userId, Integer skuNum,Boolean isTempId);

    List<CartInfo> getCartList(String userId,String userTempId);

    void checkCart(String userId, Long skuId, Integer isChecked,Boolean isTempId);

    void deleteCartItemByUserId(Long skuId, String userId, Boolean isTempId);


}
