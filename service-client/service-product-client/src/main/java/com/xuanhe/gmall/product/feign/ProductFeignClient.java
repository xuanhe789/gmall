package com.xuanhe.gmall.product.feign;

import com.alibaba.fastjson.JSONObject;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.model.list.Goods;
import com.xuanhe.gmall.model.product.BaseCategoryView;
import com.xuanhe.gmall.model.product.SkuImage;
import com.xuanhe.gmall.model.product.SkuInfo;
import com.xuanhe.gmall.model.product.SpuSaleAttr;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
@FeignClient(value = "product-service")
public interface ProductFeignClient {
    /**
     * 根据skuId获取sku信息
     *
     * @param skuId
     * @return
     */
    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId);

    @GetMapping("/api/product/inner/getSkuPrice/{skuId}")
    BigDecimal getSkuPrice(@PathVariable("skuId") Long skuId);

    @GetMapping("/api/product/inner/getSpuSaleAttrList/{spuId}/{skuId}")
    List<SpuSaleAttr> getSpuSaleAttrList(@PathVariable("spuId") Long spuId,@PathVariable("skuId") Long skuId);

    @GetMapping("/api/product/inner/getSkuImages/{skuId}")
    List<SkuImage> getSkuImages(@PathVariable("skuId") Long skuId);

    @GetMapping("/api/product/inner/getBaseCategoryView/{category3Id}")
    BaseCategoryView getBaseCategoryView(@PathVariable("category3Id") Long category3Id);

    @GetMapping("/api/product/inner/getSkuValueMap/{spuId}")
    Map<String,Long> getSkuValueMap(@PathVariable("spuId") Long spuId);

    @GetMapping("/api/product/inner/getGoodsBySkuId/{skuId}")
    Goods getGoodsBySkuId(@PathVariable("skuId") Long skuId);

    @GetMapping("/api/product/inner/getCategoryList")
    List<JSONObject> getCategoryList();
}
