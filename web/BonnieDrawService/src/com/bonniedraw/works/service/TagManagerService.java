package com.bonniedraw.works.service;

import java.util.List;

import com.bonniedraw.web_api.module.WorksResponse;
import com.bonniedraw.works.model.TagInfo;
import com.bonniedraw.works.model.WorksTag;
import com.bonniedraw.works.module.TagViewModule;

public interface TagManagerService {
	public List<TagInfo> queryTagList();
	public List<TagViewModule> queryTagViewList();
	public int createCustomTag(TagInfo tagInfo, Integer passId);
	public TagInfo updateCustomTag(TagInfo tagInfo, Integer passId);
	public int removeCustomTag(TagInfo tagInfo, Integer passId);
	public List<WorksResponse> queryTagWorkList(List<WorksTag> worksTagList);
}
