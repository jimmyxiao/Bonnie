package com.bonniedraw.web_api.model.request;

import com.bonniedraw.web_api.model.ApiRequestVO;

public class WorkListRequestVO extends ApiRequestVO {
	private Integer wid;
	private Integer wt;
	private Integer stn;
	private Integer rc;
	private Integer queryId;
	private String tagName;
	private String search;

	public Integer getWid() {
		return wid;
	}

	public void setWid(Integer wid) {
		this.wid = wid;
	}

	public Integer getWt() {
		return wt;
	}

	public void setWt(Integer wt) {
		this.wt = wt;
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

	public Integer getQueryId() {
		return queryId;
	}

	public void setQueryId(Integer queryId) {
		this.queryId = queryId;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}
	
}
