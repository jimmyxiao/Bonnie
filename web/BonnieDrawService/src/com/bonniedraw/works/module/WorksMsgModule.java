package com.bonniedraw.works.module;

import com.bonniedraw.works.model.WorksMsg;

public class WorksMsgModule extends WorksMsg{
	private String userName;
	private String profilePicture;
	private String commodityUrl;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	public String getCommodityUrl() {
		return commodityUrl;
	}

	public void setCommodityUrl(String commodityUrl) {
		this.commodityUrl = commodityUrl;
	}
	
}
