package com.bonniedraw.web_api.model.request;

import com.bonniedraw.web_api.model.ApiRequestVO;

public class UserInfoQueryRequestVO extends ApiRequestVO {
	private int type;
	private Integer queryId;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Integer getQueryId() {
		return queryId;
	}

	public void setQueryId(Integer queryId) {
		this.queryId = queryId;
	}

}
