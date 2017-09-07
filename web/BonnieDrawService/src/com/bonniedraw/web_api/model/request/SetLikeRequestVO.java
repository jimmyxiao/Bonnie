package com.bonniedraw.web_api.model.request;

import com.bonniedraw.web_api.model.ApiRequestVO;

public class SetLikeRequestVO extends ApiRequestVO{
	private int worksId;
	private int likeType;

	public int getWorksId() {
		return worksId;
	}

	public void setWorksId(int worksId) {
		this.worksId = worksId;
	}

	public int getLikeType() {
		return likeType;
	}

	public void setLikeType(int likeType) {
		this.likeType = likeType;
	}

}
