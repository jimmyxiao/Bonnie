package com.bonniedraw.works.dao;

import java.util.List;

import com.bonniedraw.works.model.CategoryInfo;
import com.bonniedraw.works.module.NodeTreeModule;

public interface NodeTreeMapper {
    int deleteByPrimaryKey(Integer categoryId);

    int insert(CategoryInfo record);

    int insertSelective(CategoryInfo record);

    NodeTreeModule selectByPrimaryKey(Integer categoryId);

    int updateByPrimaryKeySelective(CategoryInfo record);

    int updateByPrimaryKey(CategoryInfo record);
    
    List<NodeTreeModule> queryDirectoryList();
}