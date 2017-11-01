package com.bonniedraw.web_api.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import com.bonniedraw.file.FileUtil;
import com.bonniedraw.systemsetup.model.SystemSetup;
import com.bonniedraw.systemsetup.service.DictionaryService;
import com.bonniedraw.systemsetup.service.SystemSetupService;
import com.bonniedraw.user.model.OtherUserModel;
import com.bonniedraw.user.model.UserCounter;
import com.bonniedraw.user.model.UserInfo;
import com.bonniedraw.user.service.UserServiceAPI;
import com.bonniedraw.util.EmailUtil;
import com.bonniedraw.util.LogUtils;
import com.bonniedraw.util.ServletUtil;
import com.bonniedraw.util.ValidateUtil;
import com.bonniedraw.web_api.model.ApiRequestVO;
import com.bonniedraw.web_api.model.request.CategoryListRequestVO;
import com.bonniedraw.web_api.model.request.DeleteWorkRequestVO;
import com.bonniedraw.web_api.model.request.DictionaryListRequestVO;
import com.bonniedraw.web_api.model.request.DrawingPlayRequestVO;
import com.bonniedraw.web_api.model.request.FileUploadRequestVO;
import com.bonniedraw.web_api.model.request.FollowingListRequestVO;
import com.bonniedraw.web_api.model.request.ForgetPwdRequestVO;
import com.bonniedraw.web_api.model.request.FriendRequestVO;
import com.bonniedraw.web_api.model.request.LeaveMsgRequestVO;
import com.bonniedraw.web_api.model.request.LoginRequestVO;
import com.bonniedraw.web_api.model.request.SetCollectionRequestVO;
import com.bonniedraw.web_api.model.request.SetFollowingRequestVO;
import com.bonniedraw.web_api.model.request.SetLikeRequestVO;
import com.bonniedraw.web_api.model.request.SetTurnInRequestVO;
import com.bonniedraw.web_api.model.request.TagListRequestVO;
import com.bonniedraw.web_api.model.request.UpdatePwdRequestVO;
import com.bonniedraw.web_api.model.request.UserInfoQueryRequestVO;
import com.bonniedraw.web_api.model.request.UserInfoUpdateRequestVO;
import com.bonniedraw.web_api.model.request.WorkListRequestVO;
import com.bonniedraw.web_api.model.request.WorksSaveRequestVO;
import com.bonniedraw.web_api.model.response.CategoryListResponseVO;
import com.bonniedraw.web_api.model.response.DeleteWorkResponseVO;
import com.bonniedraw.web_api.model.response.DictionaryListResponseVO;
import com.bonniedraw.web_api.model.response.DrawingPlayResponseVO;
import com.bonniedraw.web_api.model.response.FileUploadResponseVO;
import com.bonniedraw.web_api.model.response.FollowingListResponseVO;
import com.bonniedraw.web_api.model.response.ForgetPwdResponseVO;
import com.bonniedraw.web_api.model.response.FriendResponseVO;
import com.bonniedraw.web_api.model.response.LeaveMsgResponseVO;
import com.bonniedraw.web_api.model.response.LoginResponseVO;
import com.bonniedraw.web_api.model.response.SetCollectionResponseVO;
import com.bonniedraw.web_api.model.response.SetFollowingResponseVO;
import com.bonniedraw.web_api.model.response.SetLikeResponseVO;
import com.bonniedraw.web_api.model.response.SetTurnInResponseVO;
import com.bonniedraw.web_api.model.response.TagListResponseVO;
import com.bonniedraw.web_api.model.response.UpdatePwdResponseVO;
import com.bonniedraw.web_api.model.response.UserInfoQueryResponseVO;
import com.bonniedraw.web_api.model.response.UserInfoUpdateResponseVO;
import com.bonniedraw.web_api.model.response.WorkListResponseVO;
import com.bonniedraw.web_api.model.response.WorksSaveResponseVO;
import com.bonniedraw.web_api.module.CategoryInfoResponse;
import com.bonniedraw.web_api.module.UserInfoResponse;
import com.bonniedraw.web_api.module.WorksResponse;
import com.bonniedraw.works.model.TagInfo;
import com.bonniedraw.works.service.WorksServiceAPI;

