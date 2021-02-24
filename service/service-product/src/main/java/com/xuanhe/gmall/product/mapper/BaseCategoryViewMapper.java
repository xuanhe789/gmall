package com.xuanhe.gmall.product.mapper;

import com.xuanhe.gmall.model.product.BaseCategoryView;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BaseCategoryViewMapper {
    BaseCategoryView getBaseCategoryView(Long category3Id);

    List<BaseCategoryView> getAll();
}
