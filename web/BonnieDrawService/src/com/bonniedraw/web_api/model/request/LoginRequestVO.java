package com.bonniedraw.web_api.model.request;

public class LoginRequestVO {
	private String uc;
	private String up;
	private String un;
	private int ut;
	private int dt;
	private int fn;
	private String thirdEmail;
	private String thirdPictureUrl;
	private Integer gender;
	private String phoneNo;
	private String captcha;
	private String mask;
	private String languageCode;
	private String countryCode;
	private String token;
	private String deviceId;
	
	public String getUc() {
		return uc;
	}

	public void setUc(String uc) {
		this.uc = uc;
	}

	public String getUp() {
		return up;
	}

	public void setUp(String up) {
		this.up = up;
	}

	public String getUn() {
		return un;
	}

	public void setUn(String un) {
		this.un = un;
	}

	public int getUt() {
		return ut;
	}

	public void setUt(int ut) {
		this.ut = ut;
	}

	public int getDt() {
		return dt;
	}

	public void setDt(int dt) {
		this.dt = dt;
	}

	public int getFn() {
		return fn;
	}

	public void setFn(int fn) {
		this.fn = fn;
	}
	
	public String getThirdEmail() {
		return thirdEmail;
	}

	public void setThirdEmail(String thirdEmail) {
		this.thirdEmail = thirdEmail;
	}

	public String getThirdPictureUrl() {
		return thirdPictureUrl;
	}

	public void setThirdPictureUrl(String thirdPictureUrl) {
		this.thirdPictureUrl = thirdPictureUrl;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
}
