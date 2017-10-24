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
    
    WorksResponse queryWorks(Map<String, Object> paramMap);
    
    List<WorksResponse> queryAllWorks(Map<String, Object> paramMap);
    
    List<WorksResponse> queryNewUploadWorks(Map<String, Object> paramMap);
    
    List<WorksResponse> queryPopularWorks(Map<String, Object> paramMap);
    
    int seletMaxPagination(Integer rc);
    
    int seletMaxPaginationBindUser(Map<String, Integer> pagerMap);
    
    int seletMaxPaginationBindFollow(Map<String, Integer> pagerMap);
    
    int seletMaxPaginationBindCollection(Map<String, Integer> pagerMap);
    
    List<WorksResponse> queryPopularWorksPager(Map<String, Integer> pagerMap);
    
    List<WorksResponse> queryUserWorksPager(Map<String, Integer> pagerMap);
    
    List<WorksResponse> queryOtherUserWorksPager(Map<String, Integer> pagerMap);
    
    List<WorksResponse> queryTrackWorksPager(Map<String, Integer> pagerMap);
    
    List<WorksResponse> queryCollectionWorksPager(Map<String, Integer> pagerMap);
    
    List<WorksResponse> queryCategoryWorksPager(Map<String, Object> pagerMap);
    
}