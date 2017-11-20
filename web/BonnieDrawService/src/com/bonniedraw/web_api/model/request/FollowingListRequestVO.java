package com.bonniedraw.web_api.model.request;

import com.bonniedraw.web_api.model.ApiRequestVO;

public class FollowingListRequestVO extends ApiRequestVO {
	private int fn;
	private Integer stn;
	private Integer rc;

	public int getFn() {
		return fn;
	}

	public void setFn(int fn) {
		this.fn = fn;
	}

	public Integer getStn() {
		return stn;
	}

	public void setStn(Integer stn) {
		this.stn = stn;
	}

	public Integer getRc() {
		return rc;
	}

	public void setRc(Integer rc) {
		this.rc = rc;
	}

}
