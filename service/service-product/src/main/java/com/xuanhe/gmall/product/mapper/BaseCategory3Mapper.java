package com.xuanhe.gmall.product.mapper;

import com.xuanhe.gmall.model.product.BaseCategory3;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BaseCategory3Mapper {
    List<BaseCategory3> getCategory3List(Long id);
}