@Controller
@RequestMapping(value="/BDService")
public class ApiController {
	
	@Autowired
	private ResourceBundleMessageSource messageSource;
	
	@Autowired
	private UserServiceAPI userServiceAPI;
	
	@Autowired
	private SystemSetupService systemSetupService;
	
	@Autowired
	private WorksServiceAPI worksServiceAPI;
	
	@Autowired
	private DictionaryService dictionaryService;
	
 	private boolean isLogin(ApiRequestVO apiRequestVO){
		if(ValidateUtil.isNotNumNone(apiRequestVO.getUi()) 
				&& ValidateUtil.isNotBlank(apiRequestVO.getLk())
				&& ValidateUtil.isNotEmpty(apiRequestVO.getDt()) ){
			return userServiceAPI.isLogin(apiRequestVO);
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
		Integer gender = loginRequestVO.getGender();
		if(ValidateUtil.isNotBlank(loginRequestVO.getUc()) 
				&& (fn>=1 && fn<=3)
				&& (ut>=1 && ut<=4) 
				&& (dt>=1 && dt<=3)
				&& (gender==null || (gender!=null && gender>=0 && gender<=2)) ){
				String ip = ServletUtil.getRequestIp(request);			
				LoginResponseVO result = userServiceAPI.login(loginRequestVO,ip);
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
							if(res==3){
								msg=result.getMsg();
							}else{
								msg = messageSource.getMessage("api_login_fail1",null,request.getLocale());
							}
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
			int res = userServiceAPI.updatePwd(updatePwdRequestVO);
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
	
	private void setUserResponse(UserInfoQueryResponseVO respResult, UserInfo userInfo, OtherUserModel otherUserModel){
		if(userInfo!=null && otherUserModel!=null){
			return;
		}
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
		}else if(otherUserModel !=null){
			respResult.setUserType(otherUserModel.getUserType());
			respResult.setUserCode(otherUserModel.getUserCode());
			respResult.setUserName(otherUserModel.getUserName());
			respResult.setNickName(otherUserModel.getNickName());
			respResult.setEmail(otherUserModel.getEmail());
			respResult.setDescription(otherUserModel.getDescription());
			respResult.setWebLink(otherUserModel.getWebLink());
			respResult.setPhoneCountryCode(otherUserModel.getPhoneCountryCode());
			respResult.setPhoneNo(otherUserModel.getPhoneNo());
			respResult.setGender(otherUserModel.getGender());
			respResult.setProfilePicture(otherUserModel.getProfilePicture());
			respResult.setBirthday(otherUserModel.getBirthday());
			respResult.setStatus(otherUserModel.getStatus());
			respResult.setLanguageCode(otherUserModel.getLanguageCode());
			respResult.setCountryCode(otherUserModel.getCountryCode());
			respResult.setFollow(otherUserModel.isFollow());
		}
	}
	
	@RequestMapping(value="/userInfoQuery" , produces="application/json")
	public @ResponseBody UserInfoQueryResponseVO userInfoQuery(HttpServletRequest request,HttpServletResponse resp,@RequestBody UserInfoQueryRequestVO userInfoQueryRequestVO) {
		UserInfoQueryResponseVO respResult = new UserInfoQueryResponseVO();
		respResult.setRes(2);
		String msg = "";
		if(isLogin(userInfoQueryRequestVO)){
			int type = userInfoQueryRequestVO.getType();
			UserInfo userInfo = null;
			OtherUserModel otherUserModel = null;
			UserCounter userCounter = null;
			if(type==0 || (type ==1 && userInfoQueryRequestVO.getQueryId()!=null)){
				int id = -1;
				if(type==0){
					id = userInfoQueryRequestVO.getUi();
					userInfo = userServiceAPI.queryUserInfo(id); 
					if(userInfo!=null){
						userCounter = userServiceAPI.getUserCounter(id);
						respResult.setRes(1);
						msg = messageSource.getMessage("api_success",null,request.getLocale());
						setUserResponse(respResult, userInfo, null);
					}else{
						msg = messageSource.getMessage("api_fail",null,request.getLocale());
					}
				}else{
					id = userInfoQueryRequestVO.getQueryId();
					otherUserModel = userServiceAPI.queryOtherUserInfo(id, userInfoQueryRequestVO.getUi());
					if(otherUserModel!=null){
						userCounter = userServiceAPI.getUserCounter(id);
						respResult.setRes(1);
						msg = messageSource.getMessage("api_success",null,request.getLocale());
						setUserResponse(respResult, null, otherUserModel);
					}else{
						msg = messageSource.getMessage("api_fail",null,request.getLocale());
					}
				}
				if(userCounter!=null){
					respResult.setWorksNum(userCounter.getWorksNum());
					respResult.setFansNum(userCounter.getFansNum());
					respResult.setFollowNum(userCounter.getFollowNum());
				}
			}else{
				msg="資料異常";
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
					&& ValidateUtil.isNotBlank(userInfoUpdateRequestVO.getUserName())
//					&& ValidateUtil.isNotBlank(userInfoUpdateRequestVO.getEmail())
					){
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
				userInfo.setLanguageCode(userInfoUpdateRequestVO.getLanguageCode());
				userInfo.setCountryCode(userInfoUpdateRequestVO.getCountryCode());
				int res = userServiceAPI.updateUserInfo(userInfo);
				respResult.setRes(res);
				if(res ==1){
					msg = messageSource.getMessage("api_success",null,request.getLocale());
				}else if(res == 3){
					msg = "綁定Email已更改，請重新登入";
				}else if(res == 4){
					msg = "Email重複，不可更改";
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
	
	@RequestMapping(value="/friendsList" , produces="application/json")
	public @ResponseBody FriendResponseVO getUserFriendsList(HttpServletRequest request,HttpServletResponse resp,@RequestBody FriendRequestVO friendRequestVO) {
		FriendResponseVO respResult = new FriendResponseVO();
		String msg = "";
		respResult.setRes(2);
		if(isLogin(friendRequestVO)){
			int thirdPlatform = friendRequestVO.getThirdPlatform();
			if(thirdPlatform>=2 && thirdPlatform<=4){
				List<Integer> uidList = friendRequestVO.getUidList();
				if(uidList !=null && !(uidList.contains(null) || uidList.contains("")) ){
					respResult = userServiceAPI.getUserFriendsList(friendRequestVO.getUi(), thirdPlatform, uidList);
					msg = "成功";
				}else{
					msg="接收朋友列表資料異常";
				}
			}else{
				msg = "無第三方登入平台";
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
			Integer worksId = worksSaveRequestVO.getWorksId();
			if( (ac>=1 && ac<=2) && (privacyType>=1 && privacyType<=3) && (ac==1 || (ac==2 && worksId !=null)) ){
				Integer wid = worksServiceAPI.worksSave(worksSaveRequestVO);
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
		try {
			if(isLogin(workListRequestVO)){
				Integer wt = workListRequestVO.getWt();
				Integer wid = workListRequestVO.getWid();
				if(ValidateUtil.isNotNumNone(wid)){		//取得單一作品
					WorksResponse worksResponse = worksServiceAPI.queryWorks(wid, workListRequestVO.getUi());
					if(worksResponse!=null){
						Integer resStatus = worksResponse.getStatus();
						Integer resUserId = worksResponse.getUserId();
						if(resStatus ==1 ||  (resStatus!=1 && resUserId == workListRequestVO.getUi())){
							Integer privacyType = worksResponse.getPrivacyType();
							if(privacyType == 1 || (privacyType!=1 && resUserId == workListRequestVO.getUi())){
								respResult.setWork(worksResponse);
								respResult.setRes(1);
								msg = messageSource.getMessage("api_success",null,request.getLocale());
							}
						}
//						List<WorksResponse> workList = respResult.getWorkList();
//						workList.add(worksResponse);
//						respResult.setRes(1);
//						msg = messageSource.getMessage("api_success",null,request.getLocale());
					}else{
						respResult.setRes(2);
						msg = messageSource.getMessage("api_fail",null,request.getLocale());
					}
				}else{
					if(ValidateUtil.isNotNumNone(wt) && wt > 0){
						if(wt!=3 && !(wt >=20)){
							List<WorksResponse> workList;
							if(ValidateUtil.isNotNumNone(workListRequestVO.getStn())){		//取得含有分頁控制的作品列表,反之只取得顯示部分作品列表
								Map<String, Object> resultMap = worksServiceAPI.queryAllWorksAndPagination(workListRequestVO);
								workList = (List<WorksResponse>) resultMap.get("worksResponseList");
								respResult.setMaxPagination((int) resultMap.get("maxPagination"));
							}else{
								workList  = worksServiceAPI.queryAllWorks(workListRequestVO);
							}
							respResult.setRes(1);
							respResult.setWorkList(workList);
							msg = messageSource.getMessage("api_success",null,request.getLocale());
						}else{
							if(ValidateUtil.isNotNumNone(wid)){		//取得系統非預設類別的作品
								WorksResponse worksResponse = worksServiceAPI.queryWorks(wid, workListRequestVO.getUi());
								if(worksResponse!=null){
									Integer resStatus = worksResponse.getStatus();
									Integer resUserId = worksResponse.getUserId();
									if(resStatus ==1 ||  (resStatus!=1 && resUserId == workListRequestVO.getUi())){
										Integer privacyType = worksResponse.getPrivacyType();
										if(privacyType == 1 || (privacyType!=1 && resUserId == workListRequestVO.getUi())){
											respResult.setWork(worksResponse);
											respResult.setRes(1);
											msg = messageSource.getMessage("api_success",null,request.getLocale());
										}
									}
								}
							}else{
								List<WorksResponse> workList=new ArrayList<WorksResponse>();
								if(ValidateUtil.isNotNumNone(workListRequestVO.getStn())){
									Map<String, Object> resultMap = worksServiceAPI.queryAllWorksAndPaginationByCategory(workListRequestVO);
									workList = (List<WorksResponse>) resultMap.get("worksResponseList");
									respResult.setMaxPagination((int) resultMap.get("maxPagination"));
								}
								respResult.setRes(1);
								respResult.setWorkList(workList);
								msg = messageSource.getMessage("api_success",null,request.getLocale());
							}
						}
					}else{
						msg="類別錯誤";
					}
				}
			}else{
				msg = "帳號未登入"; 
			}
			respResult.setMsg(msg);
		} catch (NoSuchMessageException e) {
			LogUtils.error(getClass(), "workslist has error : " + e);
		}
		return respResult;
	}

	@RequestMapping(value="/leavemsg" , produces="application/json")
	public @ResponseBody LeaveMsgResponseVO leavemsg(HttpServletRequest request,HttpServletResponse resp,@RequestBody LeaveMsgRequestVO leaveMsgRequestVO) {
		LeaveMsgResponseVO respResult = new LeaveMsgResponseVO();
		respResult.setRes(2);
		String msg = "";
		if(isLogin(leaveMsgRequestVO)){
			int fn = leaveMsgRequestVO.getFn();
			if(fn==1 || (fn==0 && ValidateUtil.isNotNumNone(leaveMsgRequestVO.getMsgId()))){
				int res = worksServiceAPI.leavemsg(leaveMsgRequestVO);
				if(res ==1){
					respResult.setRes(res);
					msg = messageSource.getMessage("api_success",null,request.getLocale());
				}else{
					msg = messageSource.getMessage("api_fail",null,request.getLocale());
				}
			}else{
				msg="資料異常";
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
			int fn = setLikeRequestVO.getFn();
			int likeType = setLikeRequestVO.getLikeType();
			if( (fn==1 || fn ==0) && (likeType>=1 && likeType<=4) ){
				int res = worksServiceAPI.setLike(setLikeRequestVO);
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
				int res = worksServiceAPI.setFollowing(setFollowingRequestVO);
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
				int res = worksServiceAPI.setTurnin(setTurnInRequestVO);
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
	
	@RequestMapping(value="/fileUpload", method = RequestMethod.POST)
	public @ResponseBody FileUploadResponseVO fileUpload(HttpServletRequest request, HttpServletResponse resp,  
			@RequestParam("file") CommonsMultipartFile file) {
		FileUploadResponseVO respResult = new FileUploadResponseVO();
		respResult.setRes(2);
		String msg = "";
		try {
			Integer ui = request.getParameter("ui") !=null? Integer.valueOf(request.getParameter("ui")):null;
			Integer dt = request.getParameter("dt") !=null? Integer.valueOf(request.getParameter("dt")):null;
			Integer wid = request.getParameter("wid") !=null? Integer.valueOf(request.getParameter("wid")):null;
			Integer ftype = request.getParameter("ftype") !=null? Integer.valueOf(request.getParameter("ftype")):null;
			String fn = request.getParameter("fn");
			FileUploadRequestVO fileUploadRequestVO = new FileUploadRequestVO();
			fileUploadRequestVO.setUi(ui);
			fileUploadRequestVO.setLk(request.getParameter("lk"));
			fileUploadRequestVO.setDt(dt);
			fileUploadRequestVO.setWid(wid);
			fileUploadRequestVO.setFtype(ftype);

			if(file !=null && !file.isEmpty()){
				if(isLogin(fileUploadRequestVO)){
					if("1".equals(fn)){
						if(ftype==null || wid==null){
							msg = messageSource.getMessage("api_fail",null,request.getLocale());
							respResult.setMsg(msg);
							return respResult;
						}
						int fType = fileUploadRequestVO.getFtype();
						if(fType>=1 && fType<=2 ){
							StringBuffer path = new StringBuffer();
							path.append(fileUploadRequestVO.getWid()).append((fType==1 ? "/png" : "/bdw"));
							Map<String, Object> result = FileUtil.uploadMultipartFile(file, path.toString());
							boolean status = (boolean)result.get("status");
							String filePath = result.get("path").toString();
							if( status && ValidateUtil.isNotBlank(filePath)){
								if(worksServiceAPI.updateWorksFilePath(fileUploadRequestVO.getWid(), fType, filePath)){
									respResult.setRes(1);
									msg = messageSource.getMessage("api_success",null,request.getLocale());
								}else{
									msg = messageSource.getMessage("api_fail",null,request.getLocale());
								}
							}else{
								msg = messageSource.getMessage("api_fail",null,request.getLocale());
							}
						}else{
							msg = messageSource.getMessage("api_data_error",null,request.getLocale());
						}
					}else if("2".equals(fn)){
						StringBuffer path = new StringBuffer();
						path.append("/picture/").append(fileUploadRequestVO.getUi());
						Map<String, Object> result = FileUtil.uploadMultipartFile(file, path.toString());
						boolean status = (boolean)result.get("status");
						String filePath = result.get("path").toString();
						if( status && ValidateUtil.isNotBlank(filePath)){
							if(userServiceAPI.updateUserPicture(fileUploadRequestVO.getUi(), filePath)){
								respResult.setRes(1);
								respResult.setProfilePicture(filePath);
								msg = messageSource.getMessage("api_success",null,request.getLocale());
							}else{
								msg = messageSource.getMessage("api_fail",null,request.getLocale());
							}
						}else{
							msg = messageSource.getMessage("api_fail",null,request.getLocale());
						}
					}
				}else{
					msg = "帳號未登入"; 
				}
			}else{
				msg = "無檔案"; 
			}
		} catch (NoSuchMessageException e) {
			LogUtils.fileConteollerError(e.toString());
			msg = "file service has error , check log file.";
		}
		respResult.setMsg(msg);
		return respResult;
	}
	
	@RequestMapping(value="/loadFile/**", method = RequestMethod.GET)
	public @ResponseBody HttpEntity<byte[]> loadFile(HttpServletRequest request,HttpServletResponse resp) {
		byte[] image = null;
		HttpHeaders headers = new HttpHeaders();
		String fullPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		if(ValidateUtil.isBlank(fullPath)) return null;
		String path = fullPath.replace("/BDService/loadFile", "");
		if(ValidateUtil.isBlank(path)) return null;
		
		String extension = (path.substring(path.lastIndexOf(".")+1)).replace("/", "");
		if("png".equals(extension)){
			headers.setContentType(MediaType.IMAGE_PNG);
		}else if("bdw".equals(extension)){
			headers.setContentType(new MediaType("application", "octet-stream"));
		}else if("jpg".equals(extension) || "jpeg".equals(extension)){
			headers.setContentType(MediaType.IMAGE_JPEG);
		}else{
			return null;
		}
		String rootPath = System.getProperty("catalina.home");
		String filePath = rootPath + path;
		File dir = new File(filePath);
		try{
			image = org.apache.commons.io.FileUtils.readFileToByteArray(dir);
			headers.setContentLength(image.length);
			return new HttpEntity<byte[]>(image, headers);
		}catch(IOException e){
			LogUtils.fileConteollerError(filePath + " loadFile has error : 輸出發生異常 =>" +e);
		}
		return null;
	}
	
	@RequestMapping(value="/drawingPlay" , produces="application/json")
	public @ResponseBody DrawingPlayResponseVO getDrawingPlay(HttpServletRequest request,HttpServletResponse resp, @RequestBody DrawingPlayRequestVO drawingPlayRequestVO) {
		DrawingPlayResponseVO respResult = new DrawingPlayResponseVO();
		String msg = "";
		if(isLogin(drawingPlayRequestVO)){
			respResult.setPointList(worksServiceAPI.getDrawingPlay(drawingPlayRequestVO.getWid(), drawingPlayRequestVO.getUi()));
			respResult.setRes(1);
		}else{
			msg = "帳號未登入"; 
		}
		respResult.setMsg(msg);
		return respResult;
	}
	
	@RequestMapping(value="/categoryList" , produces="application/json")
	public @ResponseBody CategoryListResponseVO getCategoryList(HttpServletRequest request,HttpServletResponse resp, @RequestBody CategoryListRequestVO categoryListRequestVO) {
		CategoryListResponseVO respResult = new CategoryListResponseVO();
		respResult.setRes(2);
		String msg = "";
		if(isLogin(categoryListRequestVO)){
			List<CategoryInfoResponse> categoryList =  worksServiceAPI.getCategoryList(categoryListRequestVO.getCategoryId());
			respResult.setCategoryList(categoryList);
			respResult.setRes(1);
		}else{
			msg = "帳號未登入"; 
		}
		respResult.setMsg(msg);
		return respResult;
	}
	
	@RequestMapping(value="/setCollection" , produces="application/json")
	public @ResponseBody SetCollectionResponseVO setCollection(HttpServletRequest request,HttpServletResponse resp, @RequestBody SetCollectionRequestVO setCollectionRequestVO) {
		SetCollectionResponseVO respResult = new SetCollectionResponseVO();
		respResult.setRes(2);
		String msg = "";
		if(isLogin(setCollectionRequestVO)){
			int fn = setCollectionRequestVO.getFn();
			if(fn==1 || fn==0){
				int res = worksServiceAPI.setCollection(setCollectionRequestVO);
				respResult.setRes(res);
			}else{
				msg = "資料異常";
			}
		}else{
			msg = "帳號未登入"; 
		}
		respResult.setMsg(msg);
		return respResult;
	}
	
	@RequestMapping(value="/followingList" , produces="application/json")
	public @ResponseBody FollowingListResponseVO getFollowingList(HttpServletRequest request,HttpServletResponse resp, @RequestBody FollowingListRequestVO followingListRequestVO) {
		FollowingListResponseVO respResult = new FollowingListResponseVO();
		respResult.setRes(2);
		String msg = "";
		if(isLogin(followingListRequestVO)){
			int fn = followingListRequestVO.getFn();
			if(fn==1 || fn==2){
				List<UserInfoResponse> userList = userServiceAPI.getFollowingList(fn, followingListRequestVO.getUi());
				respResult.setUserList(userList);
				respResult.setRes(1);
			}else{
				msg = "資料異常";
			}
		}else{
			msg = "帳號未登入"; 
		}
		respResult.setMsg(msg);
		return respResult;
	}
	
	@RequestMapping(value="/tagList" , produces="application/json")
	public @ResponseBody TagListResponseVO getTagList(HttpServletRequest request,HttpServletResponse resp, @RequestBody TagListRequestVO tagListRequestVO) {
		TagListResponseVO respResult = new TagListResponseVO();
		respResult.setRes(2);
		String msg = "";
		if(isLogin(tagListRequestVO)){
				List<TagInfo> tagList = worksServiceAPI.getTagList(null);
				respResult.setTagList(tagList);
				respResult.setRes(1);
		}else{
			msg = "帳號未登入"; 
		}
		respResult.setMsg(msg);
		return respResult;
	}
	
	@RequestMapping(value="/deleteWork" , produces="application/json")
	public @ResponseBody DeleteWorkResponseVO deleteWork(HttpServletRequest request,HttpServletResponse resp, @RequestBody DeleteWorkRequestVO deleteWorkRequestVO) {
		DeleteWorkResponseVO respResult = new DeleteWorkResponseVO();
		respResult.setRes(2);
		String msg = "";
		if(isLogin(deleteWorkRequestVO)){
			int res = worksServiceAPI.deleteWork(deleteWorkRequestVO.getUi(), deleteWorkRequestVO.getWorksId());
			if(res == 1){
				respResult.setRes(res);
				msg = "成功";
			}else if(res ==3){
				msg = "拒絕刪除";
			}else{
				msg = "異常";
			}
		}else{
			msg = "帳號未登入"; 
		}
		respResult.setMsg(msg);
		return respResult;
	}
	
	@RequestMapping(value="/dictionaryList" , produces="application/json")
	public @ResponseBody DictionaryListResponseVO getDictionaryList(HttpServletRequest request,HttpServletResponse resp, @RequestBody DictionaryListRequestVO dictionaryListRequestVO) {
		DictionaryListResponseVO respResult = new DictionaryListResponseVO();
		respResult.setRes(2);
		String msg = "";
//		if(isLogin(dictionaryListRequestVO)){
			int dictionaryType = dictionaryListRequestVO.getDictionaryType();
			if(dictionaryType==1 || dictionaryType==2){
				respResult.setDictionaryList(dictionaryService.getDictionaryList(dictionaryType, dictionaryListRequestVO.getDictionaryID()));
				respResult.setRes(1);
			}else{
				msg = "資料異常";
			}
//		}else{
//			msg = "帳號未登入"; 
//		}
		respResult.setMsg(msg);
		return respResult;
	}
	
	
}
