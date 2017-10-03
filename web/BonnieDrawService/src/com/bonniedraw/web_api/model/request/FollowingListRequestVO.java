package com.bonniedraw.web_api.model.request;

import com.bonniedraw.web_api.model.ApiRequestVO;

public class FollowingListRequestVO extends ApiRequestVO{
	private int fn;

	public int getFn() {
		return fn;
	}

	public void setFn(int fn) {
		this.fn = fn;
	}
	
}
