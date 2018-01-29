package com.bonniedraw.user.service;

import java.util.List;

import com.bonniedraw.user.model.UserInfo;
import com.bonniedraw.web_api.model.response.UserInfoQueryResponseVO;

public interface WebUserService {
	public int register(UserInfo userInfo);
	public int registerComplete(String token);
	public List<UserInfo> queryUserList(UserInfo searchInfo);
	public UserInfoQueryResponseVO queryUserDetail(UserInfo searchInfo);
	public UserInfo changeStatus(UserInfo userInfo);
	public UserInfo changeUserGroup(UserInfo userInfo);
}
