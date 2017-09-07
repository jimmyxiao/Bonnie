package com.bonniedraw.web_api.model.request;

import com.bonniedraw.web_api.model.ApiRequestVO;

public class SetFollowingRequestVO extends ApiRequestVO{
	private int fn;
	private int followingUserId;

	public int getFn() {
		return fn;
	}

	public void setFn(int fn) {
		this.fn = fn;
	}

	public int getFollowingUserId() {
		return followingUserId;
	}

	public void setFollowingUserId(int followingUserId) {
		this.followingUserId = followingUserId;
	}

}
