package com.xuanhe.gmall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xuanhe.gmall.model.list.Goods;
import com.xuanhe.gmall.model.product.BaseCategoryView;
import com.xuanhe.gmall.model.product.SkuImage;
import com.xuanhe.gmall.model.product.SkuInfo;
import com.xuanhe.gmall.model.product.SpuSaleAttr;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SkuService {
    boolean saveSkuInfo(SkuInfo skuInfo);

    void getList(IPage<SkuInfo> iPage);

    boolean onSale(Long skuId);

    boolean upSale(Long skuId);

    SkuInfo getSkuInfoById(Long skuId);

    BigDecimal getSkuPriceBySkuId(Long skuId);

    List<SpuSaleAttr> getSpuSaleAttrListBySpuId(Long spuId,Long skuId);

    List<SkuImage> getSkuImagesBySkuId(Long skuId);


    Map<String, Long> getSkuValueMap(Long spuId);

    Goods getGoodsBySkuId(Long skuId);
}
