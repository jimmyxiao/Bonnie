package com.bonniedraw.works.dao;

import com.bonniedraw.works.model.WorksFolder;

public interface WorksFolderMapper {
    int deleteByPrimaryKey(Integer worksFolderId);

    int insert(WorksFolder record);

    int insertSelective(WorksFolder record);

    WorksFolder selectByPrimaryKey(Integer worksFolderId);

    int updateByPrimaryKeySelective(WorksFolder record);

    int updateByPrimaryKey(WorksFolder record);
}