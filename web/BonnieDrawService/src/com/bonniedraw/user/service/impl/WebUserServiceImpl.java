package com.bonniedraw.user.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonniedraw.base.service.BaseService;
import com.bonniedraw.user.dao.UserInfoMapper;
import com.bonniedraw.user.model.UserInfo;
import com.bonniedraw.user.service.WebUserService;
import com.bonniedraw.util.EncryptUtil;
import com.bonniedraw.util.LogUtils;
import com.bonniedraw.util.TimerUtil;

@Service
public class WebUserServiceImpl extends BaseService implements WebUserService {

	@Autowired
	UserInfoMapper userInfoMapper;
	
	@Override
	public int register(UserInfo userInfo) {
		int res=2;
		int count = userInfoMapper.inspectRegisterByUserInfo(userInfo);
		if(count>0){
			res = 3;
		}else{
			try {
				Date nowDate = TimerUtil.getNowDate();
				userInfo.setEmail(userInfo.getUserCode());
				userInfo.setUserPw(EncryptUtil.convertMD5(userInfo.getUserPw()));
				userInfo.setStatus(0);
				userInfo.setCreatedBy(0);
				userInfo.setCreationDate(nowDate);
				userInfo.setUpdatedBy(0);
				userInfo.setUpdateDate(nowDate);
				userInfo.setLanguageId(1);
				userInfoMapper.insertSelective(userInfo);
				res =1;
			} catch (Exception e) {
				LogUtils.error(getClass(), "register has error : " + e);
			}
		}
		return res;
	}
	
}
