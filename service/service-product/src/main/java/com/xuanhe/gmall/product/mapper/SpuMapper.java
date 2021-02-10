package com.xuanhe.gmall.product.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xuanhe.gmall.model.product.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SpuMapper {
    List<SpuInfo> getSpuList(@Param("page") Long page,@Param("limit") Long limit, @Param("id") Long category3Id);

    long getTotal(Long category3Id);

    List<BaseSaleAttr> getBaseSaleAttrList();

    Integer saveSpuInfo(SpuInfo spuInfo);

    Integer savespuImageList(List<SpuImage> spuImageList);

    Integer spuSaleAttrList(List<SpuSaleAttr> spuSaleAttrList);

    Integer savespuSaleAttrValueList(List<SpuSaleAttrValue> spuSaleAttrValueList);

    List<SpuImage> getSpuImageListBySpuId(Long spuId);

    List<SpuSaleAttr> getspuSaleAttrListBySpuId(Long spuId);
}
