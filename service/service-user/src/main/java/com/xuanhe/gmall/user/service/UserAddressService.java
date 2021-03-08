package com.xuanhe.gmall.user.service;

import com.xuanhe.gmall.model.user.UserAddress;

import java.util.List;

public interface UserAddressService {
    List<UserAddress> findUserAddressListByUserId(String userId);
}
