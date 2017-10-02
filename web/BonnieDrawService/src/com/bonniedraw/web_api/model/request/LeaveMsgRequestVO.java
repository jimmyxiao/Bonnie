package com.bonniedraw.web_api.model.request;

import com.bonniedraw.web_api.model.ApiRequestVO;

public class LeaveMsgRequestVO extends ApiRequestVO{
	private int fn;
	private int worksId;
	private Integer msgId;
	private String message;
	
	public int getFn() {
		return fn;
	}

	public void setFn(int fn) {
		this.fn = fn;
	}

	public int getWorksId() {
		return worksId;
	}

	public void setWorksId(int worksId) {
		this.worksId = worksId;
	}

	public Integer getMsgId() {
		return msgId;
	}

	public void setMsgId(Integer msgId) {
		this.msgId = msgId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
