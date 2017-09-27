package com.bonniedraw.web_api.model.request;

import java.util.List;

import com.bonniedraw.web_api.model.ApiRequestVO;

public class FriendRequestVO extends ApiRequestVO {
	private int thirdPlatform;
	private List<Integer> uidList;

	public int getThirdPlatform() {
		return thirdPlatform;
	}

	public void setThirdPlatform(int thirdPlatform) {
		this.thirdPlatform = thirdPlatform;
	}

	public List<Integer> getUidList() {
		return uidList;
	}

	public void setUidList(List<Integer> uidList) {
		this.uidList = uidList;
	}

}
