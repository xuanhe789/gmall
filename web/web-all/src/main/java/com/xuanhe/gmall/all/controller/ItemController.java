package com.xuanhe.gmall.all.controller;

import com.xuanhe.gmall.item.feign.ItemFeignClient;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.product.feign.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class ItemController {

    @Autowired
    ItemFeignClient itemFeignClient;

    @GetMapping("{skuId}.html")
    public String getItem(@PathVariable Long skuId, Model model){
        Result<Map<String, Object>> item =itemFeignClient.getItem(skuId);
        model.addAllAttributes(item.getData());
        return "item/index";

    }


}
