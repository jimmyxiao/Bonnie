package com.bonniedraw.works.service;

import java.util.List;

import com.bonniedraw.works.model.TagInfo;

public interface TagManagerService {
	public List<TagInfo> queryTagList();
	public int createCustomTag(TagInfo tagInfo, Integer passId);
	public TagInfo updateCustomTag(TagInfo tagInfo, Integer passId);
	public int removeCustomTag(TagInfo tagInfo, Integer passId);
}
