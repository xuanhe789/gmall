package com.xuanhe.gmall.product.service;

import com.alibaba.fastjson.JSONObject;
import com.xuanhe.gmall.model.product.BaseCategoryView;

import java.util.List;

public interface BaseCategoryViewService {
    BaseCategoryView getBaseCategoryView(Long category3Id);

    List<JSONObject> getCategoryList();
}
