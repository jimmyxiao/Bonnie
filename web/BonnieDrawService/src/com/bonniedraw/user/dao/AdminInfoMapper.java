package com.bonniedraw.user.dao;



import java.util.List;

import com.bonniedraw.login.module.LoginInput;
import com.bonniedraw.user.model.AdminInfo;

public interface AdminInfoMapper {
    int deleteByPrimaryKey(Integer adminId);

    int insert(AdminInfo record);

    int insertSelective(AdminInfo record);

    AdminInfo selectByPrimaryKey(Integer adminId);

    int updateByPrimaryKeySelective(AdminInfo record);

    int updateByPrimaryKey(AdminInfo record);
    
    List<AdminInfo> queryAdminList();
    
    AdminInfo selectByUserCode(String userCode);
    
    AdminInfo inspectPwd(LoginInput loginInput);
    
    
}