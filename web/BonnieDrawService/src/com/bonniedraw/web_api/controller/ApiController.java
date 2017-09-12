package com.bonniedraw.web_api.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bonniedraw.file.FileUtil;
import com.bonniedraw.systemsetup.model.SystemSetup;
import com.bonniedraw.systemsetup.service.SystemSetupService;
import com.bonniedraw.user.model.UserInfo;
import com.bonniedraw.user.service.MobileUserService;
import com.bonniedraw.util.EmailUtil;
import com.bonniedraw.util.LogUtils;
import com.bonniedraw.util.ValidateUtil;
import com.bonniedraw.web_api.model.ApiRequestVO;
import com.bonniedraw.web_api.model.request.FileUploadRequestVO;
import com.bonniedraw.web_api.model.request.ForgetPwdRequestVO;
import com.bonniedraw.web_api.model.request.LeaveMsgRequestVO;
import com.bonniedraw.web_api.model.request.LoadFileRequestVO;
import com.bonniedraw.web_api.model.request.LoginRequestVO;
import com.bonniedraw.web_api.model.request.SetFollowingRequestVO;
import com.bonniedraw.web_api.model.request.SetLikeRequestVO;
import com.bonniedraw.web_api.model.request.SetTurnInRequestVO;
import com.bonniedraw.web_api.model.request.UpdatePwdRequestVO;
import com.bonniedraw.web_api.model.request.UserInfoQueryRequestVO;
import com.bonniedraw.web_api.model.request.UserInfoUpdateRequestVO;
import com.bonniedraw.web_api.model.request.WorkListRequestVO;
import com.bonniedraw.web_api.model.request.WorksSaveRequestVO;
import com.bonniedraw.web_api.model.response.FileUploadResponseVO;
import com.bonniedraw.web_api.model.response.ForgetPwdResponseVO;
import com.bonniedraw.web_api.model.response.LeaveMsgResponseVO;
import com.bonniedraw.web_api.model.response.LoginResponseVO;
import com.bonniedraw.web_api.model.response.SetFollowingResponseVO;
import com.bonniedraw.web_api.model.response.SetLikeResponseVO;
import com.bonniedraw.web_api.model.response.SetTurnInResponseVO;
import com.bonniedraw.web_api.model.response.UpdatePwdResponseVO;
import com.bonniedraw.web_api.model.response.UserInfoQueryResponseVO;
import com.bonniedraw.web_api.model.response.UserInfoUpdateResponseVO;
import com.bonniedraw.web_api.model.response.WorkListResponseVO;
import com.bonniedraw.web_api.model.response.WorksSaveResponseVO;
import com.bonniedraw.web_api.module.WorksResponse;
import com.bonniedraw.works.service.MobileWorksService;

@Controller
@RequestMapping(value="/BDService")
public class ApiController {
	
	@Autowired
	private ResourceBundleMessageSource messageSource;
	
	@Autowired
	private MobileUserService mobileUserService;
	
	@Autowired
	private SystemSetupService systemSetupService;
	
	@Autowired
	private MobileWorksService mobileWorksService;
	
 	private boolean isLogin(ApiRequestVO apiRequestVO){
		if(ValidateUtil.isNotNumNone(apiRequestVO.getUi()) 
				&& ValidateUtil.isNotBlank(apiRequestVO.getLk())
				&& ValidateUtil.isNotEmpty(apiRequestVO.getDt()) ){
			return true;
		}else{
			return false;
		}
	}
	
