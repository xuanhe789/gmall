package com.xuanhe.gmall.product.mapper;

import com.xuanhe.gmall.model.product.BaseCategoryView;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BaseCategoryViewMapper {
    BaseCategoryView getBaseCategoryView(Long category3Id);
}
