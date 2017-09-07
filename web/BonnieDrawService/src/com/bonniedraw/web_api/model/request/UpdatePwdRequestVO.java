package com.bonniedraw.web_api.model.request;

import com.bonniedraw.web_api.model.ApiRequestVO;

public class UpdatePwdRequestVO extends ApiRequestVO{
	private int oldPwd;
	private int newPwd;

	public int getOldPwd() {
		return oldPwd;
	}

	public void setOldPwd(int oldPwd) {
		this.oldPwd = oldPwd;
	}

	public int getNewPwd() {
		return newPwd;
	}

	public void setNewPwd(int newPwd) {
		this.newPwd = newPwd;
	}

}