	@RequestMapping(value="/login", produces="application/json")
	public @ResponseBody LoginResponseVO login(HttpServletRequest request,HttpServletResponse resp,@RequestBody LoginRequestVO loginRequestVO) {
		LoginResponseVO respResult = new LoginResponseVO();
		String msg = null;
		respResult.setRes(2);
		int fn = loginRequestVO.getFn();
		int ut = loginRequestVO.getUt();
		int dt = loginRequestVO.getDt();
		if(ValidateUtil.isNotBlank(loginRequestVO.getUc()) 
				&& (fn>=1 && fn<=3)
				&& (ut>=1 && ut<=4) 
				&& (dt>=1 && dt<=3) ){
			if(ut == 2 && ValidateUtil.isBlank(loginRequestVO.getFbemail())){
				msg = messageSource.getMessage("api_data_error",null,request.getLocale());
			}else{
				LoginResponseVO result = mobileUserService.login(loginRequestVO);
				if(result !=null){
					respResult = result;
					int res = result.getRes();
					if(res==1){
						switch (fn) {
						case 1:
							msg = messageSource.getMessage("api_login_success1",null,request.getLocale());
							break;
						case 2:
							msg = messageSource.getMessage("api_login_success2",null,request.getLocale());
							break;
						case 3:
							msg = messageSource.getMessage("api_login_success3",null,request.getLocale());
							break;
						}
					}else{
						switch (fn) {
						case 1:
							msg = messageSource.getMessage("api_login_fail1",null,request.getLocale());
							break;
						case 2:
							msg = messageSource.getMessage("api_login_fail2",null,request.getLocale());
							break;
						case 3:
							if (res == 2){
								msg = messageSource.getMessage("api_login_fail3_1",null,request.getLocale());
							}else{
								msg = messageSource.getMessage("api_login_fail3_2",null,request.getLocale());
							}
							break;
						}
					}
				}
			}
		}else{
			msg = messageSource.getMessage("api_data_error",null,request.getLocale());
		}
		respResult.setMsg(msg);
		return respResult;
	}
	
	@RequestMapping(value="/forgetpwd" , produces="application/json")
	public @ResponseBody ForgetPwdResponseVO forgetPwd(HttpServletRequest request,HttpServletResponse resp,@RequestBody ForgetPwdRequestVO forgetPwdRequestVO) {
		ForgetPwdResponseVO respResult = new ForgetPwdResponseVO();
		String msg = "";
		String email = forgetPwdRequestVO.getEmail();
//		String mask = forgetPwdRequestVO.getMask();
		respResult.setRes(0);
		try {
			if(ValidateUtil.isBlank(email)){
				msg = "無郵件資訊";
			}else{
				SystemSetup systemSetup = systemSetupService.querySystemSetup();
				if(EmailUtil.send(systemSetup, email, "", "")){
					msg = "已發送";
				}else{
					msg = "發送郵件伺服設定異常";
				}
			}
		}catch (Exception e) {
			LogUtils.error(this.getClass(), "send mail has error : " + e);
			msg =  "發送失敗" ;
		}
		respResult.setMsg(msg);
		return respResult;
	}
	
	@RequestMapping(value="/updatePwd" , produces="application/json")
	public @ResponseBody UpdatePwdResponseVO updatePwd(HttpServletRequest request,HttpServletResponse resp,@RequestBody UpdatePwdRequestVO updatePwdRequestVO) {
		UpdatePwdResponseVO respResult = new UpdatePwdResponseVO();
		respResult.setRes(2);
		String msg = "";
		if(isLogin(updatePwdRequestVO)){
			int res = mobileUserService.updatePwd(updatePwdRequestVO);
			respResult.setRes(res);
			switch (res) {
			case 1:
				msg = messageSource.getMessage("api_success",null,request.getLocale());
				break;
			case 2:
				msg = messageSource.getMessage("api_fail",null,request.getLocale());
				break;
			case 3:
				msg = "停用";
				break;
			case 4:
				msg = "舊密碼不符";
				break;
			}
		}else{
			msg = "帳號未登入"; 
		}
		respResult.setMsg(msg);
		return respResult;
	}
	
