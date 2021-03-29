package com.xuanhe.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import com.xuanhe.gmall.activity.feign.ActivityFeignClient;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.common.util.MD5;
import com.xuanhe.gmall.model.user.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class SeckillController {
    @Autowired
    ActivityFeignClient activityFeignClient;

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
    @GetMapping("seckill/queue.html")
    public String queue(Model model, HttpServletRequest request){
        Long skuId = (Long) request.getAttribute("skuId");
        String skuIdStr= (String) request.getAttribute("skuIdStr");
        model.addAttribute("skuId",skuId);
        model.addAttribute("skuIdStr",skuIdStr);
        return "seckill/queue";

    }



}
