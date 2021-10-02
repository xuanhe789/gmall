package com.xuanhe.gmall.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.model.product.BaseTrademark;
import com.xuanhe.gmall.product.service.TrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin/product")
@RestController
public class TrademarkApiController {
    @Autowired
    TrademarkService trademarkService;
    @GetMapping("/baseTrademark/getTrademarkList")
    public Result getTrademarkList(){
        List<BaseTrademark> baseTrademarks=trademarkService.getTrademarkList();
        return Result.ok(baseTrademarks);
    }

    @GetMapping("/baseTrademark/{page}/{limit}")
    public Result getBaseTrademarkPage(@PathVariable("page") Integer page,@PathVariable("limit") Integer limit){
        IPage pageinfo=new Page<>();
        pageinfo.setCurrent(page);
        pageinfo.setSize(limit);
        trademarkService.getBaseTrademarkPage(pageinfo);
        return Result.ok(pageinfo);
    }

}
