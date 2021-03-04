package com.xuanhe.gmall.cart.service.impl;

import com.xuanhe.gmall.cart.mapper.CartMapper;
import com.xuanhe.gmall.cart.service.CartService;
import com.xuanhe.gmall.common.constant.RedisConst;
import com.xuanhe.gmall.model.cart.CartInfo;
import com.xuanhe.gmall.model.product.SkuInfo;
import com.xuanhe.gmall.product.feign.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    ProductFeignClient productFeignClient;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    CartMapper cartMapper;
    @Override
    public void addToCart(Long skuId, String userId, Integer skuNum) {
        CartInfo cartInfoExist=cartMapper.exist(skuId,userId);
        String cartKey = getCartKey(userId);
        //如果该商品已存在购物车，则修改数量
        if (cartInfoExist!=null){
            //数量相加
            cartInfoExist.setSkuNum(cartInfoExist.getSkuNum()+skuNum);
            //更新数据库
            cartMapper.updateById(cartInfoExist);
            //更新缓存
            redisTemplate.boundHashOps(cartKey).put(skuId.toString(),cartInfoExist);
        }else {
            CartInfo cartInfo = new CartInfo();
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            cartInfo.setSkuNum(skuNum);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setIsChecked(0);
            cartInfo.setUserId(userId);
            cartInfo.setSkuPrice(skuInfo.getPrice());
            cartInfo.setSkuId(skuId);
            cartMapper.insertALL(cartInfo);
            //加入缓存
            redisTemplate.boundHashOps(cartKey).put(skuId.toString(),cartInfo);
        }
    }

    @Override
    public List<CartInfo> getCartList(String userId,String userTempId) {
        List<CartInfo> cartInfoList=new ArrayList<>();
        List<CartInfo> cartInfoListByUserTempId=getCartListById(userTempId);
        //如果用户id为空，则返回临时id的数据
        if (StringUtils.isEmpty(userId)){
            return cartInfoListByUserTempId;
        }else {
            //用户已登录，合并临时数据
            if (!CollectionUtils.isEmpty(cartInfoListByUserTempId)){
                cartInfoList=merge(cartInfoListByUserTempId,userId);
            }else {
                cartInfoList=getCartListById(userId);
            }
        }
        return cartInfoList;
    }

    @Override
    public void checkCart(String userId, Long skuId, Integer isChecked) {
        String cartKey = getCartKey(userId);
        String skuIdS = String.valueOf(skuId);
        //修改数据库
        cartMapper.updateIscheckedByUserIdAndSkuId(userId,skuId,isChecked);
        //修改缓存
        CartInfo cartInfo= (CartInfo) redisTemplate.boundHashOps(cartKey).get(skuIdS);
        cartInfo.setIsChecked(isChecked);
        //写回缓存
        redisTemplate.boundHashOps(cartKey).put(skuIdS,cartInfo);
        //设置过期时间
        setCartKeyExpire(cartKey);
    }

    @Override
    public void deleteCartByUserId(Long skuId, String userId) {
        String skuIdS = String.valueOf(skuId);
        //获取缓存key
        String cartKey = getCartKey(userId);
        if (redisTemplate.hasKey(cartKey)) {
            redisTemplate.boundHashOps(cartKey).delete(skuIdS);
        }
        cartMapper.deleteByUserIdAndSkuId(skuId,userId);
    }

    private List<CartInfo> merge(List<CartInfo> cartInfoListByUserTempId, String userId) {
        List<CartInfo> cartInfoList =  new ArrayList<>();
        List<CartInfo> cartInfoListByUserId= getCartListById(userId);
        //获取redis中购物车的key
        String cartKey = getCartKey(userId);
        //将已登录用户的数据封装成map
        Map<Long, CartInfo> map = cartInfoListByUserId.stream().collect(Collectors.toMap(CartInfo::getSkuId, cartInfo -> cartInfo));
        //遍历未登录时的购物项集合
        for (CartInfo cartInfo : cartInfoListByUserTempId) {
            BigDecimal skuPrice = productFeignClient.getSkuPrice(cartInfo.getSkuId());
            //如果已登录的购物车存在这条购物项，则更新数量
            if (map.containsKey(cartInfo.getSkuId())){
                CartInfo cartInfo1 = map.get(cartInfo.getSkuId());
                cartInfo1.setSkuNum(cartInfo1.getSkuNum()+cartInfo.getSkuNum());
                //  合并数据：勾选
                // 未登录状态选中的商品！
                if (cartInfo1.getIsChecked().intValue() == 1) {
                    cartInfo1.setIsChecked(1);
                }
                //更新价格
                cartInfo1.setSkuPrice(skuPrice);
                cartInfoList.add(cartInfo1);
                //更新数据库
                cartMapper.updateById(cartInfo1);
                continue;
            }
            //更新价格
            cartInfo.setUserId(userId);
            cartInfo.setSkuPrice(skuPrice);
            //若登录后的购物车内没有这条购物车项，则插入数据库
            cartMapper.insert(cartInfo);
            cartInfoList.add(cartInfo);
        }
        Map<String, CartInfo> cartInfoMap = cartInfoList.stream().collect(Collectors.toMap(cartInfo -> cartInfo.getSkuId()+"", cartInfo -> cartInfo));
        //将合并后的数据放进缓存
        redisTemplate.boundHashOps(cartKey).putAll(cartInfoMap);
        //删除临时id的缓存和数据库
        deleteCart(cartInfoListByUserTempId.get(0).getUserId());

        return cartInfoList;
    }
    /*
    * 通过用户Id或者临时Id获取购物车数据
    * */
    private List<CartInfo> getCartListById(String id) {
        List<CartInfo> cartInfoList = new ArrayList<>();
        if (StringUtils.isEmpty(id)){
            return null;
        }
        //先尝试从缓存中获取数据
        String cartKey = getCartKey(id);
        if (redisTemplate.hasKey(cartKey)){
            cartInfoList = redisTemplate.boundHashOps(cartKey).values();
            // 购物车列表显示有顺序：按照商品的更新时间 降序
            cartInfoList.sort(new Comparator<CartInfo>() {
                @Override
                public int compare(CartInfo o1, CartInfo o2) {
                    // str1 = ab str2 = ac;
                    return o1.getId().toString().compareTo(o2.getId().toString());
                }
            });
            //更新价格
            cartInfoList.stream().forEach(cartInfo -> {
                BigDecimal skuPrice = productFeignClient.getSkuPrice(cartInfo.getSkuId());
                cartInfo.setSkuPrice(skuPrice);
            });
        }else {
            cartInfoList= loadCartCache(id);
        }
        return cartInfoList;
    }

    private List<CartInfo> loadCartCache(String id) {
        //从数据库中获取购物车数据
        List<CartInfo> cartInfoList=cartMapper.getCartInfoList(id);
        //获取redis的key
        String cartKey = getCartKey(id);
        if (!CollectionUtils.isEmpty(cartInfoList)){
            //更新价格，将购物车数据封装成map，方便存入
            Map<String, CartInfo> map = cartInfoList.stream().collect(Collectors.toMap(cartInfo -> cartInfo.getSkuId()+"", cartInfo -> {
                BigDecimal skuPrice = productFeignClient.getSkuPrice(cartInfo.getSkuId());
                cartInfo.setSkuPrice(skuPrice);
                return cartInfo;
            }));
            redisTemplate.boundHashOps(cartKey).putAll(map);

            return cartInfoList;
        }
        return cartInfoList;
    }
    /**
     * 删除购物车
     * @param userTempId
     */
    public void deleteCart(String userTempId){
        //先删数据库
        Integer result=cartMapper.deleteByuserTempId(userTempId);
        String cartKey = getCartKey(userTempId);
        if (redisTemplate.hasKey(cartKey)){
            redisTemplate.delete(cartKey);
        }
    }
    // 设置过期时间
    private void setCartKeyExpire(String cartKey) {
        redisTemplate.expire(cartKey, RedisConst.USER_CART_EXPIRE, TimeUnit.SECONDS);
    }
    // 获取购物车的key
    private String getCartKey(String userId) {
        //定义key user:userId:cart
        return RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX;
    }
}
