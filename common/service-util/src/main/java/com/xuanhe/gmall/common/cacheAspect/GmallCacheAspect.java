package com.xuanhe.gmall.common.cacheAspect;

import com.xuanhe.gmall.model.product.SkuInfo;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class GmallCacheAspect {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;

    // 通过 aop 实现自动添加缓存的效果！{工具 任何一个方法都可以实现 【不能保证每个方法的返回值都一样】}
    // 返回数据类型 ：不一定是SkuInfo
    @Around("@annotation(com.xuanhe.gmall.common.cacheAspect.GmallCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint point){
        //声明一个对象object
        Object result=null;
        //获取到传递的参数s
        Object[] args = point.getArgs();
        // 想得到哪些方法上有注解
        // 获取方法上的签名
        MethodSignature signature= (MethodSignature) point.getSignature();
        // 得到注解
        GmallCache gmallCacheannotation = signature.getMethod().getAnnotation(GmallCache.class);
        //获取缓存key的前缀
        String cacheKey = gmallCacheannotation.prefix()+":"+ StringUtils.join(Arrays.asList(args),":");
        // 表示缓存不为空，则直接返回数据
        result = cacheHit(signature, cacheKey);
        if (result!=null){
            return result;
        }
        // 缓存要是空，则从数据库中获取数据{避免缓存击穿，穿透}
        RLock lock = redissonClient.getLock(cacheKey + "_lock");
        try {
            //尝试获取锁10秒
            boolean flag = lock.tryLock(100, 10, TimeUnit.SECONDS);
            if (flag){
                //双捡，再判断一次缓存是否有数据
                result = cacheHit(signature, cacheKey);
                if (result!=null){
                    return result;
                }
                result = point.proceed();
                // 判断执行结果:说明数据库中根本没有这个数据 防止缓存穿透
//                if (result==null){
//                    SkuInfo skuInfo = new SkuInfo();
//                    redisTemplate.opsForValue().set(cacheKey,skuInfo,60,TimeUnit.SECONDS);
//                    return skuInfo;
//                }
                //数据库中有数据直接放入缓存
                redisTemplate.opsForValue().set(cacheKey,result);
                return result;
            }else {
                //没有获取到锁的线程
                Thread.sleep(2000);
                return cacheHit(signature,cacheKey);
            }
        }catch (Throwable e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return result;
    }

    public Object cacheHit(Signature signature,String key){
        Object result = redisTemplate.opsForValue().get(key);
        if (result!=null){
            return result;
        }
        return null;

    }
}
