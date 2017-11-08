package com.bonniedraw.notification.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonniedraw.base.service.BaseService;
import com.bonniedraw.notification.dao.NotiMsgInfoMapper;
import com.bonniedraw.notification.dao.NotificationMsgMapper;
import com.bonniedraw.notification.service.NotiMsgService;
import com.bonniedraw.web_api.model.request.NotiMsgRequestVO;
import com.bonniedraw.web_api.module.NotiMsgResponse;

@Service
public class NotiMsgServiceImpl extends BaseService implements NotiMsgService {

	@Autowired
	NotiMsgInfoMapper notiMsgInfoMapper;
	
	@Autowired
	NotificationMsgMapper notificationMsgMapper;
	
	@Override
	public List<NotiMsgResponse> getNotiMsgList(NotiMsgRequestVO notiMsgRequestVO) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", notiMsgRequestVO.getUi());
		paramMap.put("notiMsgId", notiMsgRequestVO.getNotiMsgId());
		return notificationMsgMapper.getNotiMsgList(paramMap);
	}

}
