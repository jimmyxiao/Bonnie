package com.bonniedraw.works.dao;

import java.util.List;
import java.util.Map;

import com.bonniedraw.works.model.Following;

public interface FollowingMapper {
    int deleteByPrimaryKey(Integer followingId);

    int insert(Following record);

    int insertSelective(Following record);

    Following selectByPrimaryKey(Integer followingId);

    int updateByPrimaryKeySelective(Following record);

    int updateByPrimaryKey(Following record);
    
    int deleteByNotPrimaryKey(Following record);
    
    List<Integer> selectTrackOrFans(Map<String, Object> paramMap);
}