package com.xuanhe.gmall.product.service.imp;

import com.xuanhe.gmall.model.base.BaseEntity;
import com.xuanhe.gmall.model.product.BaseAttrInfo;
import com.xuanhe.gmall.model.product.BaseAttrValue;
import com.xuanhe.gmall.model.product.BaseTrademark;
import com.xuanhe.gmall.product.mapper.AttrMapper;
import com.xuanhe.gmall.product.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttrServiceImpl implements AttrService {
    @Autowired
    AttrMapper attrMapper;
    @Override
    public List<BaseAttrInfo> getAttrInfoBy3Id(Long category3Id) {
        List<BaseAttrInfo> result=attrMapper.getAttrInfoList(category3Id);
        return result;
    }

    @Override
    public Integer saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        //先保存属性信息，再保存属性值
        if (baseAttrInfo.getId()==null) {
            Integer integer = attrMapper.saveAttrInfo(baseAttrInfo);
            if (integer < 1) {
                return integer;
            }
        }
        else {
            Integer integer=attrMapper.updateInfoById(baseAttrInfo);
            attrMapper.deleteValuesByAttrId(baseAttrInfo.getId());
        }
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        attrValueList.stream().forEach(baseAttrValue -> baseAttrValue.setAttrId(baseAttrInfo.getId()));
        Integer result = attrMapper.insertAttrValues(attrValueList);
        return result;
    }

    @Override
    public List<BaseAttrValue> getAttrValueList(long attrId) {
        return attrMapper.getAttrValueList(attrId);
    }
}
