package com.bonniedraw.user.service.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bonniedraw.base.service.BaseService;
import com.bonniedraw.email.EmailProcess;
import com.bonniedraw.login.dao.LoginMapper;
import com.bonniedraw.login.model.Login;
import com.bonniedraw.systemsetup.service.SystemSetupService;
import com.bonniedraw.user.dao.UserInfoMapper;
import com.bonniedraw.user.model.UserInfo;
import com.bonniedraw.user.service.UserServiceAPI;
import com.bonniedraw.util.EncryptUtil;
import com.bonniedraw.util.LogUtils;
import com.bonniedraw.util.SercurityUtil;
import com.bonniedraw.util.TimerUtil;
import com.bonniedraw.util.ValidateUtil;
import com.bonniedraw.web_api.model.ApiRequestVO;
import com.bonniedraw.web_api.model.request.LoginRequestVO;
import com.bonniedraw.web_api.model.request.UpdatePwdRequestVO;
import com.bonniedraw.web_api.model.response.FriendResponseVO;
import com.bonniedraw.web_api.model.response.LoginResponseVO;
import com.bonniedraw.web_api.module.UserInfoResponse;
import com.bonniedraw.works.dao.FollowingMapper;

@Service
public class UserServiceAPIImpl extends BaseService implements UserServiceAPI {

	@Autowired
	SystemSetupService systemSetupService;
	
	@Autowired
	UserInfoMapper userInfoMapper;
	
	@Autowired
	LoginMapper loginMapper;
	
	@Autowired
	FollowingMapper followingMapper;
	
