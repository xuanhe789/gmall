package com.xuanhe.gmall.user.service.imp;

import com.xuanhe.gmall.model.user.UserInfo;
import com.xuanhe.gmall.user.mapper.UserMapper;
import com.xuanhe.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Override
    public UserInfo login(UserInfo userInfo) {
        //将密码进行加密
        String pwdNew = DigestUtils.md5DigestAsHex(userInfo.getPasswd().getBytes());
        userInfo.setPasswd(pwdNew);
        return userMapper.login(userInfo);
    }
}
