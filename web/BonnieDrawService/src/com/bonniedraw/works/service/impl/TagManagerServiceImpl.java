package com.bonniedraw.works.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonniedraw.base.service.BaseService;
import com.bonniedraw.util.LogUtils;
import com.bonniedraw.util.ValidateUtil;
import com.bonniedraw.web_api.module.WorksResponse;
import com.bonniedraw.works.dao.TagInfoMapper;
import com.bonniedraw.works.dao.WorksTagMapper;
import com.bonniedraw.works.model.TagInfo;
import com.bonniedraw.works.model.WorksTag;
import com.bonniedraw.works.module.TagViewModule;
import com.bonniedraw.works.service.TagManagerService;

@Service
public class TagManagerServiceImpl extends BaseService implements TagManagerService {

	@Autowired
	TagInfoMapper tagInfoMapper;
	
	@Autowired
	WorksTagMapper worksTagMapper;
	
	@Override
	public List<TagInfo> queryTagList() {
		return tagInfoMapper.getTagList(null);
	}
	
	@Override
	public List<TagViewModule> queryTagViewList() {
		List<TagViewModule> tagViewList = worksTagMapper.queryTagViewList();
		return tagViewList;
	}
	
	@Override
	public List<TagViewModule> searchTagViewList(String tagName) {
		List<TagViewModule> tagViewList = worksTagMapper.searchTagViewList(tagName);
		return tagViewList;
	}

	@Override
	public int createCustomTag(TagInfo tagInfo, Integer passId) {
		List<TagInfo> existTags = tagInfoMapper.getTagList(tagInfo.getTagName());
		if(!ValidateUtil.isNotEmptyAndSize(existTags)){
			if(tagInfo.getTagOrder()==null){
				tagInfo.setTagOrder(0);
			}
			if(ValidateUtil.isBlank(tagInfo.getCountryCode())){
				tagInfo.setCountryCode("2");
			}
		}else{
			return 2;
		}
		
		try {
			tagInfoMapper.insert(tagInfo);
		} catch (Exception e) {
			LogUtils.error(getClass(), "createCustomTag has error : " + e);
			return 0;
		}
		return 1;
	}

	@Override
	public TagInfo updateCustomTag(TagInfo tagInfo, Integer passId) {
		TagInfo afterCustomTag = null;
		try {
			if(tagInfo.getTagOrder()==null){
				tagInfo.setTagOrder(0);
			}
			if(ValidateUtil.isBlank(tagInfo.getCountryCode())){
				tagInfo.setCountryCode("2");
			}
			tagInfoMapper.updateByPrimaryKey(tagInfo);
			afterCustomTag = tagInfoMapper.selectByPrimaryKey(tagInfo.getTagId());
		} catch (Exception e) {
			LogUtils.error(getClass(), "updateCustomTag has error : " + e);
		}
		return afterCustomTag;
	}

	@Override
	public int removeCustomTag(TagInfo tagInfo, Integer passId) {
		int tagId = tagInfo.getTagId(); 
		try {
			tagInfoMapper.deleteByPrimaryKey(tagId);
		} catch (Exception e) {
			LogUtils.error(getClass(), "removeCustomTag has error : " + e);
			callRollBack();
			return 0;
		}
		return 1;
	}

	@Override
	public List<WorksResponse> queryTagWorkList(List<WorksTag> worksTagList) {
		return worksTagMapper.queryTagWorkList(worksTagList);
	}

}
