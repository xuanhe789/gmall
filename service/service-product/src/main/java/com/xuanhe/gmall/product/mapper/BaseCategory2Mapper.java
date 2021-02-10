package com.xuanhe.gmall.product.mapper;

import com.xuanhe.gmall.model.product.BaseCategory2;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BaseCategory2Mapper {

    List<BaseCategory2> selectCategory2List(Long id);
}
