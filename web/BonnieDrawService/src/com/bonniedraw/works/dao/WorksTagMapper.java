package com.bonniedraw.works.dao;

import com.bonniedraw.works.model.WorksTag;

public interface WorksTagMapper {
    int deleteByPrimaryKey(Integer worksTagId);

    int insert(WorksTag record);

    int insertSelective(WorksTag record);

    WorksTag selectByPrimaryKey(Integer worksTagId);

    int updateByPrimaryKeySelective(WorksTag record);

    int updateByPrimaryKey(WorksTag record);
}