package com.bonniedraw.works.dao;

import java.util.List;

import com.bonniedraw.works.model.TurnIn;

public interface TurnInMapper {
    int deleteByPrimaryKey(Integer turnInId);

    int insert(TurnIn record);

    int insertSelective(TurnIn record);

    TurnIn selectByPrimaryKey(Integer turnInId);

    int updateByPrimaryKeySelective(TurnIn record);

    int updateByPrimaryKey(TurnIn record);
    
    int updateStatusByPrimaryKey(TurnIn record);
    
    int updateStatusByPrimaryKey();
    
    List<TurnIn> queryTurnInList(Integer worksId);
}