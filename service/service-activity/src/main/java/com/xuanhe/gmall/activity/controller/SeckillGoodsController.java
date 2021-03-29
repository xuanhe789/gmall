package com.xuanhe.gmall.activity.controller;

import com.alibaba.fastjson.JSONObject;
import com.xuanhe.gmall.activity.config.CacheHelper;
import com.xuanhe.gmall.activity.service.SeckillService;
import com.xuanhe.gmall.common.constant.RedisConst;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.common.util.DateUtil;
import com.xuanhe.gmall.common.util.MD5;
import com.xuanhe.gmall.model.activity.SeckillGoods;
import com.xuanhe.gmall.model.user.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/activity/seckill")
public class SeckillGoodsController {
    @Autowired
    SeckillService seckillService;
    @Autowired
    RedisTemplate redisTemplate;

    /*
    * 返回秒杀所有列表
    * */
    @GetMapping("/findAll")
    public Result<List<SeckillGoods>> findAll(){
        return Result.ok(seckillService.findAll());
    }

    /*
    * 获取秒杀商品详情
    * */
    @GetMapping("/getSeckillGoods/{skuId}")
    public Result<SeckillGoods> getSeckillGoodById(@PathVariable("skuId") Long skuId){
        return Result.ok(seckillService.getSeckillGodd(skuId));
    }

    /*
    * 生成下单码
    * */
    @GetMapping("auth/getSeckillSkuIdStr/{skuId}")
    public Result getSeckillSkuIdStr(@PathVariable("skuId") Long skuId, HttpServletRequest request) {
        String userInfoStr = request.getHeader("userInfo");
        UserInfo userInfo = JSONObject.parseObject(userInfoStr, UserInfo.class);
        SeckillGoods seckillGoods= (SeckillGoods) redisTemplate.boundHashOps(RedisConst.SECKILL_GOODS).get(skuId+"");
        if (seckillGoods!=null){
            Date currentTime = new Date();
            //判断当前时间是否在秒杀时间内
            if (DateUtil.dateCompare(seckillGoods.getStartTime(), currentTime) && DateUtil.dateCompare(currentTime, seckillGoods.getEndTime())){
                //时间戳
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdHHmm");
                //根据用户id，MD5加密生成抢购码
                String encrypt = MD5.encrypt(userInfo.getId() + "");
                encrypt=encrypt+sdf.format(new Date());
                //存入缓存
                return Result.ok(encrypt);
            }
        }
        return Result.fail().message("该秒杀不存在，获取下单码失败");
    }
    /*
    * 校验抢购码，将用户的抢购信息发送到消息队列mq
    * */
    @PostMapping("/auth/seckillOrder/{skuId}")
    public Result saveOrder(@PathVariable("skuId") Long skuId,HttpServletRequest request){
        String  skuIdStr= (String) request.getAttribute("skuIdStr");
        String userInfoJson = request.getHeader("userInfo");
        UserInfo userInfo = JSONObject.parseObject(userInfoJson, UserInfo.class);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdHHmm");
        String confireStr=MD5.encrypt(userInfo.getId()+"")+sdf.format(new Date());
        if (!confireStr.equals(skuIdStr)){
            return Result.fail().message("非法请求");
        }
        String status = (String) CacheHelper.get(skuId + "");
        if (status==null||!status.equals("1")){
            return Result.fail().message("已售罄");
        }
        seckillService.saveOrder(skuId,userInfo.getId());
        return Result.ok();
    }
}
