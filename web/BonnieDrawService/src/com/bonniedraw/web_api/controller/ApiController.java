package com.bonniedraw.web_api.controller;

import java.io.File;
import java.io.IOException;

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

import com.bonniedraw.util.LogUtils;
import com.bonniedraw.util.ValidateUtil;
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

@Controller
@RequestMapping(value="/BDService")
public class ApiController {
	
	@Autowired
	private ResourceBundleMessageSource messageSource;
	
	@RequestMapping(value="/login", produces="application/json")
	public @ResponseBody LoginResponseVO login(HttpServletRequest request,HttpServletResponse resp,@RequestBody LoginRequestVO loginRequestVO) {
		LoginResponseVO respResult = new LoginResponseVO();
		respResult.setRes(2);
		if(ValidateUtil.isNotBlank(loginRequestVO.getUc())){
			
		}else{
			respResult.setMsg(messageSource.getMessage("api_login_error",null,request.getLocale()));
		}
		return respResult;
	}
	
	@RequestMapping(value="/forgetpwd" , produces="application/json")
	public @ResponseBody ForgetPwdResponseVO forgetPwd(HttpServletRequest request,HttpServletResponse resp,@RequestBody ForgetPwdRequestVO forgetPwdRequestVO) {
		ForgetPwdResponseVO respResult = new ForgetPwdResponseVO();
		return respResult;
	}
	
	@RequestMapping(value="/updatePwd" , produces="application/json")
	public @ResponseBody UpdatePwdResponseVO updatePwd(HttpServletRequest request,HttpServletResponse resp,@RequestBody UpdatePwdRequestVO updatePwdRequestVO) {
		UpdatePwdResponseVO respResult = new UpdatePwdResponseVO();
		return respResult;
	}
	
	@RequestMapping(value="/worksSave" , produces="application/json")
	public @ResponseBody WorksSaveResponseVO worksSave(HttpServletRequest request,HttpServletResponse resp,@RequestBody WorksSaveRequestVO worksSaveRequestVO) {
		WorksSaveResponseVO respResult = new WorksSaveResponseVO();
		return respResult;
	}
	
	@RequestMapping(value="/worksList" , produces="application/json")
	public @ResponseBody WorkListResponseVO worksList(HttpServletRequest request,HttpServletResponse resp,@RequestBody WorkListRequestVO workListRequestVO) {
		WorkListResponseVO respResult = new WorkListResponseVO();
		return respResult;
	}
	
	@RequestMapping(value="/userInfoQuery" , produces="application/json")
	public @ResponseBody UserInfoQueryResponseVO userInfoQuery(HttpServletRequest request,HttpServletResponse resp,@RequestBody UserInfoQueryRequestVO userInfoQueryRequestVO) {
		UserInfoQueryResponseVO respResult = new UserInfoQueryResponseVO();
		return respResult;
	}
	
	@RequestMapping(value="/userInfoUpdate" , produces="application/json")
	public @ResponseBody UserInfoUpdateResponseVO userInfoUpdate(HttpServletRequest request,HttpServletResponse resp,@RequestBody UserInfoUpdateRequestVO userInfoUpdateRequestVO) {
		UserInfoUpdateResponseVO respResult = new UserInfoUpdateResponseVO();
		return respResult;
	}
	
	@RequestMapping(value="/leavemsg" , produces="application/json")
	public @ResponseBody LeaveMsgResponseVO leavemsg(HttpServletRequest request,HttpServletResponse resp,@RequestBody LeaveMsgRequestVO leaveMsgRequestVO) {
		LeaveMsgResponseVO respResult = new LeaveMsgResponseVO();
		return respResult;
	}
	
	@RequestMapping(value="/setLike" , produces="application/json")
	public @ResponseBody SetLikeResponseVO setLike(HttpServletRequest request,HttpServletResponse resp,@RequestBody SetLikeRequestVO setLikeRequestVO) {
		SetLikeResponseVO respResult = new SetLikeResponseVO();
		return respResult;
	}
	
	@RequestMapping(value="/setFollowing" , produces="application/json")
	public @ResponseBody SetFollowingResponseVO setFollowing(HttpServletRequest request,HttpServletResponse resp,@RequestBody SetFollowingRequestVO setFollowingRequestVO) {
		SetFollowingResponseVO respResult = new SetFollowingResponseVO();
		return respResult;
	}
	
	@RequestMapping(value="/setTurnin" , produces="application/json")
	public @ResponseBody SetTurnInResponseVO setTurnin(HttpServletRequest request,HttpServletResponse resp,@RequestBody SetTurnInRequestVO setTurnInRequestVO) {
		SetTurnInResponseVO respResult = new SetTurnInResponseVO();
		return respResult;
	}
	
	@RequestMapping(value="/fileUpload" , produces="application/json")
	public @ResponseBody FileUploadResponseVO fileUpload(HttpServletRequest request,HttpServletResponse resp, @RequestBody FileUploadRequestVO fileUploadRequestVO) {
		FileUploadResponseVO respResult = new FileUploadResponseVO();
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
