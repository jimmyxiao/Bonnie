package com.bonniedraw.user.service.impl;


import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bonniedraw.base.service.BaseService;
import com.bonniedraw.email.EmailContent;
import com.bonniedraw.email.EmailProcess;
import com.bonniedraw.file.FileUtil;
import com.bonniedraw.login.dao.LoginMapper;
import com.bonniedraw.login.model.Login;
import com.bonniedraw.systemsetup.model.SystemSetup;
import com.bonniedraw.systemsetup.service.SystemSetupService;
import com.bonniedraw.user.dao.UserInfoMapper;
import com.bonniedraw.user.model.OtherUserModel;
import com.bonniedraw.user.model.UserCounter;
import com.bonniedraw.user.model.UserInfo;
import com.bonniedraw.user.service.UserServiceAPI;
import com.bonniedraw.util.EmailUtil;
import com.bonniedraw.util.EncryptUtil;
import com.bonniedraw.util.LogUtils;
import com.bonniedraw.util.SercurityUtil;
import com.bonniedraw.util.TimerUtil;
import com.bonniedraw.util.ValidateUtil;
import com.bonniedraw.web_api.model.ApiRequestVO;
import com.bonniedraw.web_api.model.request.FollowingListRequestVO;
import com.bonniedraw.web_api.model.request.LoginRequestVO;
import com.bonniedraw.web_api.model.request.UpdatePwdRequestVO;
import com.bonniedraw.web_api.model.response.ForgetPwdResponseVO;
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
	
	private void putLogin(LoginResponseVO result, LoginRequestVO loginRequestVO, UserInfo userInfo, String ipAddress) throws Exception{		
		Login loginVO = new Login();
		loginVO.setUserId(userInfo.getUserId());
		loginVO.setLoginToken(SercurityUtil.getUUID());
		loginVO.setServiceKey(SercurityUtil.getUUID());
		loginVO.setIsCurrent(1);
		loginVO.setLoginResult(1);
		loginVO.setSessionId(0);
		loginVO.setDeviceIp(ipAddress);
		loginVO.setToken(loginRequestVO.getToken());
		loginVO.setDeviceId(loginRequestVO.getDeviceId());
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
		loginMapper.updateCurrentIsFalse(loginVO);
		loginMapper.insertSelective(loginVO);
		result.setRes(1);
		result.setUi(userInfo.getUserId());
		result.setUt(loginRequestVO.getUt());
		result.setLk(loginVO.getLoginToken());
		result.setSk(loginVO.getServiceKey());
		result.setUserInfo(userInfo);
	}
	
	private void insertUserInfo(LoginResponseVO result, LoginRequestVO loginRequestVO, String ipAddress) throws Exception{
		Date nowDate = TimerUtil.getNowDate();
		UserInfo userInfo = getInitalUserInfo(loginRequestVO, nowDate);
		userInfoMapper.insert(userInfo);
		if(ValidateUtil.isNotBlank(loginRequestVO.getThirdPictureUrl())){
			StringBuffer path = new StringBuffer();
			path.append("/picture/").append(userInfo.getUserId());
			Map<String, Object> resultMap = FileUtil.copyURLToFile(loginRequestVO.getThirdPictureUrl(), path.toString());
			boolean status = (boolean)resultMap.get("status");
			if(status){
				String filePath = resultMap.get("path")!=null?resultMap.get("path").toString():null;
				userInfo.setProfilePicture(filePath);
				userInfoMapper.updateByPrimaryKeySelective(userInfo);
			}
		}
		putLogin(result, loginRequestVO, userInfo, ipAddress);
	}
	
	private LoginResponseVO callLogin(LoginRequestVO loginRequestVO, String ipAddress) {
		LoginResponseVO result = new LoginResponseVO();
		result.setRes(2);
		int dt = loginRequestVO.getDt();
		int ut = loginRequestVO.getUt();
		UserInfo existUserInfo = null;
		
		switch (ut) {
		case 1:		//使用郵件登入
			try {
				if(dt == 3){
					loginRequestVO.setUp((EncryptUtil.convertMD5(loginRequestVO.getUp())));
				}
				existUserInfo = userInfoMapper.inspectAppPwd(loginRequestVO);
				if(existUserInfo != null){
					Integer status = existUserInfo.getStatus();
					if(status != null && status!=0){
						putLogin(result, loginRequestVO, existUserInfo, ipAddress);
					}else{
						result.setRes(4);
					}
				}
			} catch (NoSuchAlgorithmException e) {
				result.setRes(3);
				result.setMsg("轉換失敗");
				LogUtils.error(getClass(), "login encrypt MD5 has error : " + e);
				return result;
			} catch (Exception e) {
				LogUtils.error(getClass(), "callLogin has error : " + e);
			}
			break;

		default:		//使用各項第三方平台登入, 如未註冊則自動註冊並登入
			try {
				existUserInfo = userInfoMapper.inspectAppPwd(loginRequestVO);
				if(existUserInfo != null && existUserInfo.getStatus() != null){
					putLogin(result, loginRequestVO, existUserInfo, ipAddress);
				}else{
					String thirdEmail = loginRequestVO.getThirdEmail();
					if(ValidateUtil.isNotBlank(thirdEmail)){
						UserInfo emailUserInfo = userInfoMapper.selectByUserCode(thirdEmail);
						if(emailUserInfo!=null){		// 第三方平台註冊,且已有email帳號,直接更新;反之做第三方平台新註冊
							Date nowDate = TimerUtil.getNowDate();
							String id = loginRequestVO.getUc();
							switch (ut) {
							case 2:
								emailUserInfo.setRegFacebookId(id);
								break;
							case 3:
								emailUserInfo.setRegGoogleId(id);
								break;
							case 4:
								emailUserInfo.setRegTwitterId(id);
								break;
							}
							emailUserInfo.setStatus(1);
							emailUserInfo.setUpdatedBy(0);
							emailUserInfo.setUpdateDate(nowDate);
							userInfoMapper.updateByPrimaryKeySelective(emailUserInfo);
							putLogin(result, loginRequestVO, emailUserInfo, ipAddress);
						}else{
							insertUserInfo(result, loginRequestVO, ipAddress);
						}
					}else{
						insertUserInfo(result, loginRequestVO, ipAddress);
					}
				}
			} catch (Exception e) {
				LogUtils.error(getClass(), "callLogin has error : " + e);
			}
			break;
		}
		
//		if(dt==3 && ut ==1){
//			try {
//				loginRequestVO.setUp((EncryptUtil.convertMD5(loginRequestVO.getUp())));
//			} catch (Exception e1) {
//				result.setRes(3);
//				result.setMsg("轉換失敗");
//				LogUtils.error(getClass(), "login encrypt MD5 has error : " + e1);
//				return result;
//			}
//		}
//		try {
//			UserInfo existUserInfo = userInfoMapper.inspectAppPwd(loginRequestVO);
//			if(existUserInfo != null){
//				Integer status = existUserInfo.getStatus();
//				if(status != null){
//					if(status == 0 && ut ==1){
//						result.setRes(4);
//					}else{
//						putLogin(result, loginRequestVO, existUserInfo, ipAddress);
//					}
//				}else{
//					result.setRes(2);
//				}
//			}else{
//				if(ut != 1){
//					String thirdEmail = loginRequestVO.getThirdEmail();
//					if(ValidateUtil.isNotBlank(thirdEmail)){
//						UserInfo emailUserInfo = userInfoMapper.selectByUserCode(thirdEmail);
//						if(emailUserInfo!=null){		// 第三方平台註冊,且已有email帳號,直接更新;反之做第三方平台新註冊
//							Date nowDate = TimerUtil.getNowDate();
//							String id = loginRequestVO.getUc();
//							switch (ut) {
//							case 2:
//								emailUserInfo.setRegFacebookId(id);
//								break;
//							case 3:
//								emailUserInfo.setRegGoogleId(id);
//								break;
//							case 4:
//								emailUserInfo.setRegTwitterId(id);
//								break;
//							}
//							emailUserInfo.setStatus(1);
//							emailUserInfo.setUpdatedBy(0);
//							emailUserInfo.setUpdateDate(nowDate);
//							userInfoMapper.updateByPrimaryKeySelective(emailUserInfo);
//							putLogin(result, loginRequestVO, emailUserInfo, ipAddress);
//						}else{
//							insertUserInfo(result, loginRequestVO, ipAddress);
//						}
//					}else{
//						insertUserInfo(result, loginRequestVO, ipAddress);
//					}
//				}else{
//					result.setRes(2);
//				}
//			}
//		} catch (Exception e) {
//			result.setRes(2);
//			LogUtils.error(getClass(), "callLogin has error : " + e);
//		}
		return result;
	}
	
	private UserInfo getInitalUserInfo(LoginRequestVO loginRequestVO, Date nowDate) throws Exception{	
		int userType = loginRequestVO.getUt();
		int dt = loginRequestVO.getDt();
		String userCode;
		if(userType!=1){
			String startStr = "";
			if(userType==2){
				startStr = "f";
			}else if(userType==3){
				startStr = "g";
			}else if(userType==4){
				startStr = "t";
			}
			userCode = startStr + loginRequestVO.getUc();
		}else{
			userCode = loginRequestVO.getUc();
		}
		UserInfo userInfo = new UserInfo();
		userInfo.setUserType(userType);
		userInfo.setStatus(0);
		userInfo.setCreatedBy(0);
		userInfo.setCreationDate(nowDate);
		userInfo.setUpdatedBy(0);
		userInfo.setUpdateDate(nowDate);
		userInfo.setLanguageCode(loginRequestVO.getLanguageCode());
		userInfo.setCountryCode(loginRequestVO.getCountryCode());
		userInfo.setGender(loginRequestVO.getGender());
		userInfo.setPhoneNo(loginRequestVO.getPhoneNo());
		userInfo.setUserCode(userCode);
		if(userType==1){
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
			userInfo.setUserName(loginRequestVO.getUn());
			userInfo.setUserPw("");
			userInfo.setEmail(loginRequestVO.getThirdEmail());
		}
		return userInfo;
	}
	
	@Transactional(rollbackFor = Exception.class)
	private LoginResponseVO callRegister(LoginRequestVO loginRequestVO){
		LoginResponseVO result = new LoginResponseVO();
		try{
			UserInfo existUserInfo = userInfoMapper.selectByUserCode(loginRequestVO.getUc());
//			if(userType != 1){
//				if(existUserInfo!=null){		// 第三方平台註冊,且已有email帳號,直接更新;反之做第三方平台新註冊
//					String id = loginRequestVO.getUc();
//					switch (userType) {
//					case 2:
//						existUserInfo.setRegFacebookId(id);
//						break;
//					case 3:
//						existUserInfo.setRegGoogleId(id);
//						break;
//					case 4:
//						existUserInfo.setRegTwitterId(id);
//						break;
//					}
//					existUserInfo.setUpdatedBy(0);
//					existUserInfo.setUpdateDate(nowDate);
//					userInfoMapper.updateByPrimaryKey(existUserInfo);
//				}else{
//					UserInfo userInfo = getInitalUserInfo(loginRequestVO, nowDate);
//					userInfoMapper.insert(userInfo);
//					if(ValidateUtil.isNotBlank(loginRequestVO.getThirdPictureUrl())){
//						StringBuffer path = new StringBuffer();
//						path.append("/picture/").append(userInfo.getUserId());
//						Map<String, Object> resultMap = FileUtil.copyURLToFile(loginRequestVO.getThirdPictureUrl(), path.toString());
//						boolean status = (boolean)resultMap.get("status");
//						if(status){
//							String filePath = resultMap.get("path")!=null?resultMap.get("path").toString():null;
//							userInfo.setProfilePicture(filePath);
//							userInfoMapper.updateByPrimaryKeySelective(userInfo);
//						}
//					}
//				}
//				result.setRes(1);
//			}else{
				Date nowDate = TimerUtil.getNowDate();
				Date regVaildDate = TimerUtil.addMin(nowDate, 30);
				String guid = SercurityUtil.getGUID();
				if(existUserInfo != null) {
					if(existUserInfo.getStatus() == 0) {		//當帳號為註冊中帳號, 可再更新和發送新的認證
						existUserInfo.setRegData(guid);
						existUserInfo.setRegValidDate(regVaildDate);
						existUserInfo.setUpdatedBy(0);
						existUserInfo.setUpdateDate(nowDate);
						userInfoMapper.updateByPrimaryKeySelective(existUserInfo);
						if(EmailProcess.sendValideMail(systemSetupService, existUserInfo)){
							result.setRes(1);
						}else{
							result.setRes(2);
							callRollBack();
						}
					}else{
						result.setRes(3);
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
					if(EmailProcess.sendValideMail(systemSetupService, userInfo)){
						result.setRes(1);
					}else{
						result.setRes(2);
						callRollBack();
					}
				}
//			}
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
//				userCode = loginRequestVO.getThirdEmail();
				result.setRes(2);
				return result;
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
	@Transactional(rollbackFor = Exception.class)
	public ForgetPwdResponseVO setPwdByEmail(String email, SystemSetup systemSetup) {
		ForgetPwdResponseVO result = new ForgetPwdResponseVO();
		result.setRes(4);
		SecureRandom random = new SecureRandom();
		String code = new BigInteger(130, random).toString(32);
		if(ValidateUtil.isNotBlank(code)){
			code = code.substring(0,8);
		}
		try {
			String md5Code = EncryptUtil.digest(code);
			UserInfo userInfo = userInfoMapper.selectByUserCode(email);
			if(userInfo!=null){
				userInfo.setUserPw(md5Code);
				userInfoMapper.updateByPrimaryKeySelective(userInfo);
				try {
					if(EmailUtil.send(systemSetup, email, EmailContent.EMAIL_SUBJECT , EmailContent.getEmailBody(code))){
						result.setRes(1);
						result.setMsg("已發送");
					}else{
						result.setMsg("發送郵件伺服設定異常");
					}
				}catch (Exception e) {
					LogUtils.error(this.getClass(), "send mail [ "+ email +" ] has error : " + e);
					result.setMsg("發送失敗") ;
					callRollBack();
				}
			}
		} catch (Exception e) {
			LogUtils.error(getClass(), "setPwdByEmail function encrypt md5Code has error : " + e );
			result.setRes(3);
			result.setMsg("重設密碼失敗");
			callRollBack();
		}
		return result;
	}

	@Override
	public UserCounter getUserCounter(int userId) {
		return userInfoMapper.getUserCounter(userId);
	}
	
	@Override
	public UserInfo queryUserInfo(int userId) {
		return userInfoMapper.selectByPrimaryKey(userId);
	}
	
	@Override
	public OtherUserModel queryOtherUserInfo(int queryId, int userId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("queryId", queryId);
		paramMap.put("userId", userId);
		return userInfoMapper.queryOtherUserInfo(paramMap);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateUserInfo(UserInfo userInfo) {
		int success = 2;
		Date nowDate = TimerUtil.getNowDate();
		
		try {
//			String userCode = userInfo.getUserCode();
//			String userEmail = userInfo.getEmail();
			userInfo.setUpdatedBy(userInfo.getUserId());
			userInfo.setUpdateDate(nowDate);
//			if(userCode.compareTo(userEmail) !=0){
//				UserInfo existiUserInfo = userInfoMapper.selectByUserCode(userEmail);
//				if(existiUserInfo!=null){
//					return 4;
//				}
//				userInfo.setUserCode(userEmail);
//				userInfoMapper.updateByPrimaryKeySelective(userInfo);
//				success = 3;
//			}else{
				userInfoMapper.updateByPrimaryKeySelective(userInfo);
				success = 1;
//			}
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
		try {
			userInfo.setUserId(updatePwdRequestVO.getUi());
			if(updatePwdRequestVO.getDt()==3){
				userInfo.setUserPw(EncryptUtil.convertMD5(updatePwdRequestVO.getOldPwd()));
			}else{
				userInfo.setUserPw(updatePwdRequestVO.getOldPwd());
			}
			UserInfo resultUserInfo = userInfoMapper.inspectOldPwd(userInfo);
			if(resultUserInfo == null ){
				success = 4;
			}else{
				if(resultUserInfo.getStatus() == 2){
					success = 3;
				}else{
					Date nowDate = TimerUtil.getNowDate();
					if(updatePwdRequestVO.getDt()==3){
						userInfo.setUserPw(EncryptUtil.convertMD5(updatePwdRequestVO.getNewPwd()));
					}else{
						userInfo.setUserPw(updatePwdRequestVO.getNewPwd());
					}
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
	@Transactional(rollbackFor = Exception.class)
	public boolean updateUserPicture(int userId, String path) {
		UserInfo updateUserInfo = new UserInfo();
		Date nowDate = TimerUtil.getNowDate();
		updateUserInfo.setUpdateDate(nowDate);
		updateUserInfo.setUpdatedBy(userId);
		updateUserInfo.setProfilePicture(path);
		updateUserInfo.setUserId(userId);
		try {
			userInfoMapper.updateByPrimaryKeySelective(updateUserInfo);
			return true;
		} catch (Exception e) {
			LogUtils.error(getClass(), "updateUserPicture has error :" + e);
		}
		return false;
	}
	
	@Override
	public FriendResponseVO getUserFriendsList(int userId, int thirdPlatform, List<String> uidList) {
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
			paramMap.put("list", followList);
			result =  userInfoMapper.queryUserByIds(paramMap);		
		}
		return result;
	}

	@Override
	public Map<String, Object> getFollowingListForWeb(FollowingListRequestVO followingListRequestVO) {
		int rc = followingListRequestVO.getRc();
		Integer stn = followingListRequestVO.getStn();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<UserInfoResponse> resultList = new ArrayList<UserInfoResponse>();
		int maxPagination = 0;
		
		Map<String, Object> pagerMap = new HashMap<String, Object>();
		pagerMap.put("offset", (rc*(stn-1)));
		pagerMap.put("limit", rc);
		pagerMap.put("userId", followingListRequestVO.getUi());
		pagerMap.put("fn", followingListRequestVO.getFn());
		maxPagination = followingMapper.seletTrackOrFansMaxPagination(pagerMap);
		List<Integer> followList = followingMapper.selectTrackOrFansPager(pagerMap);
		if(ValidateUtil.isNotEmptyAndSize(followList)){
			pagerMap.put("list", followList);
			resultList =  userInfoMapper.queryUserByIds(pagerMap);		
		}

		resultMap.put("userList", resultList);
		resultMap.put("maxPagination", maxPagination);
		return resultMap;
	}
	
}
