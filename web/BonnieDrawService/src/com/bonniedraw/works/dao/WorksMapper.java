package com.bonniedraw.works.dao;

import java.util.List;
import java.util.Map;

import com.bonniedraw.web_api.module.WorksResponse;
import com.bonniedraw.works.model.Works;
import com.bonniedraw.works.module.SearchWorkModule;

public interface WorksMapper {
    int deleteByPrimaryKey(Integer worksId);

    int insert(Works record);

    int insertSelective(Works record);

    Works selectByPrimaryKey(Integer worksId);

    int updateByPrimaryKeySelective(Works record);

    int updateByPrimaryKey(Works record);
    
    int updateStatusByPrimaryKey(Works record);
    
    WorksResponse queryWorks(Map<String, Object> paramMap);
    
    List<WorksResponse> queryAllWorks(Map<String, Object> paramMap);
    
    List<WorksResponse> queryNewUploadWorks(Map<String, Object> paramMap);
    
    List<WorksResponse> queryUserWorks(Map<String, Object> paramMap);
    
    List<WorksResponse> queryPopularWorks(Map<String, Object> paramMap);
    
    List<WorksResponse> queryOtherUserWorks(Map<String, Object> paramMap);
    
    List<WorksResponse> queryCollectionWorks(Map<String, Object> paramMap);
    
    List<WorksResponse> queryRelatedTagWorks(Map<String, Object> paramMap);
    
    List<WorksResponse> querySearchWorks(Map<String, Object> paramMap);
    
    int seletMaxPagination(Integer rc);
    
    int seletMaxPaginationBindUser(Map<String, Object> pagerMap);
    
    int seletMaxPaginationBindFollow(Map<String, Object> pagerMap);
    
    int seletMaxPaginationBindCollection(Map<String, Object> pagerMap);
    
    int seletMaxPaginationBindTagName(Map<String, Object> pagerMap);
    
    int seletMaxPaginationBindSearch(Map<String, Object> pagerMap);
    
    List<WorksResponse> queryPopularWorksPager(Map<String, Object> pagerMap);
    
    List<WorksResponse> queryUserWorksPager(Map<String, Object> pagerMap);
    
    List<WorksResponse> queryOtherUserWorksPager(Map<String, Object> pagerMap);
    
    List<WorksResponse> queryTrackWorksPager(Map<String, Object> pagerMap);
    
    List<WorksResponse> queryCollectionWorksPager(Map<String, Object> pagerMap);
    
    List<WorksResponse> queryCategoryWorksPager(Map<String, Object> pagerMap);
    
    List<WorksResponse> queryRelatedTagWorksPager(Map<String, Object> pagerMap);
    
    List<WorksResponse> querySearchWorksPager(Map<String, Object> pagerMap);
    
    List<WorksResponse> queryWorkListBySearchWorkModule(SearchWorkModule searchWorkModule);
    
    WorksResponse queryWorkDetail(Works works);
    
    List<WorksResponse> queryTurnInWorkList();
    
}