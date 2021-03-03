package com.xuanhe.gmall.cart.service;

public interface CartService {
    void addToCart(Long skuId, String userId, Integer skuNum);
}
