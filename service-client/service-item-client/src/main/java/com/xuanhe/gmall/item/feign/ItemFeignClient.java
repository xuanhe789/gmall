package com.xuanhe.gmall.item.feign;

import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.item.fallback.ItemFeignClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@Component
@FeignClient(value = "service-item")
public interface ItemFeignClient {

    @GetMapping("/api/item/getItem/{skuId}")
    Result<Map<String,Object>> getItem(@PathVariable("skuId") Long skuId);
}


