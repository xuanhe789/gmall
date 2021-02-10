package com.xuanhe.gmall.product.service.imp;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuanhe.gmall.model.product.*;
import com.xuanhe.gmall.product.mapper.SpuMapper;
import com.xuanhe.gmall.product.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpuServiceImpl implements SpuService {
    @Autowired
    SpuMapper spuMapper;

    @Override
    public IPage<SpuInfo> getSpuList(IPage page, Long category3Id) {
        long total=spuMapper.getTotal(category3Id);
        page.setTotal(total);
        page.setPages(total%page.getSize()==0?total/page.getSize():total/page.getSize()+1);
        List<SpuInfo> spuList = spuMapper.getSpuList(page.getCurrent()-1,page.getSize(), category3Id);
        page.setRecords(spuList);
        return page;
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return spuMapper.getBaseSaleAttrList();
    }

    @Override
    public Integer saveSpuInfo(SpuInfo spuInfo) {
        //先保存spuInfo表的信息
        Integer integer=spuMapper.saveSpuInfo(spuInfo);
        //然后保存Spu_image信息
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (spuImageList!=null&&spuImageList.size()!=0) {
            spuImageList.stream().forEach(spuImage -> spuImage.setSpuId(spuInfo.getId()));
            Integer integer1 = spuMapper.savespuImageList(spuImageList);
        }
        //保存spu_sale_attr信息
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        spuSaleAttrList.stream().forEach(spuSaleAttr -> spuSaleAttr.setSpuId(spuInfo.getId()));
        Integer integer2=spuMapper.spuSaleAttrList(spuSaleAttrList);
        //保存spu_sale_attr_value信息
        for (SpuSaleAttr spuSaleAttr:spuSaleAttrList){
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            spuSaleAttrValueList.stream().forEach(spuSaleAttrValue -> {spuSaleAttrValue.setSpuId(spuInfo.getId());
            spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
            });
            Integer integer3=spuMapper.savespuSaleAttrValueList(spuSaleAttrValueList);
        }
        return integer2;
    }

    @Override
    public List<SpuImage> getSpuImageListBySpuId(Long spuId) {
        return spuMapper.getSpuImageListBySpuId(spuId);
    }

    @Override
    public List<SpuSaleAttr> getspuSaleAttrListBySpuId(Long spuId) {
        return spuMapper.getspuSaleAttrListBySpuId(spuId);
    }
}
