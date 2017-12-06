package com.bonniedraw.base.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.bonniedraw.login.dao.LoginMapper;
import com.bonniedraw.notification.dao.NotiMsgInfoMapper;
import com.bonniedraw.notification.dao.NotificationMsgMapper;
import com.bonniedraw.notification.model.NotiMsgInfo;
import com.bonniedraw.notification.model.NotificationMsg;
import com.bonniedraw.notification.service.impl.PushNotificationsServiceImpl;
import com.bonniedraw.user.dao.UserInfoMapper;
import com.bonniedraw.user.model.UserInfo;
import com.bonniedraw.util.LogUtils;
import com.bonniedraw.util.TimerUtil;
import com.bonniedraw.util.ValidateUtil;
import com.bonniedraw.works.dao.WorksMapper;
import com.bonniedraw.works.model.Works;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BaseService extends PushNotificationsServiceImpl{
	
	private final String TOPIC = "/topics/";
	
	@Autowired
	public DataSourceTransactionManager transactionManager;
	
	@Autowired
	NotificationMsgMapper notificationMsgMapper;
	
	@Autowired
	NotiMsgInfoMapper notiMsgInfoMapper;
	
	@Autowired
	UserInfoMapper userInfoMapper;
	
	@Autowired
	WorksMapper worksMapper;
	
	@Autowired
	LoginMapper loginMapper;
	
	
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
			
			List<String> registerTokens = loginMapper.selectTokenByUserIdAndCurrentIsTrue(userId);
			if(ValidateUtil.isNotEmptyAndSize(registerTokens)){
				pushNotificationMsg(notiMsgType, userId, userIdFollow, worksId, registerTokens);
			}
		} catch (Exception e) {
			LogUtils.error(getClass(), "insertNotificationMsg has error : " + e);
		}
	}
	
	private boolean pushNotificationMsg(int notiMsgType, int userId, int userIdFollow, Integer worksId, List<String> registerTokens){
		try {
			UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
			if(userInfo!=null){							
				NotiMsgInfo notiMsgInfo = new NotiMsgInfo();
				notiMsgInfo.setNotiMsgType(notiMsgType);
				if(userInfo.getLanguageCode()!=null){
					notiMsgInfo.setLanguageCode(userInfo.getLanguageCode());
				}else{
					notiMsgInfo.setLanguageCode("en");
				}
				
				notiMsgInfo = notiMsgInfoMapper.selectByPrimaryKey(notiMsgInfo);
				if(notiMsgInfo!=null && notiMsgInfo.getMessage1()!=null){
					UserInfo followUserInfo = userInfoMapper.selectByPrimaryKey(userId);
					if(followUserInfo!=null){
						notiMsgInfo.setMessage1(notiMsgInfo.getMessage1().replace("xxx", followUserInfo.getUserName() ));
					}
					if(notiMsgInfo.getMessage1().indexOf("www")!=-1){
						if(worksId!=null){
							Works works = worksMapper.selectByPrimaryKey(worksId);
							if(works!=null){
								notiMsgInfo.setMessage1(notiMsgInfo.getMessage1().replace("www", works.getTitle()));
							}
						}
					}
					
					String notiBody = "";
					switch (notiMsgType) {
					case 1:
						notiBody = "被追踨通知";
						break;
					case 2:
						notiBody = "朋友加入通知";
						break;
					case 3:
						notiBody = "留言通知";
						break;
					case 4:
						notiBody = "追蹤人發表通知";
						break;
					case 5:
						notiBody = "作品按讚通知";
						break;
					}
					
//					String groupName = String.valueOf(userId) + "_" + userInfo.getUserCode();
//					String notificationKey = createGroup(registerTokens, groupName);
//					if(notificationKey!=null){
						ObjectMapper mapper = new ObjectMapper();
						JsonNode notification = mapper.createObjectNode();
						JsonNode sendBody = mapper.createObjectNode();		
						((ObjectNode)notification).put("title", notiBody);
						((ObjectNode)notification).put("body", notiMsgInfo.getMessage1());
						((ObjectNode) sendBody).put("priority", "high");
						((ObjectNode) sendBody).set("notification", notification);	
//						((ObjectNode) sendBody).put("to", notificationKey);
//						JsonNode data = mapper.createObjectNode();
//						((ObjectNode)data).put("Key-1", "JSA Data 1");
//						((ObjectNode)data).put("Key-2", "JSA Data 2");
//						((ObjectNode) body).put("to", TOPIC + userInfo.getUserId());
//						((ObjectNode) body).set("data", data);
						for(String registerId : registerTokens){
							((ObjectNode) sendBody).put("to", registerId);
							String  jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(sendBody);
							HttpEntity<String> request = new HttpEntity<>(jsonString);
							try {
								LogUtils.info(getClass(), "start send notification message ==> " +  registerId);
								CompletableFuture<String> pushNotification = send(request);
								CompletableFuture.allOf(pushNotification).join();
								String firebaseResponse = pushNotification.get();
								if(firebaseResponse!=null){}
							} catch (Exception e) {
								LogUtils.error(getClass(), "send notification message has error : " + e);
							}
						}
//					}
				}
			}
			return true;
		} catch (Exception e) {
			LogUtils.error(getClass(), "pushNotificationMsg has error : " + e);
		}
		return false;
	}
	
}
