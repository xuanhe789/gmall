package com.xuanhe.gmall.user.mapper;

import com.xuanhe.gmall.model.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    UserInfo login(UserInfo userInfo);
}
