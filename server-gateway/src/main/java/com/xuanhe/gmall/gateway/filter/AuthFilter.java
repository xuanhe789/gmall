package com.xuanhe.gmall.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.common.result.ResultCodeEnum;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AuthFilter implements GlobalFilter {
    @Value("${authUrls.url}")
    private String authUrls;
    @Autowired
    RedisTemplate redisTemplate;
    // 匹配路径的工具类
    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();
        // 如果是内部接口，则网关拦截不允许外部访问！
        if (antPathMatcher.match("**/inner/**",path)){
            return authErro(response,ResultCodeEnum.PERMISSION);
        }
        // 用户登录认证
        //api接口，异步请求，校验用户必须登录
        boolean login=userExeit(response);
        if(antPathMatcher.match("/api/**/auth/**", path)) {
            if (!login){
                return authErro(response,ResultCodeEnum.LOGIN_AUTH);
            }
        }
        //验证url
        for (String authUrl:authUrls.split(",")){
            if (antPathMatcher.match(authUrl, path)&&!login) {
                response.setStatusCode(HttpStatus.SEE_OTHER);
                response.getHeaders().set(HttpHeaders.LOCATION,"http://www.gmall.com/login.html?originUrl="+request.getURI());
                Mono<Void> voidMono = response.setComplete();
                return voidMono;
            }
        }
        //放行
        return chain.filter(exchange);

    }

    public Boolean userExeit(ServerHttpResponse response) {
        List<String> tokenList = response.getHeaders().get("token");
        String token=tokenList.get(0);
        return redisTemplate.hasKey(token);
    }

    /**
     * 认证错误输出
     * @param resp 响应对象
     * @param resultCodeEnum 错误信息
     * @return
     */
    private Mono<Void> authErro(ServerHttpResponse resp, ResultCodeEnum resultCodeEnum) {
        resp.setStatusCode(HttpStatus.OK);
        resp.getHeaders().add("Content-Type","application/json;charset=UTF-8");
        Result<Object> result = Result.build(null, ResultCodeEnum.LOGIN_AUTH);
        String returnStr = "";
        try {
            returnStr = JSONObject.toJSONString(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DataBuffer buffer = resp.bufferFactory().wrap(returnStr.getBytes(StandardCharsets.UTF_8));
        return resp.writeWith(Flux.just(buffer));
    }
}
