package com.bonniedraw.web_api.model.response;

import java.util.List;

import com.bonniedraw.web_api.model.ApiResponseVO;
import com.bonniedraw.web_api.module.CategoryInfoResponse;

public class CategoryListResponseVO extends ApiResponseVO{
	private List<CategoryInfoResponse> categoryList;

	public List<CategoryInfoResponse> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<CategoryInfoResponse> categoryList) {
		this.categoryList = categoryList;
	}
	
}
