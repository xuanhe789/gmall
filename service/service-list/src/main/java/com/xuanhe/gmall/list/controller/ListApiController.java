package com.xuanhe.gmall.list.controller;

import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.list.service.ListService;
import com.xuanhe.gmall.model.list.Goods;
import com.xuanhe.gmall.model.list.SearchParam;
import com.xuanhe.gmall.model.list.SearchResponseVo;
import com.xuanhe.gmall.product.feign.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/list")
public class ListApiController {
    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    ListService listService;
    @GetMapping("/inner/createGoodsIndex")
    public Result createGoodsMapping(){
        elasticsearchRestTemplate.createIndex(Goods.class);
        elasticsearchRestTemplate.putMapping(Goods.class);
        return Result.ok();
    }
//    @GetMapping("/onSale/{skuId}")
//    public Result onsale(@PathVariable("skuId") Long skuId){
//        listService.onSale(skuId);
//        return Result.ok();
//    }
//
//    @GetMapping("/cancelSale/{skuId}")
//    public Result cancelSale(@PathVariable("skuId") Long skuId){
//        listService.cancelSale(skuId);
//        return Result.ok();
//    }

    @GetMapping("/incrHotScore/{skuId}")
    public Result incrHotScore(@PathVariable("skuId") Long skuId){
        listService.incrHotScore(skuId);
        return Result.ok();
    }

    @PostMapping("/api/list")
    public Result list(@RequestBody SearchParam listParam) throws IOException {
        SearchResponseVo search = listService.search(listParam);
        return Result.ok(search);
    }
}
