package com.bonniedraw.works.dao;

import com.bonniedraw.works.model.UserFolder;

public interface UserFolderMapper {
    int deleteByPrimaryKey(Integer userFolderId);

    int insert(UserFolder record);

    int insertSelective(UserFolder record);

    UserFolder selectByPrimaryKey(Integer userFolderId);

    int updateByPrimaryKeySelective(UserFolder record);

    int updateByPrimaryKey(UserFolder record);
}