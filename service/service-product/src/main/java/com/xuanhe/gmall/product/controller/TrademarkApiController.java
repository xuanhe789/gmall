package com.xuanhe.gmall.product.controller;

import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.model.product.BaseTrademark;
import com.xuanhe.gmall.product.service.TrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
