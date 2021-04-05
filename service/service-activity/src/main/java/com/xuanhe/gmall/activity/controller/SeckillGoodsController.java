package com.xuanhe.gmall.activity.controller;

import com.alibaba.fastjson.JSONObject;
import com.xuanhe.gmall.activity.config.CacheHelper;
import com.xuanhe.gmall.activity.service.SeckillService;
import com.xuanhe.gmall.common.constant.RedisConst;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.common.result.ResultCodeEnum;
import com.xuanhe.gmall.common.util.AuthContextHolder;
import com.xuanhe.gmall.common.util.DateUtil;
import com.xuanhe.gmall.common.util.MD5;
import com.xuanhe.gmall.model.activity.OrderRecode;
import com.xuanhe.gmall.model.activity.SeckillGoods;
import com.xuanhe.gmall.model.order.OrderDetail;
import com.xuanhe.gmall.model.order.OrderInfo;
import com.xuanhe.gmall.model.user.UserInfo;
import com.xuanhe.gmall.model.user.UserRecode;
import com.xuanhe.gmall.order.feign.OrderFeignClient;
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
    @Autowired
    OrderFeignClient orderFeignClient;

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
    @GetMapping("/auth/getSeckillSkuIdStr/{skuId}")
    public Result getSeckillSkuIdStr(@PathVariable("skuId") Long skuId, HttpServletRequest request) {
        String userInfoStr = request.getHeader("userInfo");
        UserInfo userInfo = JSONObject.parseObject(userInfoStr, UserInfo.class);
        Long isExit= (Long) redisTemplate.opsForValue().get(RedisConst.SECKILL_USER+userInfo.getId());
        if (null!=isExit&&isExit.equals(skuId)){
            return Result.build(null, ResultCodeEnum.SECKILL_FAIL).message("您已下过单");
        }
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
        String  skuIdStr= request.getParameter("skuIdStr");
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

    /*
    * 轮询检查抢购的状态
    * */
    @GetMapping(value = "auth/checkOrder/{skuId}")
    public Result checkOrder(@PathVariable("skuId") Long skuId, HttpServletRequest request) {
        String userInfoJson = request.getHeader("userInfo");
        UserInfo userInfo = JSONObject.parseObject(userInfoJson, UserInfo.class);
        return seckillService.checkOrder(userInfo.getId(),skuId);
    }
    /*
    * 获取商品详情
    * */
    @GetMapping("/getOrderDetaliList/{skuId}")
    List<OrderDetail> getOrderDetaliList(@PathVariable("skuId") String skuId,HttpServletRequest request){
        String userInfoJson = request.getHeader("userInfo");
        UserInfo userInfo = JSONObject.parseObject(userInfoJson, UserInfo.class);
        List<OrderDetail> orderDetailList=seckillService.getOrderDetaliList(skuId,userInfo.getId());
        return orderDetailList;
    }

    /*
    * 提交秒杀订单
    * */
    @PostMapping("/auth/submitOrder/{tradeNo}")
    public Result submitOrder(@PathVariable("tradeNo") String tradeNo, @RequestBody OrderInfo orderInfo,HttpServletRequest request){
        String userInfoJson = request.getHeader("userInfo");
        UserInfo userInfo = JSONObject.parseObject(userInfoJson, UserInfo.class);
        //判断用户是否秒杀成功
        Long skuId = orderInfo.getOrderDetailList().get(0).getSkuId();
        OrderRecode orderRecode= (OrderRecode) redisTemplate.boundHashOps(RedisConst.SECKILL_ORDERS+ skuId).get(userInfo.getId().toString());
        if (orderRecode==null){
            return Result.fail().message("非法请求");
        }
        orderInfo.setUserId(userInfo.getId());
        Result<String> result = orderFeignClient.submitSeckillOrder(orderInfo, tradeNo);
        if (result.getCode()!=200){
            return result;
        }
        String orderId = result.getData();
        //删除缓存中秒杀成功的数据
        seckillService.deleteOrderRecode(userInfo.getId()+"",skuId);
        //增加到下单成功列表
        seckillService.createOrderSucess(userInfo.getId().toString(),skuId,orderId);
        return Result.ok(orderId);
    }
}
