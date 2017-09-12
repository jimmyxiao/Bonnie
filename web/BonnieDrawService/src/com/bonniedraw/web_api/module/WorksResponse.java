package com.bonniedraw.web_api.module;

import java.util.List;

import com.bonniedraw.works.model.Works;
import com.bonniedraw.works.model.WorksCategory;
import com.bonniedraw.works.model.WorksLike;
import com.bonniedraw.works.model.WorksMsg;

public class WorksResponse extends Works {
	private Integer likeCount;
	private Integer msgCount;
	private List<WorksLike> likeList;
	private List<WorksMsg> msgList;
	private List<WorksCategory> categoryList;

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

}