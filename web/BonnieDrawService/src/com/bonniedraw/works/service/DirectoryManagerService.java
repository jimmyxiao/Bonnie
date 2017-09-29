package com.bonniedraw.works.service;

import java.util.List;

import com.bonniedraw.works.model.CategoryInfo;

public interface DirectoryManagerService {
//	List<NodeTreeModule> queryDirectoryList();	nodeTree uses
	List<CategoryInfo> queryDirectoryList(Integer categoryParentId);
	int createDirectory(CategoryInfo categoryInfo, Integer passId);
	CategoryInfo updateDirectory(CategoryInfo categoryInfo, Integer passId);
	int removeDirectory(CategoryInfo categoryInfo, Integer passId);
}
