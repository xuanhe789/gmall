package com.xuanhe.gmall.product.service;

import com.xuanhe.gmall.model.product.BaseCategory1;
import com.xuanhe.gmall.model.product.BaseCategory2;
import com.xuanhe.gmall.model.product.BaseCategory3;

import java.util.List;

public interface CategoryService {
    List<BaseCategory1> getCategory1();

    List<BaseCategory2> getCategory2(Long id);

    List<BaseCategory3> getCategory3(Long id);
}