	@RequestMapping(value="/worksSave" , produces="application/json")
	public @ResponseBody WorksSaveResponseVO worksSave(HttpServletRequest request,HttpServletResponse resp,@RequestBody WorksSaveRequestVO worksSaveRequestVO) {
		WorksSaveResponseVO respResult = new WorksSaveResponseVO();
		String msg = "";
		if(isLogin(worksSaveRequestVO)){
			int ac = worksSaveRequestVO.getAc();
			int privacyType = worksSaveRequestVO.getPrivacyType();
			if( (ac>=1 && ac<=2) && (privacyType>=1 && privacyType<=3) ){
				Integer wid = mobileWorksService.worksSave(worksSaveRequestVO);
				if(ValidateUtil.isNotEmpty(wid)){
					respResult.setRes(1);
					respResult.setWid(wid);
					msg = messageSource.getMessage("api_success",null,request.getLocale());
				}else{
					respResult.setRes(2);
					msg = messageSource.getMessage("api_fail",null,request.getLocale());
				}
			}else{
				msg = messageSource.getMessage("api_data_error",null,request.getLocale());
			}
		}else{
			msg = "帳號未登入"; 
		}
		respResult.setMsg(msg);
		return respResult;
	}
	
	@RequestMapping(value="/worksList" , produces="application/json")
	public @ResponseBody WorkListResponseVO worksList(HttpServletRequest request,HttpServletResponse resp,@RequestBody WorkListRequestVO workListRequestVO) {
		WorkListResponseVO respResult = new WorkListResponseVO();
		String msg = "";
		if(isLogin(workListRequestVO)){
			Integer wid = workListRequestVO.getWid();
			if(ValidateUtil.isNotNumNone(wid)){
				WorksResponse worksResponse = mobileWorksService.queryWorks(wid);
				if(worksResponse!=null){
					List<WorksResponse> workList = respResult.getWorkList();
					workList.add(worksResponse);
					respResult.setRes(1);
					msg = messageSource.getMessage("api_success",null,request.getLocale());
				}else{
					respResult.setRes(2);
					msg = messageSource.getMessage("api_fail",null,request.getLocale());
				}
			}else{
				List<WorksResponse> workList  = mobileWorksService.queryAllWorks();
				if(ValidateUtil.isNotEmptyAndSize(workList)){
					respResult.setRes(1);
					respResult.setWorkList(workList);
					msg = messageSource.getMessage("api_success",null,request.getLocale());
				}else{
					respResult.setRes(2);
					msg = messageSource.getMessage("api_fail",null,request.getLocale());
				}
			}
		}else{
			msg = "帳號未登入"; 
		}
		respResult.setMsg(msg);
		return respResult;
	}
	
	@RequestMapping(value="/userInfoQuery" , produces="application/json")
	public @ResponseBody UserInfoQueryResponseVO userInfoQuery(HttpServletRequest request,HttpServletResponse resp,@RequestBody UserInfoQueryRequestVO userInfoQueryRequestVO) {
		UserInfoQueryResponseVO respResult = new UserInfoQueryResponseVO();
		String msg = "";
		if(isLogin(userInfoQueryRequestVO)){
			UserInfo userInfo = mobileUserService.queryUserInfo(userInfoQueryRequestVO.getUi());
			if(userInfo!=null){
				respResult.setRes(1);
				msg = messageSource.getMessage("api_success",null,request.getLocale());
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
				respResult.setLanguageId(userInfo.getLanguageId());
			}else{
				respResult.setRes(2);
				msg = messageSource.getMessage("api_fail",null,request.getLocale());
			}
		}else{
			msg = "帳號未登入"; 
		}
		respResult.setMsg(msg);
		return respResult;
	}
	
