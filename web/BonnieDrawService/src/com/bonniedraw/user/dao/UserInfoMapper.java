package com.bonniedraw.user.dao;

import java.util.List;

import com.bonniedraw.user.model.UserInfo;
import com.bonniedraw.web_api.model.request.LoginRequestVO;

public interface UserInfoMapper {
	int deleteByPrimaryKey(Integer userId);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);
    
    UserInfo inspectAppPwd(LoginRequestVO loginRequestVO);
    
    int inspectRegister(LoginRequestVO loginRequestVO);
    
    UserInfo inspectRegisterByUserInfo(UserInfo userInfo);
    
    UserInfo inspectOldPwd(UserInfo record);
    
    List<UserInfo> queryUserList();
    
    UserInfo queryTokenUser(String token);
    
}