package com.xuanhe.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import com.xuanhe.gmall.activity.feign.ActivityFeignClient;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.model.order.OrderDetail;
import com.xuanhe.gmall.model.order.OrderInfo;
import com.xuanhe.gmall.model.user.UserAddress;
import com.xuanhe.gmall.model.user.UserInfo;
import com.xuanhe.gmall.order.feign.OrderFeignClient;
import com.xuanhe.gmall.user.feign.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class SeckillController {
    @Autowired
    ActivityFeignClient activityFeignClient;
    @Autowired
    UserFeignClient userFeignClient;
    @Autowired
    OrderFeignClient orderFeignClient;

    /**
     * 秒杀列表
     * @param model
     * @return
     */
    @GetMapping("seckill.html")
    public String index(Model model) {
        Result result = activityFeignClient.findAll();
        model.addAttribute("list", result.getData());
        return "seckill/index";
    }

    /**
     * 秒杀详情
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("seckill/{skuId}.html")
    public String getItem(@PathVariable Long skuId, Model model){
// 通过skuId 查询skuInfo
        Result result = activityFeignClient.getSeckillGoodById(skuId);
        model.addAttribute("item", result.getData());
        return "seckill/item";
    }

    /*
    * 进入队列界面
    * */
    @GetMapping("/seckill/queue.html")
    public String queue(Model model, HttpServletRequest request){
        //这里搞混了好久，获取路劲参数得用getParameter，只有在转发的时候才能用getAttribute()
        String skuId = request.getParameter("skuId");
        String skuIdStr= request.getParameter("skuIdStr");
        model.addAttribute("skuId",skuId);
        model.addAttribute("skuIdStr",skuIdStr);
        return "seckill/queue";

    }

    @GetMapping("/seckill/trade.html")
    public String trade(HttpServletRequest request,Model model){
        String info = request.getHeader("userInfo");
        UserInfo userInfo = JSONObject.parseObject(info, UserInfo.class);
        //获取用户地址
        List<UserAddress> userAddressListByUserId = userFeignClient.findUserAddressListByUserId(userInfo.getId() + "");
        model.addAttribute("userAddressList",userAddressListByUserId);
        //获取商品详情
        String skuId= (String) request.getParameter("skuId");
        List<OrderDetail> orderDetailList=activityFeignClient.getOrderDetaliList(skuId);
        if (orderDetailList==null){
            return "seckill/fail";
        }
        //获取订单流水号，防止重复提交
        String tradeNo=orderFeignClient.getTradeNo(userInfo.getId()+"");
        //计算总金额
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderDetailList(orderDetailList);
        orderInfo.sumTotalAmount();
        model.addAttribute("tradeNo",tradeNo);
        model.addAttribute("detailArrayList",orderDetailList);
        model.addAttribute("totalAmount",orderInfo.getTotalAmount());
        return "seckill/trade";
    }



}
