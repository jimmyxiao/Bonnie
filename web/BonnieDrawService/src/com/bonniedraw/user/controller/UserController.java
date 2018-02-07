package com.bonniedraw.user.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bonniedraw.base.controller.BaseController;
import com.bonniedraw.base.model.BaseModel;
import com.bonniedraw.user.model.UserInfo;
import com.bonniedraw.user.service.WebUserService;
import com.bonniedraw.web_api.model.response.UserInfoQueryResponseVO;

import com.bonniedraw.login.dao.LoginMapper;
import com.bonniedraw.login.model.Login;

@Controller
@RequestMapping(value="user",produces="application/json")
public class UserController extends BaseController {
	
	@Autowired
	WebUserService webUserService;

	@Autowired
	LoginMapper loginMapper;
	
	@RequestMapping(value="/queryUserList", produces="application/json")
	public @ResponseBody BaseModel queryUserList(HttpServletRequest request, HttpServletResponse resp, @RequestBody UserInfo searchInfo) {
		List<UserInfo> userInfoList = webUserService.queryUserList(searchInfo);
		BaseModel baseModel = basicOutput(userInfoList);
		return baseModel;
	}
	
	@RequestMapping(value="/queryUserDetail", produces="application/json")
	public @ResponseBody BaseModel queryUserDetail(HttpServletRequest request, HttpServletResponse resp, @RequestBody UserInfo searchInfo) {
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(false);
		if(searchInfo.getUserId()!=null){
			UserInfoQueryResponseVO userInfo = webUserService.queryUserDetail(searchInfo);
			if(userInfo!=null){
				baseModel.setResult(true);
				baseModel.setData(userInfo);
			}
		}
		return baseModel;
	}
	
	@RequestMapping(value="/changeStatus", produces="application/json")
	public @ResponseBody BaseModel changeStatus(HttpServletRequest request, HttpServletResponse resp, @RequestBody UserInfo userInfo) {
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(false);
		if(userInfo.getUserId()!=null && userInfo.getStatus()!=null){
			UserInfo result = webUserService.changeStatus(userInfo);
			if(result !=null){
				baseModel.setResult(true);
				baseModel.setData(result);
			}
			Login loginVO = new Login();		
			loginVO.setUserId(userInfo.getUserId());
			loginMapper.updateCurrentIsFalseByUser(loginVO);
		}
		return baseModel;
	}

	@RequestMapping(value="/changeUserGroup", produces="application/json")
	public @ResponseBody BaseModel changeUserGroup(HttpServletRequest request, HttpServletResponse resp, @RequestBody UserInfo userInfo) {
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(false);
		if(userInfo.getUserId()!=null && userInfo.getUserGroup()!=null){
			UserInfo result = webUserService.changeUserGroup(userInfo);
			if(result !=null){
				baseModel.setResult(true);
				baseModel.setData(result);
			}
		}
		return baseModel;
	}
}
