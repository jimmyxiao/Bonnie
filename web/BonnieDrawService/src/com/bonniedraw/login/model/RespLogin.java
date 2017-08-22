package com.bonniedraw.login.model;

import com.bonniedraw.base.model.BaseModel;

public class RespLogin extends BaseModel {
	private int status;
	private int index;
	
	public RespLogin() {
		this.status = 1;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
