package com.bonniedraw.works.dao;

import java.util.List;

import com.bonniedraw.works.model.CategoryInfo;

public interface CategoryInfoMapper {
    int deleteByPrimaryKey(Integer categoryId);

    int insert(CategoryInfo record);

    int insertSelective(CategoryInfo record);

    CategoryInfo selectByPrimaryKey(Integer categoryId);

    int updateByPrimaryKeySelective(CategoryInfo record);

    int updateByPrimaryKey(CategoryInfo record);
    
    String getBreadCrumbs(int categoryId);
    
    List<CategoryInfo> queryDirectoryList(Integer categoryParentId);
    
    List<CategoryInfo> selectByPrimaryKeyList(List<Integer> categoryIds);
}