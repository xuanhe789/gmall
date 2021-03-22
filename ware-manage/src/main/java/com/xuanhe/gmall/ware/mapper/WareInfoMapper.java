package com.xuanhe.gmall.ware.mapper;

import com.xuanhe.gmall.ware.bean.WareInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @param
 * @return
 */
@Mapper
public interface WareInfoMapper extends BaseMapper<WareInfo> {


    public List<WareInfo> selectWareInfoBySkuid(String skuid);



}
