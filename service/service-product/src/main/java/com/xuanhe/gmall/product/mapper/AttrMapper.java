package com.xuanhe.gmall.product.mapper;

import com.xuanhe.gmall.model.product.BaseAttrInfo;
import com.xuanhe.gmall.model.product.BaseAttrValue;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AttrMapper {
    List<BaseAttrInfo> getAttrInfoList(Long category3Id);

    Integer saveAttrInfo(BaseAttrInfo baseAttrInfo);

    Integer insertAttrValues(List<BaseAttrValue> attrValueList);

    List<BaseAttrValue> getAttrValueList(long attrId);

    Integer updateInfoById(BaseAttrInfo baseAttrInfo);

    Integer deleteValuesByAttrId(Long id);
}
