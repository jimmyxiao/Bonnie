package com.bonniedraw.works.dao;

import com.bonniedraw.works.model.WorksCategory;

public interface WorksCategoryMapper {
    int deleteByPrimaryKey(Integer worksCategoryId);

    int insert(WorksCategory record);

    int insertSelective(WorksCategory record);

    WorksCategory selectByPrimaryKey(Integer worksCategoryId);

    int updateByPrimaryKeySelective(WorksCategory record);

    int updateByPrimaryKey(WorksCategory record);
}