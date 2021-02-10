package com.xuanhe.gmall.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.model.product.BaseSaleAttr;
import com.xuanhe.gmall.model.product.SpuImage;
import com.xuanhe.gmall.model.product.SpuInfo;
import com.xuanhe.gmall.model.product.SpuSaleAttr;
import com.xuanhe.gmall.product.service.SpuService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin/product")
@RestController
public class SpuApiController {
    @Autowired
    SpuService spuService;
    @GetMapping("/{page}/{limit}")
    public Result spuList(@PathVariable("page") Integer page, @PathVariable("limit")Integer limit,Long category3Id){
        IPage<SpuInfo> spuInfoIPage = new Page<>();
        spuInfoIPage.setCurrent(page);
        spuInfoIPage.setSize(limit);
        spuInfoIPage= spuService.getSpuList(spuInfoIPage,category3Id);
        return Result.ok(spuInfoIPage);
    }

    @GetMapping("/baseSaleAttrList")
    public Result baseSaleAttrList(){
        List<BaseSaleAttr> baseSaleAttrs= spuService.getBaseSaleAttrList();
        return Result.ok(baseSaleAttrs);
    }

    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        Integer result= spuService.saveSpuInfo(spuInfo);
        if (result<1){
            return Result.fail();
        }
        return Result.ok();
    }

    @GetMapping("/spuImageList/{spuId}")
    public Result spuImageList(@PathVariable("spuId") Long spuId){
        List<SpuImage> spuImages= spuService.getSpuImageListBySpuId(spuId);
        return Result.ok(spuImages);
    }

    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable("spuId") Long spuId){
        List<SpuSaleAttr> spuSaleAttrs= spuService.getspuSaleAttrListBySpuId(spuId);
        return Result.ok(spuSaleAttrs);
    }
}
