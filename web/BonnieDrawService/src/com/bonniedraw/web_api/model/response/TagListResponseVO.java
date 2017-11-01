package com.bonniedraw.web_api.model.response;

import java.util.List;

import com.bonniedraw.web_api.model.ApiResponseVO;
import com.bonniedraw.works.model.TagInfo;

public class TagListResponseVO extends ApiResponseVO {
	private List<TagInfo> tagList;

	public List<TagInfo> getTagList() {
		return tagList;
	}

	public void setTagList(List<TagInfo> tagList) {
		this.tagList = tagList;
	}

}
