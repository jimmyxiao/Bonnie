package com.bonniedraw.web_api.module;

import java.util.List;

import com.bonniedraw.works.model.Works;
import com.bonniedraw.works.model.WorksCategory;
import com.bonniedraw.works.model.WorksLike;
import com.bonniedraw.works.model.WorksMsg;

public class WorksResponse extends Works {
	private String userName;
	private String commodityUrl;
	private String profilePicture;
	private Integer likeCount;
	private Integer msgCount;
	private List<WorksLike> likeList;
	private List<WorksMsg> msgList;
	private List<WorksCategory> categoryList;
	private boolean isCollection;
	private int isFollowing;
	private boolean isLike;
	private boolean isTurnIn;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCommodityUrl() {
		return commodityUrl;
	}

	public void setCommodityUrl(String commodityUrl) {
		this.commodityUrl = commodityUrl;
	}

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	public Integer getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(Integer likeCount) {
		this.likeCount = likeCount;
	}

	public Integer getMsgCount() {
		return msgCount;
	}

	public void setMsgCount(Integer msgCount) {
		this.msgCount = msgCount;
	}

	public List<WorksLike> getLikeList() {
		return likeList;
	}

	public void setLikeList(List<WorksLike> likeList) {
		this.likeList = likeList;
	}

	public List<WorksMsg> getMsgList() {
		return msgList;
	}

	public void setMsgList(List<WorksMsg> msgList) {
		this.msgList = msgList;
	}

	public List<WorksCategory> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<WorksCategory> categoryList) {
		this.categoryList = categoryList;
	}

	public boolean isCollection() {
		return isCollection;
	}

	public void setCollection(boolean isCollection) {
		this.isCollection = isCollection;
	}

	public int getIsFollowing() {
		return isFollowing;
	}

	public void setIsFollowing(int isFollowing) {
		this.isFollowing = isFollowing;
	}

	public boolean isLike() {
		return isLike;
	}

	public void setLike(boolean isLike) {
		this.isLike = isLike;
	}

	public boolean isTurnIn() {
		return isTurnIn;
	}

	public void setTurnIn(boolean isTurnIn) {
		this.isTurnIn = isTurnIn;
	}
	
}
