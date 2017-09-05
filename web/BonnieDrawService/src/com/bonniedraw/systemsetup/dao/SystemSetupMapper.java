package com.bonniedraw.systemsetup.dao;

import com.bonniedraw.systemsetup.model.SystemSetup;

public interface SystemSetupMapper {
    int deleteByPrimaryKey(Integer systemSetupId);

    int insert(SystemSetup record);

    int insertSelective(SystemSetup record);

    SystemSetup selectByPrimaryKey(Integer systemSetupId);

    int updateByPrimaryKeySelective(SystemSetup record);

    int updateByPrimaryKey(SystemSetup record);
}