package com.bonniedraw.login.module;

import com.bonniedraw.base.model.BaseVO;
import com.bonniedraw.user.model.UserInfo;

public class LoginOutput extends BaseVO{
	private Integer status;
	private String securityKey;
	private UserInfo userInfo;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getSecurityKey() {
		return securityKey;
	}

	public void setSecurityKey(String securityKey) {
		this.securityKey = securityKey;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

}
