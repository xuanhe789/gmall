package com.xuanhe.gmall.user.service.imp;

import com.xuanhe.gmall.model.user.UserAddress;
import com.xuanhe.gmall.user.mapper.UserMapper;
import com.xuanhe.gmall.user.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAddressServiceImpl implements UserAddressService {
    @Autowired
    UserMapper userMapper;

    @Override
    public List<UserAddress> findUserAddressListByUserId(String userId) {
        return userMapper.findUserAddressListByUserId(userId);
    }
}
