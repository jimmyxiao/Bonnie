package com.bonniedraw.web_api.model.request;

import com.bonniedraw.web_api.model.ApiRequestVO;

public class SetCollectionRequestVO extends ApiRequestVO {
	private int fn;
	private int worksId;

	public int getFn() {
		return fn;
	}

	public void setFn(int fn) {
		this.fn = fn;
	}

	public int getWorksId() {
		return worksId;
	}

	public void setWorksId(int worksId) {
		this.worksId = worksId;
	}

}
