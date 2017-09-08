package com.bonniedraw.user.service;

import com.bonniedraw.user.model.UserInfo;
import com.bonniedraw.web_api.model.request.LoginRequestVO;
import com.bonniedraw.web_api.model.request.UpdatePwdRequestVO;
import com.bonniedraw.web_api.model.response.LoginResponseVO;

public interface MobileUserService {
	public LoginResponseVO login(LoginRequestVO loginRequestVO);
	public UserInfo queryUserInfo(int userId);
	public int updateUserInfo(UserInfo userInfo);
	public int updatePwd(UpdatePwdRequestVO updatePwdRequestVO);
}
