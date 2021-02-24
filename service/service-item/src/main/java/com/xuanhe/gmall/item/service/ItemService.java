package com.xuanhe.gmall.item.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

public interface ItemService {
    Map<String, Object> getItem(Long skuId);

    List<JSONObject> getCategoryList();
}
