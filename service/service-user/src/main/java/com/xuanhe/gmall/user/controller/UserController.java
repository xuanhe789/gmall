package com.xuanhe.gmall.user.controller;

import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.model.user.UserInfo;
import com.xuanhe.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/user/passport")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    RedisTemplate redisTemplate;

    @PostMapping("/login")
    public Result login(@RequestBody UserInfo userInfo){
        UserInfo user=userService.login(userInfo);
        if (user!=null){
            String token= UUID.randomUUID().toString().replaceAll("-", "");
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", user.getName());
            map.put("nickName", user.getNickName());
            map.put("token", token);
            //存到将token存入redis
            redisTemplate.opsForValue()
                    .set(RedisConst.USER_LOGIN_KEY_PREFIX + token, user.getId().toString(), RedisConst.USERKEY_TIMEOUT, TimeUnit.SECONDS);
            return Result.ok(map);
        }else {
            return Result.fail("用户或密码失败");
        }
    }

    @GetMapping("/logout")
    public Result logout(HttpServletRequest httpServletRequest){
        String token=httpServletRequest.getHeader("token");
        if (StringUtils.isEmpty(token)){
            redisTemplate.delete(RedisConst.USER_LOGIN_KEY_PREFIX + token);
        }
        return Result.ok();
    }
}
