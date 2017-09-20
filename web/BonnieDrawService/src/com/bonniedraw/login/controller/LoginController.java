package com.bonniedraw.login.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import  com.bonniedraw.base.model.BaseModel;
import com.bonniedraw.login.model.RespLogin;
import com.bonniedraw.login.module.LoginInput;
import com.bonniedraw.login.module.LoginOutput;
import com.bonniedraw.login.service.LoginService;
import com.bonniedraw.user.model.AdminInfo;
import com.bonniedraw.user.model.UserInfo;
import com.bonniedraw.user.service.WebUserService;
import com.bonniedraw.util.TimerUtil;
import com.bonniedraw.util.ValidateUtil;
import  com.bonniedraw.util.auth.AuthView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="ws", produces="application/json")
public class LoginController {
	
	@Autowired
	WebUserService webUserService;
	
	@Autowired
	LoginService loginService;
	
	@RequestMapping(value="/complete")
	public @ResponseBody BaseModel registerComplete(HttpServletRequest request, HttpServletResponse resp, @RequestParam String token){
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(false);
		String msg ="";
		if(ValidateUtil.isNotBlank(token) && token.length()>10){
			int res = webUserService.registerComplete(token);
			if(res==1){
				baseModel.setResult(true);
			}else if(res==3){
				msg="連結已失效，請重新申請";
			}else {
				msg="無註冊會員";
			}
		}else{
			msg = "無效網址";
		}
		
		baseModel.setMessage(msg);
		return baseModel;
	}
	
	@RequestMapping(value="/register")
	public @ResponseBody BaseModel register(HttpServletRequest request, HttpServletResponse resp, @RequestBody UserInfo userInfo){
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(false);
		String msg ="";
		if(ValidateUtil.isNotBlank(userInfo.getPhoneNo()) && ValidateUtil.isNotBlank(userInfo.getUserName())
				&& ValidateUtil.isNotBlank(userInfo.getUserCode()) && ValidateUtil.isNotBlank(userInfo.getUserPw())){
			int res = webUserService.register(userInfo);
			switch (res) {
			case 1:
				baseModel.setResult(true);
				msg = "信件已發送";
				break;
			case 2:
				msg = "失敗";
				break;
			case 3:
				msg = "已註冊";
				break;
			}
		}else{
			
		}
		baseModel.setMessage(msg);
		return baseModel;
	}
	
	@AuthView
	@RequestMapping(value="/loginFailed")
	public @ResponseBody BaseModel loginFailed(HttpServletRequest request,HttpServletResponse resp) {
		RespLogin respLogin = new RespLogin();
		respLogin.setStatus(0);
		respLogin.setResult(false);
		return respLogin;
	}
	
	@AuthView
	@RequestMapping(value="/loginBackend", produces="application/json")
	public @ResponseBody BaseModel loginBackend(HttpSession session,HttpServletRequest request,HttpServletResponse resp,@RequestBody LoginInput loginInput) {		
		resp.setHeader("Access-Control-Allow-Origin", "*");
		RespLogin respLogin = new RespLogin();
		LoginOutput result = loginService.loginBackend(loginInput);
		
		if(result!=null && result.getStatus() == 1){
			String sessionId=(String)session.getId();
			result.setSessionId(sessionId);
			respLogin.setResult(true);
			respLogin.setData(result);
			
			AdminInfo adminInfo = result.getAdminInfo();
			String strtp = String.valueOf(TimerUtil.getNowDate().getTime());
			session.setAttribute("time_key", strtp);
			session.setAttribute("loginSuccess", "yes");
			session.setAttribute("pass_id", adminInfo.getAdminId());
			session.setAttribute("adminInfo", adminInfo);
			session.setAttribute("serviceKey", result.getSecurityKey());
		}else{
			respLogin.setResult(false);
			respLogin.setMessage("帳號密碼錯誤,請確認!");
		}
		return respLogin;
	}

}
