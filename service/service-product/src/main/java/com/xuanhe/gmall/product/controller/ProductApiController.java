package com.xuanhe.gmall.product.controller;

import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.model.list.Goods;
import com.xuanhe.gmall.model.product.BaseCategoryView;
import com.xuanhe.gmall.model.product.SkuImage;
import com.xuanhe.gmall.model.product.SkuInfo;
import com.xuanhe.gmall.model.product.SpuSaleAttr;
import com.xuanhe.gmall.product.service.BaseCategoryViewService;
import com.xuanhe.gmall.product.service.SkuService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
public class ProductApiController {
    @Autowired
    SkuService skuService;
    @Autowired
    BaseCategoryViewService baseCategoryViewService;

    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId){
        SkuInfo skuInfo=skuService.getSkuInfoById(skuId);
        return skuInfo;
    }

    @GetMapping("/api/product/inner/getSkuPrice/{skuId}")
    BigDecimal getSkuPrice(@PathVariable("skuId") Long skuId){
        BigDecimal price=skuService.getSkuPriceBySkuId(skuId);
        return price;
    }

    @GetMapping("/api/product/inner/getSpuSaleAttrList/{spuId}/{skuId}")
    List<SpuSaleAttr> getSpuSaleAttrList(@PathVariable("spuId") Long spuId,@PathVariable("skuId") Long skuId){
        List<SpuSaleAttr> spuSaleAttrList=skuService.getSpuSaleAttrListBySpuId(spuId,skuId);
        return spuSaleAttrList;
    }

    @GetMapping("/api/product/inner/getSkuImages/{skuId}")
    List<SkuImage> getSkuImages(@PathVariable("skuId") Long skuId){
        List<SkuImage> skuImages=skuService.getSkuImagesBySkuId(skuId);
        return skuImages;
    }

    @GetMapping("/api/product/inner/getBaseCategoryView/{category3Id}")
    BaseCategoryView getBaseCategoryView(@PathVariable("category3Id") Long category3Id){
        BaseCategoryView baseCategoryView=baseCategoryViewService.getBaseCategoryView(category3Id);
        return baseCategoryView;
    }

    @GetMapping("/api/product/inner/getSkuValueMap/{spuId}")
    Map<String,Long> getSkuValueMap(@PathVariable("spuId") Long spuId){
        Map<String,Long> result=skuService.getSkuValueMap(spuId);
        return result;
    }


    @GetMapping("/api/product/inner/getGoodsBySkuId/{skuId}")
    Goods getGoodsBySkuId(@PathVariable("skuId") Long skuId){
        Goods goods=skuService.getGoodsBySkuId(skuId);
        return goods;
    }
}
