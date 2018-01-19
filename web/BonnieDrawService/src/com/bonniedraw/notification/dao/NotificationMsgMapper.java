package com.bonniedraw.notification.dao;

import java.util.List;
import java.util.Map;

import com.bonniedraw.notification.model.NotificationMsg;
import com.bonniedraw.web_api.module.NotiMsgResponse;

public interface NotificationMsgMapper {
    int deleteByPrimaryKey(Integer notiMsgId);

    int insert(NotificationMsg record);

    int insertSelective(NotificationMsg record);

    NotificationMsg selectByPrimaryKey(Integer notiMsgId);

    int updateByPrimaryKeySelective(NotificationMsg record);

    int updateByPrimaryKey(NotificationMsg record);
    
    List<NotiMsgResponse> getNotiMsgList(Map<String, Object> paramMap);

    int deleteByWorksId(int worksId);
}