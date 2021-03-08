package com.xuanhe.gmall.cart.mapper;

import com.xuanhe.gmall.model.cart.CartInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartMapper {


    List<CartInfo> getCartInfoList(String id);

    Integer deleteByuserTempId(String userTempId);

    void updateById(CartInfo cartInfo1);

    void insert(CartInfo cartInfo);

    void updateIscheckedByUserIdAndSkuId(@Param("userId") String userId,@Param("skuId") Long skuId,@Param("isChecked") Integer isChecked);

    void deleteByUserIdAndSkuId(@Param("skuId") Long skuId,@Param("userId") String userId);

    CartInfo exist(@Param("skuId") Long skuId,@Param("userId") String userId);

    void insertALL(CartInfo cartInfo);

    List<CartInfo> getCartListIsCheckedByUserId(String userId);

    void deleteAllByUserId(String userId);
}
