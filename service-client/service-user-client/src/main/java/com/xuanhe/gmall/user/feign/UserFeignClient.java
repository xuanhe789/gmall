package com.xuanhe.gmall.user.feign;

import com.xuanhe.gmall.common.constant.RedisConst;
import com.xuanhe.gmall.model.user.UserAddress;
import com.xuanhe.gmall.model.user.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "service-user")
public interface UserFeignClient {
    @PostMapping("/api/user/passport/verify")
    UserInfo verify(@RequestParam String token);

    @GetMapping("/api/user/passport/findUserAddressListByUserId/{userId}")
    List<UserAddress> findUserAddressListByUserId(@PathVariable("userId") String userId);
}
