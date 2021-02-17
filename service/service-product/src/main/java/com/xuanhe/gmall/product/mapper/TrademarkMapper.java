package com.xuanhe.gmall.product.mapper;

import com.xuanhe.gmall.model.product.BaseTrademark;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TrademarkMapper {
    List<BaseTrademark> getTrademarkList();

    int getTotal();

    List<BaseTrademark> getBaseTrademarkPage(@Param("size") long size,@Param("start") long start);
}
