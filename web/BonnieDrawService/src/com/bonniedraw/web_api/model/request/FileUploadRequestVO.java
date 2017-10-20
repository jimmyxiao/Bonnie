package com.bonniedraw.web_api.model.request;

import com.bonniedraw.web_api.model.ApiRequestVO;

public class FileUploadRequestVO extends ApiRequestVO{
	private int fn;
	private Integer wid;
	private Integer ftype;
//	private CommonsMultipartFile file;

	public int getFn() {
		return fn;
	}

	public void setFn(int fn) {
		this.fn = fn;
	}

	public Integer getWid() {
		return wid;
	}

	public void setWid(Integer wid) {
		this.wid = wid;
	}

	public Integer getFtype() {
		return ftype;
	}

	public void setFtype(Integer ftype) {
		this.ftype = ftype;
	}

//	public CommonsMultipartFile getFile() {
//		return file;
//	}
//
//	public void setFile(CommonsMultipartFile file) {
//		this.file = file;
//	}

}
