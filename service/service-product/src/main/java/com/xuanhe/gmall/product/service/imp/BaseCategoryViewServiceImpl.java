package com.xuanhe.gmall.product.service.imp;

import com.xuanhe.gmall.common.cacheAspect.GmallCache;
import com.xuanhe.gmall.model.product.BaseCategoryView;
import com.xuanhe.gmall.product.mapper.BaseCategoryViewMapper;
import com.xuanhe.gmall.product.service.BaseCategoryViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaseCategoryViewServiceImpl implements BaseCategoryViewService {
    @Autowired
    BaseCategoryViewMapper baseCategoryViewMapper;

    @Override
    @GmallCache(prefix = "categoryView_category3Id")
    public BaseCategoryView getBaseCategoryView(Long category3Id) {
        BaseCategoryView baseCategoryView=baseCategoryViewMapper.getBaseCategoryView(category3Id);
        return baseCategoryView;
    }
}
