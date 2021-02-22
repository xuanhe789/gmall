package com.xuanhe.gmall.product.mapper;

import com.xuanhe.gmall.model.list.Goods;
import com.xuanhe.gmall.model.product.SkuAttrValue;
import com.xuanhe.gmall.model.product.SkuImage;
import com.xuanhe.gmall.model.product.SkuInfo;
import com.xuanhe.gmall.model.product.SkuSaleAttrValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SkuMapper {
    Integer saveSkuInfo(SkuInfo skuInfo);

    Integer saveSkuAttrValueList(List<SkuAttrValue> skuAttrValueList);

    Integer saveSkuImageList(List<SkuImage> skuImageList);

    Integer saveSkuSaleAttrValueList(List<SkuSaleAttrValue> skuSaleAttrValueList);

    Integer getTotal();

    List<SkuInfo> getListPage(@Param("start") long l,@Param("limit") long size);

    Integer onSale(Long skuId);

    Integer upSale(Long skuId);

    SkuInfo getSkuInfoById(Long skuId);

    List<SkuImage> getSkuImagesBySkuId(Long skuId);

    List<Map> getSkuValueMap(Long spuId);

    Goods getGoodsBySkuId(Long skuId);
}
