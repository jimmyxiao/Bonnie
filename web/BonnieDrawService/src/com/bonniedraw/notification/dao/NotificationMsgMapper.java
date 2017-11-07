package com.bonniedraw.notification.dao;

import com.bonniedraw.notification.model.NotificationMsg;

public interface NotificationMsgMapper {
    int deleteByPrimaryKey(Integer notiMsgId);

    int insert(NotificationMsg record);

    int insertSelective(NotificationMsg record);

    NotificationMsg selectByPrimaryKey(Integer notiMsgId);

    int updateByPrimaryKeySelective(NotificationMsg record);

    int updateByPrimaryKey(NotificationMsg record);
}