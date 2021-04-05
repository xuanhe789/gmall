package com.xuanhe.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import com.xuanhe.gmall.common.result.Result;
import com.xuanhe.gmall.common.util.UrlParamUtils;
import com.xuanhe.gmall.item.feign.ItemFeignClient;
import com.xuanhe.gmall.list.feign.ListFeignClient;
import com.xuanhe.gmall.model.list.SearchParam;
import com.xuanhe.gmall.model.list.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.servlet.http.HttpServletRequest;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ListController {
    @Autowired
    ItemFeignClient itemFeignClient;
    @Autowired
    ListFeignClient listFeignClient;
    @Autowired
    SpringTemplateEngine templateEngine;
    @GetMapping(value = {"index.html",""})
    public String index(){
        return "index";
    }

    /*
    * 生成静态页面
    * */
    @GetMapping("createHTML")
    @ResponseBody
    public Result createHTML() throws Exception{
        List<JSONObject> categoryList = itemFeignClient.getCategoryList();
        Context context=new Context();
        context.setVariable("list",categoryList);
        FileWriter write = new FileWriter("D:\\index.html");
        templateEngine.process("index/index.html",context,write);
        return Result.ok();
    }

    @GetMapping({"list.html","search.html"})
    public String list(SearchParam searchParam, Model model){
        Result<Map> list = listFeignClient.list(searchParam);
        Map data = list.getData();
        String urlParam = UrlParamUtils.getUrlParam(searchParam);
        model.addAttribute("searchParam",searchParam);
        model.addAttribute("urlParam",urlParam);
        Map map=new HashMap<>();
        String order = searchParam.getOrder();
        if (!StringUtils.isEmpty(order)){
            String[] split = order.split(":");
            map.put("type",split[0]);
            map.put("sort",split[1]);
            model.addAttribute("orderMap",map);
        }else {
            map.put("type",1);
            map.put("sort","desc");
        }

        model.addAllAttributes(data);
        return "list/index";
    }
}
