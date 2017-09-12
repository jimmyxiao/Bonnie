package com.bonniedraw.web_api.model.response;

import com.bonniedraw.user.model.UserInfo;
import com.bonniedraw.web_api.model.ApiResponseVO;

public class LoginResponseVO extends ApiResponseVO{
	private int ui;
	private int ut;
	private String lk;
	private String sk;
	private String appVersionCode;
	private String appUrl;
	
	// for web	
	private UserInfo userInfo;

	public int getUi() {
		return ui;
	}

	public void setUi(int ui) {
		this.ui = ui;
	}

	public int getUt() {
		return ut;
	}

	public void setUt(int ut) {
		this.ut = ut;
	}

	public String getLk() {
		return lk;
	}

	public void setLk(String lk) {
		this.lk = lk;
	}

	public String getSk() {
		return sk;
	}

	public void setSk(String sk) {
		this.sk = sk;
	}

	public String getAppVersionCode() {
		return appVersionCode;
	}

	public void setAppVersionCode(String appVersionCode) {
		this.appVersionCode = appVersionCode;
	}

	public String getAppUrl() {
		return appUrl;
	}

	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

}
