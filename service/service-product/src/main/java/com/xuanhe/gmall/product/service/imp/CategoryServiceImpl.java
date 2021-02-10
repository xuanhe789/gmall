package com.xuanhe.gmall.product.service.imp;

import com.xuanhe.gmall.model.product.BaseCategory1;
import com.xuanhe.gmall.model.product.BaseCategory2;
import com.xuanhe.gmall.model.product.BaseCategory3;
import com.xuanhe.gmall.product.mapper.BaseCategory1Mapper;
import com.xuanhe.gmall.product.mapper.BaseCategory2Mapper;
import com.xuanhe.gmall.product.mapper.BaseCategory3Mapper;
import com.xuanhe.gmall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    BaseCategory1Mapper baseCategory1Mapper;
    @Autowired
    BaseCategory2Mapper baseCategory2Mapper;
    @Autowired
    BaseCategory3Mapper baseCategory3Mapper;


    @Override
    public List<BaseCategory1> getCategory1() {
        List<BaseCategory1> category1s = baseCategory1Mapper.selectList(null);
        return category1s;
    }

    @Override
    public List<BaseCategory2> getCategory2(Long id) {
        return baseCategory2Mapper.selectCategory2List(id);
    }

    @Override
    public List<BaseCategory3> getCategory3(Long id) {
        return baseCategory3Mapper.getCategory3List(id);
    }
}
