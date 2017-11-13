package com.bonniedraw.notification.service;

import java.util.List;

import com.bonniedraw.web_api.model.request.NotiMsgRequestVO;
import com.bonniedraw.web_api.module.NotiMsgResponse;

public interface NotiMsgService {
	public List<NotiMsgResponse> getNotiMsgList(NotiMsgRequestVO notiMsgRequestVO);
}
