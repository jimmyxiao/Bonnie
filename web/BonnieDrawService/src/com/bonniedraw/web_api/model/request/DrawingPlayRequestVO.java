package com.bonniedraw.web_api.model.request;

import com.bonniedraw.web_api.model.ApiRequestVO;

public class DrawingPlayRequestVO extends ApiRequestVO {
	private int wid;
	private Integer stn;
	private Integer rc;

	public int getWid() {
		return wid;
	}

	public void setWid(int wid) {
		this.wid = wid;
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
