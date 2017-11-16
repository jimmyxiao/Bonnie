package com.bonniedraw.user.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bonniedraw.base.service.BaseService;
import com.bonniedraw.email.EmailProcess;
import com.bonniedraw.systemsetup.service.SystemSetupService;
import com.bonniedraw.user.dao.UserInfoMapper;
import com.bonniedraw.user.model.UserCounter;
import com.bonniedraw.user.model.UserInfo;
import com.bonniedraw.user.service.WebUserService;
import com.bonniedraw.util.EncryptUtil;
import com.bonniedraw.util.LogUtils;
import com.bonniedraw.util.SercurityUtil;
import com.bonniedraw.util.TimerUtil;
import com.bonniedraw.web_api.model.response.UserInfoQueryResponseVO;

@Service
public class WebUserServiceImpl extends BaseService implements WebUserService {

	@Autowired
	UserInfoMapper userInfoMapper;
	
	@Autowired
	SystemSetupService systemSetupService;
	
	private void userInfoSetting(UserInfo userInfo, Date nowDate) throws Exception{
		Date regVaildDate = TimerUtil.addMin(nowDate, 30);
		String guid = SercurityUtil.getGUID();
		userInfo.setEmail(userInfo.getUserCode());
		userInfo.setUserPw(EncryptUtil.convertMD5(userInfo.getUserPw()));
		userInfo.setStatus(0);
		userInfo.setRegData(guid);
		userInfo.setRegValidDate(regVaildDate);
		userInfo.setCreatedBy(0);
		userInfo.setCreationDate(nowDate);
		userInfo.setUpdatedBy(0);
		userInfo.setUpdateDate(nowDate);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int register(UserInfo userInfo) {
		Date nowDate = TimerUtil.getNowDate();
		int res=2;
		try {
			UserInfo result = userInfoMapper.inspectRegisterByUserInfo(userInfo);
			if(result !=null){
				if(result.getStatus()==1){
					res = 3;
				}else if(result.getStatus() == 0){
					if(nowDate.compareTo(result.getRegValidDate())<=0 ){
						res = 3;
					} else {
						userInfoSetting(userInfo,nowDate);
						userInfo.setUserId(result.getUserId());
						userInfoMapper.updateByPrimaryKey(userInfo);
						if(EmailProcess.sendValideMail(systemSetupService,userInfo)){
							res =1;
						}else{
							callRollBack();
						}
					}
				}
			}else{
				userInfoSetting(userInfo, nowDate);
				userInfoMapper.insertSelective(userInfo);
				if(EmailProcess.sendValideMail(systemSetupService,userInfo)){
					res =1;
				}else{
					callRollBack();
				}
			}
		} catch (Exception e) {
			LogUtils.error(getClass(), "register has error : " + e);
			callRollBack();
		}
		return res;
	}
	
	@Override
	public int registerComplete(String token) {
		int res = 2;
		UserInfo userInfo = userInfoMapper.queryTokenUser(token);
		if(userInfo!=null){
			Date nowDate = TimerUtil.getNowDate();
			Date regValidDate = userInfo.getRegValidDate();
			if(nowDate.compareTo(regValidDate) > 0){
				res = 3;
			}else {
				userInfo.setStatus(1);
				userInfo.setRegData(null);
				userInfo.setRegValidDate(null);
				userInfo.setUpdateDate(nowDate);
				userInfo.setUpdatedBy(0);
				userInfoMapper.updateByPrimaryKey(userInfo);
				res=1;
			}
		}
		return res;
	}

	@Override
	public List<UserInfo> queryUserList(UserInfo searchInfo) {
		return userInfoMapper.queryUserList(searchInfo);
	}

	@Override
	public UserInfoQueryResponseVO queryUserDetail(UserInfo searchInfo) {
		UserInfoQueryResponseVO respResult = new UserInfoQueryResponseVO();
		UserInfo userInfo = userInfoMapper.selectByPrimaryKey(searchInfo.getUserId());
		if(userInfo!=null){
			respResult.setUserType(userInfo.getUserType());
			respResult.setUserCode(userInfo.getUserCode());
			respResult.setUserName(userInfo.getUserName());
			respResult.setNickName(userInfo.getNickName());
			respResult.setEmail(userInfo.getEmail());
			respResult.setDescription(userInfo.getDescription());
			respResult.setWebLink(userInfo.getWebLink());
			respResult.setPhoneCountryCode(userInfo.getPhoneCountryCode());
			respResult.setPhoneNo(userInfo.getPhoneNo());
			respResult.setGender(userInfo.getGender());
			respResult.setProfilePicture(userInfo.getProfilePicture());
			respResult.setBirthday(userInfo.getBirthday());
			respResult.setStatus(userInfo.getStatus());
			respResult.setLanguageCode(userInfo.getLanguageCode());
			respResult.setCountryCode(userInfo.getCountryCode());
			UserCounter userCounter = userInfoMapper.getUserCounter(searchInfo.getUserId());
			if(userCounter!=null){
				respResult.setWorksNum(userCounter.getWorksNum());
				respResult.setFansNum(userCounter.getFansNum());
				respResult.setFollowNum(userCounter.getFollowNum());
			}
		}
		return respResult;
	}

	@Override
	public UserInfo changeStatus(UserInfo userInfo) {
		if(userInfo.getStatus() == 1){
			userInfo.setStatus(2);
			userInfoMapper.updateStatusByPrimaryKey(userInfo);
		}else if(userInfo.getStatus() == 2){
			userInfo.setStatus(1);
			userInfoMapper.updateStatusByPrimaryKey(userInfo);
		}else{
			return null;
		}
		return userInfo;
	}
	
}
