package com.xuanhe.gmall.product.controller;

import com.alibaba.fastjson.JSONObject;
import com.xuanhe.gmall.model.product.BaseCategory1;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.model.product.BaseCategory2;
import com.xuanhe.gmall.model.product.BaseCategory3;
import com.xuanhe.gmall.product.service.CategoryService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequestMapping("/admin/product")
@RestController
public class CategoryApiController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("/getCategory1")
    public Result getCategory1(){
        List<BaseCategory1> category1s=categoryService.getCategory1();
        Result<List<BaseCategory1>> result = Result.ok(category1s);
        return result;
    }

    @GetMapping("/getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable("category1Id") Long id){
        if (id==null){
            Result fail = Result.fail();
            fail.setMessage("分类id不能为空");
            return fail;
        }
        List<BaseCategory2> category2s=categoryService.getCategory2( id);
        return Result.ok(category2s);
    }

    @GetMapping("/getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable("category2Id") Long id){
        if (id==null){
            Result fail = Result.fail();
            fail.setMessage("分类id不能为空");
            return fail;
        }
        List<BaseCategory3> category3s=categoryService.getCategory3(id);
        return Result.ok(category3s);
    }
}
