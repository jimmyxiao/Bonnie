package com.bonniedraw.web_api.model.response;

import java.util.List;

import com.bonniedraw.web_api.model.ApiResponseVO;
import com.bonniedraw.web_api.module.NotiMsgResponse;

public class NotiMsgResponseVO extends ApiResponseVO{
	private List<NotiMsgResponse> notiMsgList;

	public List<NotiMsgResponse> getNotiMsgList() {
		return notiMsgList;
	}

	public void setNotiMsgList(List<NotiMsgResponse> notiMsgList) {
		this.notiMsgList = notiMsgList;
	}
	
}
