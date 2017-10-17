package com.bonniedraw.works.dao;

import java.util.List;

import com.bonniedraw.works.model.WorksTag;

public interface WorksTagMapper {
    int deleteByPrimaryKey(Integer worksTagId);

    int insert(WorksTag record);

    int insertSelective(WorksTag record);

    WorksTag selectByPrimaryKey(Integer worksTagId);

    int updateByPrimaryKeySelective(WorksTag record);

    int updateByPrimaryKey(WorksTag record);
    
    int insertWorksTagList(List<WorksTag> tagList);
    
    int updateWorksTagList(List<WorksTag> tagList);
    
    int deleteWorksTagList(List<WorksTag> tagList);
    
    int deleteByWorksId(Integer worksId);
    
    List<WorksTag> selectByWorksId(Integer worksId);
    
    int selectNextOrderNum(Integer worksId);
    
}