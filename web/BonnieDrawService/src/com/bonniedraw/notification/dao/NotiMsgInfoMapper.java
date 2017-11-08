package com.bonniedraw.notification.dao;

import com.bonniedraw.notification.model.NotiMsgInfo;
import com.bonniedraw.notification.model.NotiMsgInfoKey;

public interface NotiMsgInfoMapper {
    int deleteByPrimaryKey(NotiMsgInfoKey key);

    int insert(NotiMsgInfo record);

    int insertSelective(NotiMsgInfo record);

    NotiMsgInfo selectByPrimaryKey(NotiMsgInfoKey key);

    int updateByPrimaryKeySelective(NotiMsgInfo record);

    int updateByPrimaryKey(NotiMsgInfo record);   
    
}