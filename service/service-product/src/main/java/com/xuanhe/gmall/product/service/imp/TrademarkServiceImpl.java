package com.xuanhe.gmall.product.service.imp;

import com.xuanhe.gmall.model.product.BaseTrademark;
import com.xuanhe.gmall.product.mapper.TrademarkMapper;
import com.xuanhe.gmall.product.service.TrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrademarkServiceImpl implements TrademarkService {
    @Autowired
    TrademarkMapper trademarkMapper;
    @Override
    public List<BaseTrademark> getTrademarkList() {
        return trademarkMapper.getTrademarkList();
    }
}
