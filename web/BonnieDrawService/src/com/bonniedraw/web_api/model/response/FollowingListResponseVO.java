package com.bonniedraw.web_api.model.response;

import java.util.List;

import com.bonniedraw.web_api.model.ApiResponseVO;
import com.bonniedraw.web_api.module.UserInfoResponse;

public class FollowingListResponseVO extends ApiResponseVO {
	private List<UserInfoResponse> userList;

	public List<UserInfoResponse> getUserList() {
		return userList;
	}

	public void setUserList(List<UserInfoResponse> userList) {
		this.userList = userList;
	}

}
