package com.xuanhe.gmall.list.service;

import com.xuanhe.gmall.model.list.SearchParam;
import com.xuanhe.gmall.model.list.SearchResponseVo;
import org.elasticsearch.action.search.SearchResponse;

import java.io.IOException;

public interface ListService {
    void onSale(Long skuId);

    void cancelSale(Long skuId);

    void incrHotScore(Long skuId);

    SearchResponseVo search(SearchParam searchParam) throws IOException;
}
