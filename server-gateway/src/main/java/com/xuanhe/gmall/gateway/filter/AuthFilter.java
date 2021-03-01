package com.xuanhe.gmall.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.common.result.ResultCodeEnum;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class AuthFilter implements GlobalFilter {

    AntPathMatcher antPathMatcher;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String s = request.getURI().toString();
        //放行静态资源

        //重定向到登录界面
        response.setStatusCode(HttpStatus.SEE_OTHER);
        response.getHeaders().set(HttpHeaders.LOCATION,"http");
        Mono<Void> voidMono = response.setComplete();
        return voidMono;

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
