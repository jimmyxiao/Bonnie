package com.bonniedraw.web_api.model.response;

import com.bonniedraw.web_api.model.ApiResponseVO;

public class FileUploadResponseVO extends ApiResponseVO {
	private String profilePicture;

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}
	
}
