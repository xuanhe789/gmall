package com.xuanhe.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.item.service.ItemService;
import com.xuanhe.gmall.list.feign.ListFeignClient;
import com.xuanhe.gmall.model.product.BaseCategoryView;
import com.xuanhe.gmall.model.product.SkuImage;
import com.xuanhe.gmall.model.product.SkuInfo;
import com.xuanhe.gmall.model.product.SpuSaleAttr;
import com.xuanhe.gmall.product.feign.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    ProductFeignClient productFeignClient;
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    ListFeignClient listFeignClient;
    @Override
    public Map<String, Object> getItem(Long skuId) {
        long start = System.currentTimeMillis();
        Map<String, Object> result=new HashMap<>();
        //异步更新热度值
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                listFeignClient.incrHotScore(skuId);
            }
        },threadPoolExecutor);
        CompletableFuture<SkuInfo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(new Supplier<SkuInfo>() {
            @Override
            public SkuInfo get() {
                SkuInfo skuInfo = (SkuInfo) productFeignClient.getSkuInfo(skuId);
                result.put("skuInfo",skuInfo);
                return skuInfo;
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> images = skuInfoCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                List<SkuImage> skuImages = productFeignClient.getSkuImages(skuId);
                skuInfo.setSkuImageList(skuImages);
            }
        }, threadPoolExecutor);

        CompletableFuture<Void> categoryView = skuInfoCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                BaseCategoryView baseCategoryView = productFeignClient.getBaseCategoryView(skuInfo.getCategory3Id());
                result.put("categoryView", baseCategoryView);
            }
        }, threadPoolExecutor);
        CompletableFuture<Void> spuSaleAttrList = skuInfoCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                List<SpuSaleAttr> spuSaleAttrList = productFeignClient.getSpuSaleAttrList(skuInfo.getSpuId(), skuId);
                result.put("spuSaleAttrList", spuSaleAttrList);
            }
        }, threadPoolExecutor);
        CompletableFuture<Void> valuesSkuJson = skuInfoCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                Map<String, Long> skuValueMap = productFeignClient.getSkuValueMap(skuInfo.getSpuId());
                result.put("valuesSkuJson", JSONObject.toJSONString(skuValueMap));
            }
        }, threadPoolExecutor);
        CompletableFuture<Void> price = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                BigDecimal price = productFeignClient.getSkuPrice(skuId);
                result.put("price", price);
            }
        }, threadPoolExecutor);
        CompletableFuture.allOf(skuInfoCompletableFuture,images,price,categoryView,valuesSkuJson,spuSaleAttrList).join();
        long end = System.currentTimeMillis();
        System.out.println("耗时:"+(end-start));
        return result;
    }
}
