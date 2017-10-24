package com.bonniedraw.works.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bonniedraw.base.service.BaseService;
import com.bonniedraw.util.LogUtils;
import com.bonniedraw.util.ValidateUtil;
import com.bonniedraw.works.dao.CategoryInfoMapper;
import com.bonniedraw.works.dao.NodeTreeMapper;
import com.bonniedraw.works.model.CategoryInfo;
import com.bonniedraw.works.service.DirectoryManagerService;

@Service
public class DirectoryManagerServiceImpl extends BaseService implements
		DirectoryManagerService {

	@Autowired
	CategoryInfoMapper categoryInfoMapper;
	
	@Autowired
	NodeTreeMapper nodeTreeMapper;
	
//	@Override
//	public List<NodeTreeModule> queryDirectoryList() {
//		List<NodeTreeModule> directoryList = nodeTreeMapper.queryDirectoryList();
//		return directoryList;
//	}
	
	@Override
	public List<CategoryInfo> getBreadCrumbs(int categoryId) {
		String parents = categoryInfoMapper.getBreadCrumbs(categoryId);
		List<Integer> categoryIdList = new ArrayList<Integer>();
		List<CategoryInfo> categoryInfoList = new ArrayList<CategoryInfo>();
		if(ValidateUtil.isNotBlank(parents)){
			String splitParents[] = parents.split(",");
			if(splitParents.length>0){
				for(String str : splitParents){
					categoryIdList.add(Integer.valueOf(str));
				}
				categoryInfoList = categoryInfoMapper.selectByPrimaryKeyList(categoryIdList);
			}
		}
		return categoryInfoList;
	}
	
	@Override
	public List<CategoryInfo> queryDirectoryList(Integer categoryParentId) {
		if(categoryParentId == 0){
			categoryParentId = null;
		}
		List<CategoryInfo> directoryList = categoryInfoMapper.queryDirectoryList(categoryParentId);
		return directoryList;
	}
	
	@Override
	public int createDirectory(CategoryInfo categoryInfo, Integer passId) {
		Integer categoeyParentId = categoryInfo.getCategoryParentId();
//		categoryInfo.setEnable(true);
		if( categoeyParentId == 0 || categoeyParentId == null ){
			categoryInfo.setCategoryParentId(null);
			categoryInfo.setCategoryLevel(1);
		}else{
			CategoryInfo parentCategoryInfo = categoryInfoMapper.selectByPrimaryKey(categoeyParentId);
			if(parentCategoryInfo!=null){
				categoryInfo.setCategoryLevel(parentCategoryInfo.getCategoryLevel() + 1);
			}else{
				return 2;
			}
		}
		try {
			categoryInfoMapper.insert(categoryInfo);
		} catch (Exception e) {
			LogUtils.error(getClass(), "createDirectory has error : " + e);
			return 0;
		}
		return 1;
	}

	@Override
	public CategoryInfo updateDirectory(CategoryInfo categoryInfo, Integer passId) {
		CategoryInfo afterCategoryInfo = null;
		try {
			categoryInfoMapper.updateByPrimaryKey(categoryInfo);
			afterCategoryInfo = categoryInfoMapper.selectByPrimaryKey(categoryInfo.getCategoryId());
		} catch (Exception e) {
			LogUtils.error(getClass(), "updateDirectory has error : " + e);
		}
		return afterCategoryInfo;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int removeDirectory(CategoryInfo categoryInfo, Integer passId) {
		int categoryId = categoryInfo.getCategoryId();
		try {
			categoryInfoMapper.deleteByPrimaryKey(categoryId);
		} catch (Exception e) {
			LogUtils.error(getClass(), "removeDirectory has error : " + e);
			callRollBack();
			return 0;
		}
		return 1;
	}

}
