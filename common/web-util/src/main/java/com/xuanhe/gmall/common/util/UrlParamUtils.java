package com.xuanhe.gmall.common.util;

import com.xuanhe.gmall.model.list.SearchParam;
import org.apache.commons.lang.ArrayUtils;

public class UrlParamUtils {
    public static String getUrlParam(SearchParam searchParam){
        StringBuilder stringBuilder = new StringBuilder("http://list.gmall.com/list.html?");
        Long category3Id = searchParam.getCategory3Id();
        String keyword = searchParam.getKeyword();
        String trademark = searchParam.getTrademark();
        String[] props = searchParam.getProps();
        if (category3Id!=null){
            stringBuilder.append("category3Id="+category3Id);
        }
        if (keyword!=null){
            stringBuilder.append("keyword="+keyword);
        }
        if (trademark!=null){
            stringBuilder.append("&trademark="+keyword);
        }
        if (!ArrayUtils.isEmpty(props)){
            for (String prop : props) {
                stringBuilder.append("&props="+prop);
            }
        }
        return stringBuilder.toString();
    }

}
