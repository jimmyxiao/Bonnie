package com.bonniedraw.user.dao;

import java.util.List;
import java.util.Map;

import com.bonniedraw.user.model.OtherUserModel;
import com.bonniedraw.user.model.UserCounter;
import com.bonniedraw.user.model.UserInfo;
import com.bonniedraw.web_api.model.request.LoginRequestVO;
import com.bonniedraw.web_api.module.UserInfoResponse;

public interface UserInfoMapper {
	int deleteByPrimaryKey(Integer userId);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);
    
    int updateStatusByPrimaryKey(UserInfo record);

    int updateUserGroupByPrimaryKey(UserInfo record);
    
    UserInfo selectByUserCode(String userCode);

    UserInfo selectUserGroupByPrimaryKey(UserInfo record);
    
    UserInfo inspectAppPwd(LoginRequestVO loginRequestVO);
    
    UserInfo inspectRegister(LoginRequestVO loginRequestVO);
    
    UserInfo inspectRegisterByUserInfo(UserInfo userInfo);
    
    UserInfo inspectOldPwd(UserInfo record);
    
    List<UserInfo> queryUserList(UserInfo searchInfo);
    
    OtherUserModel queryOtherUserInfo(Map<String, Object> paramMap);
    
    UserCounter getUserCounter(Integer userId);
    
    UserInfo queryTokenUser(String token);
    
    List<UserInfo> getUserFriendsList(Map<String, Object> paramMap);
    
    List<UserInfoResponse> queryUserByIds(Map<String, Object> paramMap);
    
}