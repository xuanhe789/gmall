package com.xuanhe.gmall.list.feign;

import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.model.list.SearchParam;
import com.xuanhe.gmall.model.list.SearchResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(value = "service-list",path = "/api/list")
public interface ListFeignClient {
    @GetMapping("/onSale/{skuId}")
    public void onsale(@PathVariable("skuId") Long skuId);

    @GetMapping("/cancelSale/{skuId}")
    public void cancelSale(@PathVariable("skuId") Long skuId);
    @GetMapping("/incrHotScore/{skuId}")
    public Result incrHotScore(@PathVariable("skuId") Long skuId);

    @PostMapping("/api/list")
    Result list(@RequestBody SearchParam listParam);
}
