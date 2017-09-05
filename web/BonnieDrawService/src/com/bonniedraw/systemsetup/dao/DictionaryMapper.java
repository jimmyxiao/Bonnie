package com.bonniedraw.systemsetup.dao;

import com.bonniedraw.systemsetup.model.Dictionary;

public interface DictionaryMapper {
    int deleteByPrimaryKey(Integer dictionaryId);

    int insert(Dictionary record);

    int insertSelective(Dictionary record);

    Dictionary selectByPrimaryKey(Integer dictionaryId);

    int updateByPrimaryKeySelective(Dictionary record);

    int updateByPrimaryKey(Dictionary record);
}