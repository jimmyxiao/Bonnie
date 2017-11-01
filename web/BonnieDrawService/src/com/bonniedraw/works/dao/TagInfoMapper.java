package com.bonniedraw.works.dao;

import java.util.List;

import com.bonniedraw.works.model.TagInfo;

public interface TagInfoMapper {
    int deleteByPrimaryKey(Integer tagId);

    int insert(TagInfo record);

    int insertSelective(TagInfo record);

    TagInfo selectByPrimaryKey(Integer tagId);

    int updateByPrimaryKeySelective(TagInfo record);

    int updateByPrimaryKey(TagInfo record);
    
    List<TagInfo> getTagList(String tagName);
    
}