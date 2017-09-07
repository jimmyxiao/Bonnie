package com.bonniedraw.web_api.model.request;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.bonniedraw.web_api.model.ApiRequestVO;

public class FileUploadRequestVO extends ApiRequestVO{
	private int wid;
	private int ftype;
	private CommonsMultipartFile file;

	public int getWid() {
		return wid;
	}

	public void setWid(int wid) {
		this.wid = wid;
	}

	public int getFtype() {
		return ftype;
	}

	public void setFtype(int ftype) {
		this.ftype = ftype;
	}

	public CommonsMultipartFile getFile() {
		return file;
	}

	public void setFile(CommonsMultipartFile file) {
		this.file = file;
	}

}
