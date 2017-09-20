package com.bonniedraw.user.service;

import java.util.List;

import com.bonniedraw.user.model.UserInfo;

public interface WebUserService {
	public int register(UserInfo userInfo);
	public int registerComplete(String token);
	public List<UserInfo> queryUserList();
}
