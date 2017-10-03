package com.bonniedraw.user.service;

import java.util.List;

import com.bonniedraw.user.model.UserInfo;
import com.bonniedraw.web_api.model.ApiRequestVO;
import com.bonniedraw.web_api.model.request.LoginRequestVO;
import com.bonniedraw.web_api.model.request.UpdatePwdRequestVO;
import com.bonniedraw.web_api.model.response.FriendResponseVO;
import com.bonniedraw.web_api.model.response.LoginResponseVO;
import com.bonniedraw.web_api.module.UserInfoResponse;

public interface UserServiceAPI {
	public boolean isLogin(ApiRequestVO apiRequestVO);
	public LoginResponseVO login(LoginRequestVO loginRequestVO, String ipAddress);
	public UserInfo queryUserInfo(int userId);
	public int updateUserInfo(UserInfo userInfo);
	public int updatePwd(UpdatePwdRequestVO updatePwdRequestVO);
	public FriendResponseVO getUserFriendsList(int userId, int thirdPlatform, List<Integer> uidList);
	public List<UserInfoResponse> getFollowingList(int fn, int userId);
}
