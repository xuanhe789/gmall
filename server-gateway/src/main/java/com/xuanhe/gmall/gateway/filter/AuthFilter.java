package com.xuanhe.gmall.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.common.result.ResultCodeEnum;
import com.xuanhe.gmall.model.user.UserInfo;
import com.xuanhe.gmall.user.feign.UserFeignClient;
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

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AuthFilter implements GlobalFilter {
    @Value("${authUrls.url}")
    private String authUrls;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserFeignClient userFeignClient;
    // 匹配路径的工具类
    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String uri=request.getURI().getPath();
        String path = request.getURI().toString();
        // 如果是内部接口，则网关拦截不允许外部访问！
        if (antPathMatcher.match("**/inner/**",path)){
            return authErro(response,ResultCodeEnum.PERMISSION);
        }
        //放行静态资源
        if(path.indexOf("passport")!=-1||path.indexOf(".js")!=-1||path.indexOf(".css")!=-1||path.indexOf(".jpg")!=-1||path.indexOf(".png")!=-1||path.indexOf(".json")!=-1||path.indexOf(".ico")!=-1||path.indexOf(".icon")!=-1){
            return chain.filter(exchange);
        }
        // 用户登录认证
        //api接口，异步请求，校验用户必须登录
        UserInfo userInfo=null;
        String token=null;
        List<String> list = request.getHeaders().get("token");
        if (list!=null){
            token=list.get(0);
            userInfo=userFeignClient.verify(token);
        }
        if(antPathMatcher.match("/api/**/auth/**", path)) {
            if (userInfo==null){
                return authErro(response,ResultCodeEnum.LOGIN_AUTH);
            }
        }
        //验证url
        for (String authUrl:authUrls.split(",")){
            if (path.indexOf(authUrl)!=-1&&userInfo==null) {
                response.setStatusCode(HttpStatus.SEE_OTHER);
                response.getHeaders().set(HttpHeaders.LOCATION,"http://passport.gmall.com/login.html?originUrl="+path);
                Mono<Void> voidMono = response.setComplete();
                return voidMono;
            }
        }
        //将用户信息放入请求中，方便后续调用
        if (userInfo!=null){
            request = request.mutate().header("userInfo", JSONObject.toJSONString(userInfo)).build();
            exchange = exchange.mutate().request(request).build();
            return chain.filter(exchange);
        }
        //放行
        return chain.filter(exchange);

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
