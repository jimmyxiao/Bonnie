package com.bonniedraw.web_api.model.request;

import java.util.List;

import com.bonniedraw.web_api.model.ApiRequestVO;
import com.bonniedraw.works.model.CategoryInfo;

public class WorksSaveRequestVO extends ApiRequestVO{
	private int ac;
	private int userId;
	private int privacyType;
	private Integer worksId;
	private String title;
	private String description;
	private String languageCode;
	private String countryCode;
	private List<CategoryInfo> categoryList;

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

	public Integer getWorksId() {
		return worksId;
	}

	public void setWorksId(Integer worksId) {
		this.worksId = worksId;
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

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public List<CategoryInfo> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<CategoryInfo> categoryList) {
		this.categoryList = categoryList;
	}

}
