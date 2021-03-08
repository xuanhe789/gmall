package com.xuanhe.gmall.user.mapper;

import com.xuanhe.gmall.model.user.UserAddress;
import com.xuanhe.gmall.model.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    UserInfo login(UserInfo userInfo);

    List<UserAddress> findUserAddressListByUserId(String userId);
}
