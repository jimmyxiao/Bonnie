package com.bonniedraw.web_api.model.request;

import com.bonniedraw.web_api.model.ApiRequestVO;

public class NotiMsgRequestVO extends ApiRequestVO {
	private Integer notiMsgId;
	private String languageCode;

	public Integer getNotiMsgId() {
		return notiMsgId;
	}

	public void setNotiMsgId(Integer notiMsgId) {
		this.notiMsgId = notiMsgId;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

}
