package com.xuanhe.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import com.xuanhe.gmall.item.feign.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ListController {
    @Autowired
    ItemFeignClient itemFeignClient;
    @GetMapping("index.html")
    public String index(){
        List<JSONObject> jsonObjects=itemFeignClient.getCategoryList();
        return "index";
    }
}
