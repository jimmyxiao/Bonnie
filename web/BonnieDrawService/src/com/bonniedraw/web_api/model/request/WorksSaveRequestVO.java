package com.bonniedraw.web_api.model.request;

import java.util.List;

import com.bonniedraw.web_api.model.ApiRequestVO;
import com.bonniedraw.works.model.WorksCategory;

public class WorksSaveRequestVO extends ApiRequestVO{
	private int ac;
	private int userId;
	private int privacyType;
	private String title;
	private String description;
	private Integer languageId;
	private Integer countryId;
	private List<WorksCategory> categoryList;

	public int getAc() {
		return ac;
	}

	public void setAc(int ac) {
		this.ac = ac;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getPrivacyType() {
		return privacyType;
	}

	public void setPrivacyType(int privacyType) {
		this.privacyType = privacyType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getLanguageId() {
		return languageId;
	}

	public void setLanguageId(Integer languageId) {
		this.languageId = languageId;
	}

	public Integer getCountryId() {
		return countryId;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	public List<WorksCategory> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<WorksCategory> categoryList) {
		this.categoryList = categoryList;
	}

}
