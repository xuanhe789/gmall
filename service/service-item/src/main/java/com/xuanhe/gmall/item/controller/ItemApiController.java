package com.xuanhe.gmall.item.controller;

import com.alibaba.fastjson.JSONObject;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.item.service.ItemService;
import com.xuanhe.gmall.product.feign.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/item")
@RestController
public class ItemApiController {
    @Autowired
    ItemService itemService;

    @GetMapping("/getItem/{skuId}")
    Result<Map<String,Object>> getItem(@PathVariable Long skuId){
        Map<String,Object> map = itemService.getItem(skuId);
        return Result.ok(map);
    }
    @GetMapping("/getCategoryList")
    List<JSONObject> getCategoryList(){
        return itemService.getCategoryList();
    }
}
