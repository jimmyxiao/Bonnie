package com.bonniedraw.web_api.model.response;

import java.util.List;

import com.bonniedraw.user.model.UserInfo;
import com.bonniedraw.web_api.model.ApiResponseVO;

public class FriendResponseVO extends ApiResponseVO {
	private List<UserInfo> friendList;

	public List<UserInfo> getFriendList() {
		return friendList;
	}

	public void setFriendList(List<UserInfo> friendList) {
		this.friendList = friendList;
	}
	
}
