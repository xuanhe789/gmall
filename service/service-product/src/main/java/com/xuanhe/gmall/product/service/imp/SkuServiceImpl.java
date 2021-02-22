package com.xuanhe.gmall.product.service.imp;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xuanhe.gmall.common.cacheAspect.GmallCache;
import com.xuanhe.gmall.list.feign.ListFeignClient;
import com.xuanhe.gmall.model.list.Goods;
import com.xuanhe.gmall.model.product.*;
import com.xuanhe.gmall.product.mapper.SkuMapper;
import com.xuanhe.gmall.product.mapper.SpuMapper;
import com.xuanhe.gmall.product.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    ListFeignClient listFeignClient;
    @Autowired
    SkuMapper skuMapper;
    @Autowired
    SpuMapper spuMapper;


    @Transactional
    @Override
    public boolean saveSkuInfo(SkuInfo skuInfo) {
        Integer integer=skuMapper.saveSkuInfo(skuInfo);
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        skuAttrValueList.stream().forEach(skuAttrValue -> skuAttrValue.setSkuId(skuInfo.getId()));
        Integer integer1=skuMapper.saveSkuAttrValueList(skuAttrValueList);
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        skuImageList.stream().forEach(skuImage -> skuImage.setSkuId(skuInfo.getId()));
        Integer integer2=skuMapper.saveSkuImageList(skuImageList);
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        skuSaleAttrValueList.stream().forEach(skuSaleAttrValue -> {skuSaleAttrValue.setSkuId(skuInfo.getId());
        skuSaleAttrValue.setSpuId(skuInfo.getSpuId());});
        Integer integer3=skuMapper.saveSkuSaleAttrValueList(skuSaleAttrValueList);
        return true;
    }

    @Override
    public void getList(IPage<SkuInfo> iPage) {
        Integer total= skuMapper.getTotal();
        iPage.setTotal(total);
        long size = iPage.getSize();
        List<SkuInfo> skuInfoList= skuMapper.getListPage((iPage.getCurrent()-1)*size,size);
        iPage.setRecords(skuInfoList);
    }

    @Override
    public boolean onSale(Long skuId) {
        Integer integer=skuMapper.onSale(skuId);
        listFeignClient.onsale(skuId);
        return true;
    }

    @Override
    public boolean upSale(Long skuId) {
        Integer result=skuMapper.upSale(skuId);
        listFeignClient.cancelSale(skuId);
        return true;
    }

    @Override
    @GmallCache(prefix = "skuInfo_skuId")
    public SkuInfo getSkuInfoById(Long skuId) {
        SkuInfo skuInfo=skuMapper.getSkuInfoById(skuId);
        return  skuInfo;
    }

    @Override
    @GmallCache(prefix = "skuPrice_skuId")
    public BigDecimal getSkuPriceBySkuId(Long skuId) {
        SkuInfo skuInfo= skuMapper.getSkuInfoById(skuId);
        if (skuInfo!=null){
            return skuInfo.getPrice();
        }
        return new BigDecimal("0");
    }

    @Override
    @GmallCache(prefix = "spuSaleAttrList_spuId_skuId")
    public List<SpuSaleAttr> getSpuSaleAttrListBySpuId(Long spuId,Long skuId) {
        List<SpuSaleAttr> spuSaleAttrList= spuMapper.getspuSaleAttrListBySpuIdAndSkuId(spuId,skuId);
        return spuSaleAttrList;
    }

    @Override
    @GmallCache(prefix = "skuImage_skuId")
    public List<SkuImage> getSkuImagesBySkuId(Long skuId) {
        List<SkuImage> skuImages=skuMapper.getSkuImagesBySkuId(skuId);
        return skuImages;
    }

    @Override
    @GmallCache(prefix = "skuValueMap_spuId")
    public Map<String, Long> getSkuValueMap(Long spuId) {
        List<Map> skuValueMap = skuMapper.getSkuValueMap(spuId);
        Map<String,Long> result= new HashMap<>();
        skuValueMap.stream().forEach(map -> result.put((String) map.get("value_ids"),(Long) map.get("sku_id")));
        return result;
    }

    @Override
    public Goods getGoodsBySkuId(Long skuId) {
        Goods goods=skuMapper.getGoodsBySkuId(skuId);
        return goods;
    }

}
