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

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ListController {
    @Autowired
    ItemFeignClient itemFeignClient;
    @Autowired
    ListFeignClient listFeignClient;
    @GetMapping("index.html")
    public String index(){
        List<JSONObject> jsonObjects=itemFeignClient.getCategoryList();
        return "index";
    }

    @GetMapping({"list.html","search.html"})
    public String list(SearchParam searchParam, Model model, HttpServletRequest request){
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
