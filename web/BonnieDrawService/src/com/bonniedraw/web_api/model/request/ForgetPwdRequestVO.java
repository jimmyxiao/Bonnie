package com.bonniedraw.web_api.model.request;

public class ForgetPwdRequestVO {
	private String email;
	private String mask;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

}
