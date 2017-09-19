package com.bonniedraw.works.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonniedraw.base.service.BaseService;
import com.bonniedraw.works.dao.NodeTreeMapper;
import com.bonniedraw.works.module.NodeTreeModule;
import com.bonniedraw.works.service.DirectoryManagerService;

@Service
public class DirectoryManagerServiceImpl extends BaseService implements
		DirectoryManagerService {

	@Autowired
	NodeTreeMapper nodeTreeMapper;
	
	@Override
	public List<NodeTreeModule> queryDirectoryList() {
		List<NodeTreeModule> directoryList = nodeTreeMapper.queryDirectoryList();
//		if(ValidateUtil.isNotEmptyAndSize(directoryList)){
//			List<Integer> splite= new ArrayList<Integer>();
//			int level = 0;
//			for(NodeTreeModule treeModule :directoryList){
//				if(treeModule.getCategoryLevel() != level){
//					splite.add(directoryList.indexOf(treeModule));
//					++level;
//				}
//			}
//		}		
		return directoryList;
	}

}
