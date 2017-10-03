package com.bonniedraw.web_api.module;

import java.util.List;

public class CategoryInfoResponse {
	private int categoryId;
	private String categoryName;
	private int categoryLevel;
	private List<CategoryInfoResponse> categoryList;

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public int getCategoryLevel() {
		return categoryLevel;
	}

	public void setCategoryLevel(int categoryLevel) {
		this.categoryLevel = categoryLevel;
	}

	public List<CategoryInfoResponse> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<CategoryInfoResponse> categoryList) {
		this.categoryList = categoryList;
	}
}
