package com.bonniedraw.commodityinfo.dao;

import com.bonniedraw.commodityinfo.model.CommodityInfo;

public interface CommodityInfoMapper {
    int deleteByPrimaryKey(Integer commodityId);

    int insert(CommodityInfo record);

    int insertSelective(CommodityInfo record);

    CommodityInfo selectByPrimaryKey(Integer commodityId);

    int updateByPrimaryKeySelective(CommodityInfo record);

    int updateByPrimaryKey(CommodityInfo record);

    int insertByWorksId(CommodityInfo record);
}