	@RequestMapping(value="/userInfoUpdate" , produces="application/json")
	public @ResponseBody UserInfoUpdateResponseVO userInfoUpdate(HttpServletRequest request,HttpServletResponse resp,@RequestBody UserInfoUpdateRequestVO userInfoUpdateRequestVO) {
		UserInfoUpdateResponseVO respResult = new UserInfoUpdateResponseVO();
		String msg = "";
		if(isLogin(userInfoUpdateRequestVO)){
			if(ValidateUtil.isNotBlank(userInfoUpdateRequestVO.getUserCode())
					&& ValidateUtil.isNotBlank(userInfoUpdateRequestVO.getUserName())){
				UserInfo userInfo = new UserInfo();
				userInfo.setUserId(userInfoUpdateRequestVO.getUi());
				userInfo.setUserType(userInfoUpdateRequestVO.getUserType());
				userInfo.setUserCode(userInfoUpdateRequestVO.getUserCode());
				userInfo.setUserName(userInfoUpdateRequestVO.getUserName());
				userInfo.setNickName(userInfoUpdateRequestVO.getNickName());
				userInfo.setEmail(userInfoUpdateRequestVO.getEmail());
				userInfo.setDescription(userInfoUpdateRequestVO.getDescription());
				userInfo.setWebLink(userInfoUpdateRequestVO.getWebLink());
				userInfo.setPhoneCountryCode(userInfoUpdateRequestVO.getPhoneCountryCode());
				userInfo.setPhoneNo(userInfoUpdateRequestVO.getPhoneNo());
				userInfo.setGender(userInfoUpdateRequestVO.getGender());
				userInfo.setProfilePicture(userInfoUpdateRequestVO.getProfilePicture());
				userInfo.setBirthday(userInfoUpdateRequestVO.getBirthday());
				userInfo.setStatus(userInfoUpdateRequestVO.getStatus());
				userInfo.setLanguageId(userInfoUpdateRequestVO.getLanguageId());
				int res = mobileUserService.updateUserInfo(userInfo);
				respResult.setRes(res);
				if(res ==1){
					msg = messageSource.getMessage("api_success",null,request.getLocale());
				}else{
					msg = messageSource.getMessage("api_fail",null,request.getLocale());
				}
			}else{
				respResult.setRes(2);
				msg = messageSource.getMessage("api_data_error",null,request.getLocale());
			}
		}else{
			msg = "帳號未登入"; 
		}
		respResult.setMsg(msg);
		return respResult;
	}
	
	@RequestMapping(value="/leavemsg" , produces="application/json")
	public @ResponseBody LeaveMsgResponseVO leavemsg(HttpServletRequest request,HttpServletResponse resp,@RequestBody LeaveMsgRequestVO leaveMsgRequestVO) {
		LeaveMsgResponseVO respResult = new LeaveMsgResponseVO();
		respResult.setRes(2);
		String msg = "";
		if(isLogin(leaveMsgRequestVO)){
			int res = mobileWorksService.leavemsg(leaveMsgRequestVO);
			if(res ==1){
				respResult.setRes(res);
				msg = messageSource.getMessage("api_success",null,request.getLocale());
			}else{
				msg = messageSource.getMessage("api_fail",null,request.getLocale());
			}
		}else{
			msg = "帳號未登入"; 
		}
		respResult.setMsg(msg);
		return respResult;
	}
	
	@RequestMapping(value="/setLike" , produces="application/json")
	public @ResponseBody SetLikeResponseVO setLike(HttpServletRequest request,HttpServletResponse resp,@RequestBody SetLikeRequestVO setLikeRequestVO) {
		SetLikeResponseVO respResult = new SetLikeResponseVO();
		respResult.setRes(2);
		String msg = "";
		if(isLogin(setLikeRequestVO)){
			int likeType = setLikeRequestVO.getLikeType();
			if(likeType>=1 && likeType<=4){
				int res = mobileWorksService.setLike(setLikeRequestVO);
				if(res==1){
					respResult.setRes(1);
					msg = messageSource.getMessage("api_success",null,request.getLocale());
				}else{
					msg = messageSource.getMessage("api_fail",null,request.getLocale());
				}
			}else{
				msg = messageSource.getMessage("api_data_error",null,request.getLocale());
			}
		}else{
			msg = "帳號未登入"; 
		}
		respResult.setMsg(msg);
		return respResult;
	}
	
