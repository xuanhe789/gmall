package com.xuanhe.gmall.product.service;

import com.xuanhe.gmall.model.product.BaseAttrInfo;
import com.xuanhe.gmall.model.product.BaseAttrValue;

import java.util.List;

public interface AttrService {
    List<BaseAttrInfo> getAttrInfoBy3Id(Long category3Id);

    Integer saveAttrInfo(BaseAttrInfo baseAttrInfo);

    List<BaseAttrValue> getAttrValueList(long attrId);
}
