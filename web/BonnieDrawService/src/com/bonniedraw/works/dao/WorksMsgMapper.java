package com.bonniedraw.works.dao;

import com.bonniedraw.works.model.WorksMsg;

public interface WorksMsgMapper {
    int deleteByPrimaryKey(Integer worksMsgId);

    int insert(WorksMsg record);

    int insertSelective(WorksMsg record);

    WorksMsg selectByPrimaryKey(Integer worksMsgId);

    int updateByPrimaryKeySelective(WorksMsg record);

    int updateByPrimaryKey(WorksMsg record);
    
    WorksMsg selectExistMsg(WorksMsg record);
    
}