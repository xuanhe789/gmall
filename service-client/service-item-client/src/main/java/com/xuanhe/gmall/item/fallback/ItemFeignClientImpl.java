package com.xuanhe.gmall.item.fallback;

import com.alibaba.fastjson.JSONObject;
import com.xuanhe.gmall.item.feign.ItemFeignClient;
import com.xuanhe.gmall.common.result.Result;

import java.util.List;

public class ItemFeignClientImpl implements ItemFeignClient {
    @Override
    public Result getItem(Long skuId) {
        return Result.fail();
    }

    @Override
    public List<JSONObject> getCategoryList() {
        return (List<JSONObject>) Result.fail();
    }
}
