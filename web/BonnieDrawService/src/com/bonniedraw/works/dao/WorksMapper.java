package com.bonniedraw.works.dao;

import java.util.List;
import java.util.Map;

import com.bonniedraw.web_api.module.WorksResponse;
import com.bonniedraw.works.model.Works;

public interface WorksMapper {
    int deleteByPrimaryKey(Integer worksId);

    int insert(Works record);

    int insertSelective(Works record);

    Works selectByPrimaryKey(Integer worksId);

    int updateByPrimaryKeySelective(Works record);

    int updateByPrimaryKey(Works record);
    
    WorksResponse queryWorks(Integer wid);
    
    List<WorksResponse> queryAllWorks();
    
    List<WorksResponse> queryPopularWorks(Integer rc);
    
    int seletMaxPagination(Integer rc);
    
    List<WorksResponse> queryPopularWorksPager(Map<String, Integer> pagerMap);
    
    List<WorksResponse> queryNewUploadWorks(Integer rc);
}