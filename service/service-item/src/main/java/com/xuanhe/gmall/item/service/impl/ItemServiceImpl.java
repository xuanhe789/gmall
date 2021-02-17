package com.xuanhe.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.item.service.ItemService;
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

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    ProductFeignClient productFeignClient;
    @Override
    public Map<String, Object> getItem(Long skuId) {
        SkuInfo skuInfo = (SkuInfo) productFeignClient.getSkuInfo(skuId);
        BigDecimal price=productFeignClient.getSkuPrice(skuId);
        List<SpuSaleAttr> spuSaleAttrList=productFeignClient.getSpuSaleAttrList(skuInfo.getSpuId(),skuId);
        List<SkuImage> skuImages=productFeignClient.getSkuImages(skuId);
        BaseCategoryView baseCategoryView=productFeignClient.getBaseCategoryView(skuInfo.getCategory3Id());
        Map<String, Long> skuValueMap = productFeignClient.getSkuValueMap(skuInfo.getSpuId());
        Map<String, Object> result=new HashMap<>();
        skuInfo.setSkuImageList(skuImages);
        result.put("skuInfo",skuInfo);
        result.put("price",price);
        result.put("spuSaleAttrList",spuSaleAttrList);
        result.put("categoryView",baseCategoryView);
        result.put("valuesSkuJson", JSONObject.toJSONString(skuValueMap));
        return result;
    }
}
