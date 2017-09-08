package com.bonniedraw.web_api.model.request;

public class SetTurnInRequestVO{
	private int ui;
	private String lk;
	private int worksId;
	private int turnInType;
	private String description;

	public int getUi() {
		return ui;
	}

	public void setUi(int ui) {
		this.ui = ui;
	}

	public String getLk() {
		return lk;
	}

	public void setLk(String lk) {
		this.lk = lk;
	}

	public int getWorksId() {
		return worksId;
	}

	public void setWorksId(int worksId) {
		this.worksId = worksId;
	}

	public int getTurnInType() {
		return turnInType;
	}

	public void setTurnInType(int turnInType) {
		this.turnInType = turnInType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
