package com.xuanhe.gmall.user.feign;

import com.xuanhe.gmall.common.constant.RedisConst;
import com.xuanhe.gmall.model.user.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "service-user")
public interface UserFeignClient {
    @PostMapping("/api/user/passport/verify")
    public UserInfo verify(@RequestParam String token);
}
