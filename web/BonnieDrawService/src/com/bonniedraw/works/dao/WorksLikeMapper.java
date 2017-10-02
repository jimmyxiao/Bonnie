package com.bonniedraw.works.dao;

import com.bonniedraw.works.model.WorksLike;

public interface WorksLikeMapper {
    int deleteByPrimaryKey(Integer worksLikeId);

    int insert(WorksLike record);

    int insertSelective(WorksLike record);

    WorksLike selectByPrimaryKey(Integer worksLikeId);

    int updateByPrimaryKeySelective(WorksLike record);

    int updateByPrimaryKey(WorksLike record);
    
    int deleteByNotPrimaryKey(WorksLike record);
}