package com.bonniedraw.user.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bonniedraw.base.controller.BaseController;
import com.bonniedraw.base.model.BaseModel;
import com.bonniedraw.user.model.UserInfo;
import com.bonniedraw.user.service.WebUserService;

@Controller
@RequestMapping(value="user",produces="application/json")
public class UserController extends BaseController {
	
	@Autowired
	WebUserService webUserService;
	
	@RequestMapping(value="/queryUserList", produces="application/json")
	public @ResponseBody BaseModel queryUserList(HttpServletRequest request,HttpServletResponse resp) {
		List<UserInfo> userInfoList = webUserService.queryUserList();
		BaseModel baseModel = basicOutput(userInfoList);
		return baseModel;
	}
}
