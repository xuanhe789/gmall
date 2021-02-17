package com.xuanhe.gmall.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.model.product.SkuInfo;
import com.xuanhe.gmall.product.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin/product")
@RestController
public class SkuApiController {
    @Autowired
    SkuService skuService;

    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        boolean result=skuService.saveSkuInfo(skuInfo);
        return Result.ok(true);
    }

    @GetMapping("/list/{page}/{limit}")
    public Result getList(@PathVariable("page") Integer page,@PathVariable("limit") Integer limit){
        IPage<SkuInfo> iPage=new Page<>(page,limit);
        skuService.getList(iPage);
        return Result.ok(iPage);
    }

    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId){
        boolean result=skuService.onSale(skuId);
        return Result.ok(true);
    }

    @GetMapping("/cancelSale/{skuId}")
    public Result upSale(@PathVariable("skuId") Long skuId){
        boolean result=skuService.upSale(skuId);
        return Result.ok(true);
    }
}
