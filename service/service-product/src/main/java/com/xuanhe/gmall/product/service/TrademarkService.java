package com.xuanhe.gmall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xuanhe.gmall.model.product.BaseTrademark;

import java.util.List;

public interface TrademarkService {
    List<BaseTrademark> getTrademarkList();

    void getBaseTrademarkPage(IPage pageinfo);
}
