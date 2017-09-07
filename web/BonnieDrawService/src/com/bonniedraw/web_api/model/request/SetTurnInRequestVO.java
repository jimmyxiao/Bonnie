package com.bonniedraw.web_api.model.request;

import com.bonniedraw.web_api.model.ApiRequestVO;

public class SetTurnInRequestVO extends ApiRequestVO{
	private int worksId;

	public int getWorksId() {
		return worksId;
	}

	public void setWorksId(int worksId) {
		this.worksId = worksId;
	}

}
