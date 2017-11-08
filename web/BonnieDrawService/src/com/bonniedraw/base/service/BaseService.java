package com.bonniedraw.base.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.bonniedraw.notification.dao.NotificationMsgMapper;
import com.bonniedraw.notification.model.NotificationMsg;
import com.bonniedraw.util.LogUtils;
import com.bonniedraw.util.TimerUtil;

public class BaseService {
	
	@Autowired
	public DataSourceTransactionManager transactionManager;
	
	@Autowired
	public NotificationMsgMapper notificationMsgMapper;
	
	public void callRollBack(){
		TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	}
	
	public void insertNotificationMsg(int notiMsgType, int userId, int userIdFollow, Integer worksId, Integer worksMsgId){
		NotificationMsg insertVO = new NotificationMsg();
		insertVO.setNotiMsgType(notiMsgType);
		insertVO.setUserId(userId);
		insertVO.setUserIdFollow(userIdFollow);
		if(notiMsgType == 3 || notiMsgType == 4 || notiMsgType == 5){
			insertVO.setWorksId(worksId);
			insertVO.setWorksMsgId(worksMsgId);
		}
		insertVO.setThirdType(0);
		insertVO.setCreationDate(TimerUtil.getNowDate());
		try {
			notificationMsgMapper.insert(insertVO);
		} catch (Exception e) {
			LogUtils.error(getClass(), "insertNotificationMsg has error : " + e);
		}
	}
	
}
