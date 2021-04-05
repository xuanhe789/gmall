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
import com.xuanhe.gmall.rabbit.constant.MqConst;
import com.xuanhe.gmall.rabbit.service.RabbitService;
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
    RabbitService rabbitService;
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
        //异步获取skuinfo信息
        CompletableFuture<SkuInfo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(new Supplier<SkuInfo>() {
            @Override
            public SkuInfo get() {
                SkuInfo skuInfo = (SkuInfo) productFeignClient.getSkuInfo(skuId);
                result.put("skuInfo",skuInfo);
                return skuInfo;
            }
        }, threadPoolExecutor);
        //在获取完skuinfo信息后，通过skuinfo的信息，异步获取sku图片信息。这两个异步有先后级关系
        CompletableFuture<Void> images = skuInfoCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                List<SkuImage> skuImages = productFeignClient.getSkuImages(skuId);
                skuInfo.setSkuImageList(skuImages);
            }
        }, threadPoolExecutor);
        //在获取完skuinfo信息后，通过skuinfo的信息，异步获取skuinfo对应的分类信息。这两个异步有先后级关系
        CompletableFuture<Void> categoryView = skuInfoCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                BaseCategoryView baseCategoryView = productFeignClient.getBaseCategoryView(skuInfo.getCategory3Id());
                result.put("categoryView", baseCategoryView);
            }
        }, threadPoolExecutor);
        //在获取完skuinfo信息后，通过skuinfo的信息，异步获取spu所有销售属性和值。这两个异步有先后级关系
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
        //异步获取sku的价格，这个不需要等待skuinfo信息
        CompletableFuture<Void> price = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                BigDecimal price = productFeignClient.getSkuPrice(skuId);
                result.put("price", price);
            }
        }, threadPoolExecutor);
        //下面这行代码的意思是等待下面所有的异步线程执行完毕，主线程才能执行下面代码，
        // 也就是说下面的long end = System.currentTimeMillis();需要等待异步线程执行完毕才能执行
        CompletableFuture.allOf(skuInfoCompletableFuture,images,price,categoryView,valuesSkuJson,spuSaleAttrList).join();
        long end = System.currentTimeMillis();
        System.out.println("耗时:"+(end-start));
        return result;
    }

    @Override
    public List<JSONObject> getCategoryList() {
        return productFeignClient.getCategoryList();
    }

    public void incrHotScore(Long skuId){
        rabbitService.sendMessage(MqConst.EXCHANGE_GOODS_INCREHOT,MqConst.ROUTING_GOODS_INCREHOT,skuId);
    }
}
