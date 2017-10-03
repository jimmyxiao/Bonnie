package com.bonniedraw.works.dao;

import com.bonniedraw.works.model.WorksCollection;

public interface WorksCollectionMapper {
    int deleteByPrimaryKey(Integer worksCollectionId);

    int insert(WorksCollection record);

    int insertSelective(WorksCollection record);

    WorksCollection selectByPrimaryKey(Integer worksCollectionId);

    int updateByPrimaryKeySelective(WorksCollection record);

    int updateByPrimaryKey(WorksCollection record);
    
    WorksCollection selectByWorksAndUser(WorksCollection record);
}