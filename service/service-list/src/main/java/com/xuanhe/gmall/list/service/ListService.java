package com.xuanhe.gmall.list.service;

public interface ListService {
    void onSale(Long skuId);

    void cancelSale(Long skuId);

    void incrHotScore(Long skuId);
}