	@Override
	public boolean isLogin(ApiRequestVO apiRequestVO){
		Login loginVO = new Login();
		loginVO.setUserId(apiRequestVO.getUi());
		loginVO.setLoginToken(apiRequestVO.getLk());
		switch (apiRequestVO.getDt()) {
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
		Login result = loginMapper.inspectLogin(loginVO);
		if(result!=null){
			return true;
		}
		return false;
	}
	
	private LoginResponseVO callLogin(LoginRequestVO loginRequestVO, String ipAddress) {
		LoginResponseVO result = new LoginResponseVO();
		int dt = loginRequestVO.getDt();
		if(dt==3){
			try {
				loginRequestVO.setUp((EncryptUtil.convertMD5(loginRequestVO.getUp())));
			} catch (Exception e1) {
				result.setRes(3);
				result.setMsg("轉換失敗");
				LogUtils.error(getClass(), "login encrypt MD5 has error : " + e1);
				return result;
			}
		}
		UserInfo userInfo = userInfoMapper.inspectAppPwd(loginRequestVO);
		if(userInfo != null){
			Login loginVO = new Login();
			loginVO.setUserId(userInfo.getUserId());
			loginVO.setLoginToken(SercurityUtil.getUUID());
			loginVO.setServiceKey(SercurityUtil.getUUID());
			loginVO.setIsCurrent(1);
			loginVO.setLoginResult(1);
			loginVO.setSessionId(0);
			loginVO.setDeviceIp(ipAddress);
			switch (dt) {
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
			try {
				loginMapper.updateCurrentIsFalse(loginVO);
				loginMapper.insertSelective(loginVO);
				result.setRes(1);
				result.setUi(userInfo.getUserId());
				result.setUt(loginRequestVO.getUt());
				result.setLk(loginVO.getLoginToken());
				result.setSk(loginVO.getServiceKey());
				result.setUserInfo(userInfo);
			} catch (Exception e) {
				result.setRes(2);
				LogUtils.error(getClass(), "callLogin has error : " + e);
			}
		}else{
			result.setRes(2);
		}
		return result;
	}
	
	private UserInfo getInitalUserInfo(LoginRequestVO loginRequestVO, Date nowDate) throws Exception{
		int userType = loginRequestVO.getUt();
		int dt = loginRequestVO.getDt();
		String userCode;
		if(userType!=1){	
			userCode = loginRequestVO.getThirdEmail();
		}else{
			userCode = loginRequestVO.getUc();
		}
		UserInfo userInfo = new UserInfo();
		userInfo.setUserType(userType);
		userInfo.setCreatedBy(0);
		userInfo.setCreationDate(nowDate);
		userInfo.setUpdatedBy(0);
		userInfo.setUpdateDate(nowDate);
		userInfo.setStatus(0);
		userInfo.setLanguageId(1);
		userInfo.setGender(loginRequestVO.getGender());
		userInfo.setPhoneNo(loginRequestVO.getPhoneNo());
		if(userType==1){
			userInfo.setUserCode(userCode);
			if(dt==3){
				userInfo.setUserPw(EncryptUtil.convertMD5(loginRequestVO.getUp()));
			}else{
				userInfo.setUserPw(loginRequestVO.getUp());
			}
			userInfo.setUserName(loginRequestVO.getUn());
			userInfo.setEmail(userCode);
		}else{
			String id = loginRequestVO.getUc();
			switch (userType) {
			case 2:
				userInfo.setRegFacebookId(id);
				break;
			case 3:
				userInfo.setRegGoogleId(id);
				break;
			case 4:
				userInfo.setRegTwitterId(id);
				break;
			}
			userInfo.setUserCode(loginRequestVO.getThirdEmail());
			userInfo.setUserName(loginRequestVO.getUn());
			userInfo.setUserPw("");
			userInfo.setEmail(loginRequestVO.getThirdEmail());
		}
		return userInfo;
	}
	
	@Transactional(rollbackFor = Exception.class)
	private LoginResponseVO callRegister(LoginRequestVO loginRequestVO){
		LoginResponseVO result = new LoginResponseVO();
		Date nowDate = TimerUtil.getNowDate();
		int userType = loginRequestVO.getUt();
		String userCode;
		if(userType!=1){	
			userCode = loginRequestVO.getThirdEmail();
		}else{
			userCode = loginRequestVO.getUc();
		}
		UserInfo existUserInfo = userInfoMapper.selectByUserCode(userCode);
		
		try{
			if(userType != 1){
				if(existUserInfo!=null){		// 第三方平台註冊,且已有email帳號,直接更新;反之做第三方平台新註冊
					String id = loginRequestVO.getUc();
					switch (userType) {
					case 2:
						existUserInfo.setRegFacebookId(id);
						break;
					case 3:
						existUserInfo.setRegGoogleId(id);
						break;
					case 4:
						existUserInfo.setRegTwitterId(id);
						break;
					}
					existUserInfo.setUpdatedBy(0);
					existUserInfo.setUpdateDate(nowDate);
					userInfoMapper.updateByPrimaryKey(existUserInfo);
				}else{
					UserInfo userInfo = getInitalUserInfo(loginRequestVO, nowDate);
					userInfoMapper.insert(userInfo);
				}
				result.setRes(1);
			}else{
				Date regVaildDate = TimerUtil.addMin(nowDate, 30);
				String guid = SercurityUtil.getGUID();
				if(existUserInfo!=null){		// email註冊,且含有第三方平台註冊帳號,更新額外資料,反之做email新註冊,都須發認證信	
					existUserInfo.setRegData(guid);
					existUserInfo.setRegValidDate(regVaildDate);
					existUserInfo.setUpdatedBy(0);
					existUserInfo.setUpdateDate(nowDate);
					userInfoMapper.updateByPrimaryKey(existUserInfo);
					if(EmailProcess.sendValideMail(systemSetupService,existUserInfo)){
						result.setRes(1);
					}else{
						result.setRes(2);
						callRollBack();
					}
				}else{
					UserInfo userInfo = getInitalUserInfo(loginRequestVO, nowDate);
					userInfo.setRegData(guid);
					userInfo.setRegValidDate(regVaildDate);
					userInfo.setCreatedBy(0);
					userInfo.setCreationDate(nowDate);
					userInfo.setUpdatedBy(0);
					userInfo.setUpdateDate(nowDate);
					userInfoMapper.insert(userInfo);
					if(EmailProcess.sendValideMail(systemSetupService,userInfo)){
						result.setRes(1);
					}else{
						result.setRes(2);
						callRollBack();
					}
				}
			}
		} catch (Exception e) {
			result.setRes(2);
			LogUtils.error(getClass(), "callRegister has error : " + e);
			callRollBack();
		}
		return result;
	}
	
	private LoginResponseVO callInspectRegister(LoginRequestVO loginRequestVO){
		LoginResponseVO result = new LoginResponseVO();
		try{
//			UserInfo userInfo = userInfoMapper.inspectRegister(loginRequestVO);
			int userType = loginRequestVO.getUt();
			String userCode;
			if(userType != 1){
				userCode = loginRequestVO.getThirdEmail();
			}else{
				userCode = loginRequestVO.getUc();
			}
			UserInfo userInfo = userInfoMapper.selectByUserCode(userCode);
			if(userInfo!=null){
				if( (userType==2 && userInfo.getRegFacebookId()==null) || 
						(userType==3 && userInfo.getRegGoogleId()==null) || 
						(userType==4 && userInfo.getRegTwitterId()==null) ){
					result.setRes(1);
				}else{
					result.setRes(2);
				}
			}else{
				result.setRes(1);
			}
		}catch (Exception e) {
			LogUtils.error(getClass() , "inspectRegister has error :" + e);
			result.setRes(3);
		}
		return result;
	}
	
	@Override
	public LoginResponseVO login(LoginRequestVO loginRequestVO, String ipAddress) {
		switch (loginRequestVO.getFn()) {
		case 1:
			return callLogin(loginRequestVO, ipAddress);
		case 2:
			return callRegister(loginRequestVO);
		case 3:
			return callInspectRegister(loginRequestVO);
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
			String userCode = userInfo.getUserCode();
			String userEmail = userInfo.getEmail();
			userInfo.setUpdatedBy(userInfo.getUserId());
			userInfo.setUpdateDate(nowDate);
			if(userCode.compareTo(userEmail) !=0){
				UserInfo existiUserInfo = userInfoMapper.selectByUserCode(userEmail);
				if(existiUserInfo!=null){
					return 4;
				}
				userInfo.setUserCode(userEmail);
				userInfoMapper.updateByPrimaryKeySelective(userInfo);
				success = 3;
			}else{
				userInfoMapper.updateByPrimaryKeySelective(userInfo);
				success = 1;
			}
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
	

	@Override
	public FriendResponseVO getUserFriendsList(int userId, int thirdPlatform, List<Integer> uidList) {
		FriendResponseVO friendResponseVO = new FriendResponseVO();
		friendResponseVO.setRes(2);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("thirdPlatform", thirdPlatform);
		paramMap.put("list", uidList);
		try {
			List<UserInfo> friendList = userInfoMapper.getUserFriendsList(paramMap);
			friendResponseVO.setFriendList(friendList);
			friendResponseVO.setRes(1);
		} catch (Exception e) {
			LogUtils.error(getClass(), "getUserFriendsList has error : " + e);
		}
		return friendResponseVO;
	}
	
	@Override
	public List<UserInfoResponse> getFollowingList(int fn, int userId) {
		List<UserInfoResponse> result = new ArrayList<UserInfoResponse>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("fn", fn);
		List<Integer> followList = followingMapper.selectTrackOrFans(paramMap);
		if(ValidateUtil.isNotEmptyAndSize(followList)){
			result =  userInfoMapper.queryUserByIds(followList);		
		}
		return result;
	}
	
}
