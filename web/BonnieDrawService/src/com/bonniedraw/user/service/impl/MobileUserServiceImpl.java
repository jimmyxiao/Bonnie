package com.bonniedraw.user.service.impl;


import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bonniedraw.base.service.BaseService;
import com.bonniedraw.login.dao.LoginMapper;
import com.bonniedraw.login.model.Login;
import com.bonniedraw.user.dao.UserInfoMapper;
import com.bonniedraw.user.model.UserInfo;
import com.bonniedraw.user.service.MobileUserService;
import com.bonniedraw.util.LogUtils;
import com.bonniedraw.util.SercurityUtil;
import com.bonniedraw.util.TimerUtil;
import com.bonniedraw.web_api.model.request.LoginRequestVO;
import com.bonniedraw.web_api.model.request.UpdatePwdRequestVO;
import com.bonniedraw.web_api.model.response.LoginResponseVO;

@Service
public class MobileUserServiceImpl extends BaseService implements MobileUserService {

	@Autowired
	UserInfoMapper userInfoMapper;
	
	@Autowired
	LoginMapper loginMapper;
	
	private LoginResponseVO callLogin(LoginRequestVO loginRequestVO){
		LoginResponseVO result = new LoginResponseVO();
		UserInfo userInfo = userInfoMapper.inspectAppPwd(loginRequestVO);
		if(userInfo != null){
			Login loginVO = new Login();
			loginVO.setUserId(userInfo.getUserId());
			loginVO.setLoginToken(SercurityUtil.getUUID());
			loginVO.setServiceKey(SercurityUtil.getUUID());
			loginVO.setIsCurrent(1);
			loginVO.setLoginResult(1);
			loginVO.setSessionId(0);
			switch (loginRequestVO.getDt()) {
			case 1:
				loginVO.setDeviceInfo("Android");
				break;
			case 2:
				loginVO.setDeviceInfo("iOS");
				break;
			case 3:
				loginVO.setDeviceInfo("Web");
				break;
			}
			loginMapper.insertSelective(loginVO);
			
			result.setRes(1);
			result.setUi(userInfo.getUserId());
			result.setUt(loginRequestVO.getUt());
			result.setLk(loginVO.getLoginToken());
			result.setSk(loginVO.getServiceKey());
		}else{
			result.setRes(2);
		}
		return result;
	}
	
	private LoginResponseVO callRegister(LoginRequestVO loginRequestVO){
		LoginResponseVO result = new LoginResponseVO();
		Date nowDate = TimerUtil.getNowDate();
		UserInfo userInfo = new UserInfo();
		String userCode = loginRequestVO.getUc();
		Integer userType = loginRequestVO.getUt();
		userInfo.setUserType(userType);
		userInfo.setCreatedBy(0);
		userInfo.setCreationDate(nowDate);
		userInfo.setUpdatedBy(0);
		userInfo.setUpdateDate(nowDate);
		userInfo.setStatus(0);
		if(userType==1){
			userInfo.setUserPw(loginRequestVO.getUp());
			userInfo.setUserName(loginRequestVO.getUn());
			userInfo.setEmail(userCode);
		}else{
			userInfo.setUserCode(userCode);
			userInfo.setUserName("");
			userInfo.setUserPw("");
			userInfo.setEmail(loginRequestVO.getFbemail());
		}
		try {
			userInfoMapper.insert(userInfo);
			result.setRes(1);
		} catch (Exception e) {
			result.setRes(2);
			LogUtils.error(getClass(), "callRegister has error : " + e);
		}
		return result;
	}
	
	@Override
	public LoginResponseVO login(LoginRequestVO loginRequestVO) {
		switch (loginRequestVO.getFn()) {
		case 1:
			return callLogin(loginRequestVO);
		case 2:
			return callRegister(loginRequestVO);
		case 3:
			LoginResponseVO result = new LoginResponseVO();
			try{
				int count = userInfoMapper.inspectRegister(loginRequestVO);
				if(count>0){
					result.setRes(2);
				}else{
					result.setRes(1);
				}
			}catch (Exception e) {
				LogUtils.error(getClass() , "inspectRegister has error :" + e);
				result.setRes(3);
			}
			return result;
		}
		return null;
	}

	
	@Override
	public UserInfo queryUserInfo(int userId) {
		return userInfoMapper.selectByPrimaryKey(userId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateUserInfo(UserInfo userInfo) {
		int success = 2;
		Date nowDate = TimerUtil.getNowDate();
		try {
			userInfo.setUpdateDate(nowDate);
			userInfoMapper.updateByPrimaryKeySelective(userInfo);
			success = 1;
		} catch (Exception e) {
			LogUtils.error(getClass(), "updateUserInfo has error : " + e );
			callRollBack();
		}
		return success;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updatePwd(UpdatePwdRequestVO updatePwdRequestVO) {
		int success = 2;
		UserInfo userInfo = new UserInfo();
		userInfo.setUserPw(String.valueOf(updatePwdRequestVO.getOldPwd()));
		try {
			UserInfo resultUserInfo = userInfoMapper.inspectOldPwd(userInfo);
			if(resultUserInfo == null ){
				success = 4;
			}else{
				if(resultUserInfo.getStatus() == 2){
					success = 3;
				}else{
					Date nowDate = TimerUtil.getNowDate();
					userInfo.setUserPw(String.valueOf(updatePwdRequestVO.getNewPwd()));
					userInfo.setUpdateDate(nowDate);
					userInfo.setUpdatedBy(updatePwdRequestVO.getUi());
					userInfoMapper.updateByPrimaryKeySelective(userInfo);
					success = 1;
				}
			}
		} catch (Exception e) {
			LogUtils.error(getClass(), "updatePwd has error : " + e);
			callRollBack();
		}
		return success;
	}
	
}
