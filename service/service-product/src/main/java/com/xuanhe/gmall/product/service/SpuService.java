package com.xuanhe.gmall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuanhe.gmall.model.product.BaseSaleAttr;
import com.xuanhe.gmall.model.product.SpuImage;
import com.xuanhe.gmall.model.product.SpuInfo;
import com.xuanhe.gmall.model.product.SpuSaleAttr;

import java.util.List;

public interface SpuService {
    IPage<SpuInfo> getSpuList(IPage page, Long category3Id);

    List<BaseSaleAttr> getBaseSaleAttrList();

    Integer saveSpuInfo(SpuInfo spuInfo);

    List<SpuImage> getSpuImageListBySpuId(Long spuId);

    List<SpuSaleAttr> getspuSaleAttrListBySpuId(Long spuId);
}
