package com.bonniedraw.web_api.model.request;

import java.util.List;

import com.bonniedraw.web_api.model.ApiRequestVO;

public class FriendRequestVO extends ApiRequestVO {
	private int thirdPlatform;
	private List<String> uidList;
	public int getThirdPlatform() {
		return thirdPlatform;
	}
	public void setThirdPlatform(int thirdPlatform) {
		this.thirdPlatform = thirdPlatform;
	}
	public List<String> getUidList() {
		return uidList;
	}
	public void setUidList(List<String> uidList) {
		this.uidList = uidList;
	}

}