	@RequestMapping(value="/setFollowing" , produces="application/json")
	public @ResponseBody SetFollowingResponseVO setFollowing(HttpServletRequest request,HttpServletResponse resp,@RequestBody SetFollowingRequestVO setFollowingRequestVO) {
		SetFollowingResponseVO respResult = new SetFollowingResponseVO();
		respResult.setRes(2);
		String msg = "";
		if(isLogin(setFollowingRequestVO)){
			int fn = setFollowingRequestVO.getFn();
			if(fn>=0 && fn<=1){
				int res = mobileWorksService.setFollowing(setFollowingRequestVO);
				if(res==1){
					respResult.setRes(res);
					msg = messageSource.getMessage("api_success",null,request.getLocale());
				}else{
					msg = messageSource.getMessage("api_fail",null,request.getLocale());
				}
			}else{
				msg = messageSource.getMessage("api_data_error",null,request.getLocale());
			}
		}else{
			msg = "帳號未登入"; 
		}
		respResult.setMsg(msg);
		return respResult;
	}
	
	@RequestMapping(value="/setTurnin" , produces="application/json")
	public @ResponseBody SetTurnInResponseVO setTurnin(HttpServletRequest request,HttpServletResponse resp,@RequestBody SetTurnInRequestVO setTurnInRequestVO) {
		SetTurnInResponseVO respResult = new SetTurnInResponseVO();
		respResult.setRes(2);
		String msg = "";
		if(ValidateUtil.isNotNumNone(setTurnInRequestVO.getUi()) 
				&& ValidateUtil.isNotBlank(setTurnInRequestVO.getLk()) ){
			if(ValidateUtil.isNotBlank(setTurnInRequestVO.getDescription())){
				int res = mobileWorksService.setTurnin(setTurnInRequestVO);
				if(res == 1){
					respResult.setRes(1);
					msg = messageSource.getMessage("api_success",null,request.getLocale());
				}else{
					msg = messageSource.getMessage("api_fail",null,request.getLocale());
				}
			}else{
				msg = messageSource.getMessage("api_data_error",null,request.getLocale());
			}
		}else{
			msg = "帳號未登入"; 
		}
		respResult.setMsg(msg);
		return respResult;
	}
	
	@RequestMapping(value="/fileUpload" , produces="application/json")
	public @ResponseBody FileUploadResponseVO fileUpload(HttpServletRequest request,HttpServletResponse resp, @RequestBody FileUploadRequestVO fileUploadRequestVO) {
		FileUploadResponseVO respResult = new FileUploadResponseVO();
		String msg = "";
		if(isLogin(fileUploadRequestVO)){
			int fType = fileUploadRequestVO.getFtype();
			if(fType>=1 && fType<=2 ){
				StringBuffer path = new StringBuffer();
				path.append(fileUploadRequestVO.getWid()).append((fType==1 ? "png" : "bdw"));
				if(FileUtil.uploadFile(fileUploadRequestVO.getFile(), path.toString())){
					respResult.setRes(1);
					msg = messageSource.getMessage("api_success",null,request.getLocale());
				}else{
					respResult.setRes(2);
					msg = messageSource.getMessage("api_fail",null,request.getLocale());
				}
			}else{
				msg = messageSource.getMessage("api_data_error",null,request.getLocale());
			}
		}else{
			msg = "帳號未登入"; 
		}
		respResult.setMsg(msg);
		return respResult;
	}
	
	@RequestMapping(value="/loadFile")
	public @ResponseBody HttpEntity<byte[]> loadFile(HttpServletRequest request,HttpServletResponse resp,@RequestBody LoadFileRequestVO loadFileRequestVO) {
		byte[] image = null;
		HttpHeaders headers = new HttpHeaders();
		String rootPath = System.getProperty("catalina.home");
		String filePath = rootPath + "" ;
		File dir = new File(filePath);
		try{
			image = org.apache.commons.io.FileUtils.readFileToByteArray(dir);
			headers.setContentType(MediaType.IMAGE_PNG); 
			headers.setContentLength(image.length);
		}catch(IOException e){
			LogUtils.fileConteollerError(filePath + " loadFile has error : 輸出發生異常 =>" +e);
		}
		return new HttpEntity<byte[]>(image, headers);
	}
	
}