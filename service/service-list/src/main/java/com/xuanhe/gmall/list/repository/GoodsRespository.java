package com.xuanhe.gmall.list.repository;

import com.xuanhe.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GoodsRespository extends ElasticsearchRepository<Goods,Long> {
}
