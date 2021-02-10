package com.xuanhe.gmall.product.controller;

import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.model.product.BaseAttrInfo;
import com.xuanhe.gmall.model.product.BaseAttrValue;
import com.xuanhe.gmall.model.product.BaseSaleAttr;
import com.xuanhe.gmall.product.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
public class AttrApiController {
    @Autowired
    AttrService attrService;

    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result getAttrInfo(@PathVariable("category1Id") Long category1Id,@PathVariable("category2Id") Long category2Id,@PathVariable("category3Id") Long category3Id){
        List<BaseAttrInfo> baseAttrInfos= attrService.getAttrInfoBy3Id(category3Id);
        return Result.ok(baseAttrInfos);
    }

    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        Integer integer = attrService.saveAttrInfo(baseAttrInfo);
        if (integer<1){
            return Result.fail();
        }
        return Result.ok();
    }

    @GetMapping("/getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable("attrId") long attrId){
        List<BaseAttrValue> attrValues= attrService.getAttrValueList(attrId);
        return Result.ok(attrValues);
    }
}
