package com.xuanhe.gmall.product.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.xuanhe.gmall.common.cacheAspect.GmallCache;
import com.xuanhe.gmall.model.product.BaseCategoryView;
import com.xuanhe.gmall.product.mapper.BaseCategoryViewMapper;
import com.xuanhe.gmall.product.service.BaseCategoryViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public List<JSONObject> getCategoryList() {
        List<BaseCategoryView> baseCategoryViewList= baseCategoryViewMapper.getAll();
        //根据1级分类id进行划分
        List<JSONObject> category1Objects = new ArrayList<>();
        Map<Long, List<BaseCategoryView>> catogory1s = baseCategoryViewList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        catogory1s.forEach((category1Id,baseCategoryViews) ->{
            JSONObject category1 = new JSONObject();
            category1.put("index",1);
            category1.put("categoryId",category1Id);
            category1.put("categoryName",baseCategoryViews.get(0).getCategory1Name());
            //根据2级分类id进行划分
            Map<Long, List<BaseCategoryView>> category2s = baseCategoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            List<JSONObject> category2Objects = new ArrayList<>();
            category2s.forEach((category2Id,baseCategoryViews2) ->{
                JSONObject category2 = new JSONObject();
                category2.put("categoryId",category2Id);
                category2.put("categoryName",baseCategoryViews2.get(0).getCategory2Name());
                //根据3级分类id进行划分
                Map<Long, List<BaseCategoryView>> category3s = baseCategoryViews2.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory3Id));
                List<JSONObject> category3Objects = new ArrayList<>();
                category3s.forEach((category3Id,baseCategoryViews3) ->{
                    JSONObject category3 = new JSONObject();
                    category3.put("categoryId",category3Id);
                    category3.put("categoryName",baseCategoryViews3.get(0).getCategory3Name());
                    category3Objects.add(category3);
                });
                category2.put("categoryChild",category3Objects);
                category2Objects.add(category2);
            });
            category1.put("categoryChild",category2Objects);
            category1Objects.add(category1);
        });
        return category1Objects;
    }
}
