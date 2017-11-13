package com.bonniedraw.web_api.module;

import java.sql.Date;

public class NotiMsgResponse {
	private Integer notiMsgId;
	private String message;
	private Integer notiMsgType;
	private Integer userIdFollow;
	private String userNameFollow;
	private String profilePicture;
	private Integer worksId;
	private String title;
	private String imagePath;
	private Integer worksMsgId;
	private String worksMsg;
	private Date creationDate;

	public Integer getNotiMsgId() {
		return notiMsgId;
	}

	public void setNotiMsgId(Integer notiMsgId) {
		this.notiMsgId = notiMsgId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getNotiMsgType() {
		return notiMsgType;
	}

	public void setNotiMsgType(Integer notiMsgType) {
		this.notiMsgType = notiMsgType;
	}

	public Integer getUserIdFollow() {
		return userIdFollow;
	}

	public void setUserIdFollow(Integer userIdFollow) {
		this.userIdFollow = userIdFollow;
	}

	public String getUserNameFollow() {
		return userNameFollow;
	}

	public void setUserNameFollow(String userNameFollow) {
		this.userNameFollow = userNameFollow;
	}

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	public Integer getWorksId() {
		return worksId;
	}

	public void setWorksId(Integer worksId) {
		this.worksId = worksId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public Integer getWorksMsgId() {
		return worksMsgId;
	}

	public void setWorksMsgId(Integer worksMsgId) {
		this.worksMsgId = worksMsgId;
	}

	public String getWorksMsg() {
		return worksMsg;
	}

	public void setWorksMsg(String worksMsg) {
		this.worksMsg = worksMsg;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

}
