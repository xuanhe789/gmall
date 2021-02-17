package com.xuanhe.gmall.item.fallback;

import com.xuanhe.gmall.item.feign.ItemFeignClient;
import com.xuanhe.gmall.common.result.Result;

public class ItemFeignClientImpl implements ItemFeignClient {
    @Override
    public Result getItem(Long skuId) {
        return Result.fail();
    }
}
