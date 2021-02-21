package com.xuanhe.gmall.list.controller;

import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.model.list.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/list")
public class ListApiController {
    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;
    @GetMapping("inner/createGoodsIndex")
    public Result createGoodsMapping(){
        elasticsearchRestTemplate.createIndex(Goods.class);
        elasticsearchRestTemplate.putMapping(Goods.class);
        return Result.ok();
    }
}
