package com.bonniedraw.works.dao;

import java.util.List;

import com.bonniedraw.web_api.module.WorksResponse;
import com.bonniedraw.works.model.WorksTag;
import com.bonniedraw.works.module.TagViewModule;

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
    
    List<WorksTag> findByTagName(String TAG_NAME);
    
    List<TagViewModule> queryTagViewList();
    
    List<TagViewModule> searchTagViewList(String tagName);
    
    List<WorksResponse> queryTagWorkList(List<WorksTag> worksTagList);
    
}