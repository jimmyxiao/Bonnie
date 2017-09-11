package com.bonniedraw.login.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import  com.bonniedraw.base.model.BaseModel;
import com.bonniedraw.login.model.RespLogin;
import com.bonniedraw.login.module.LoginInput;
import com.bonniedraw.login.module.LoginOutput;
import com.bonniedraw.user.model.UserInfo;
import com.bonniedraw.util.TimerUtil;
import  com.bonniedraw.util.auth.AuthView;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {
	
	@AuthView
	@RequestMapping(value="/loginFailed", produces="application/json")
	public @ResponseBody BaseModel loginFailed(HttpServletRequest request,HttpServletResponse resp) {
		RespLogin respLogin = new RespLogin();
		respLogin.setStatus(0);
		respLogin.setResult(false);
		return respLogin;
	}
	
	@AuthView
	@RequestMapping(value="/login", produces="application/json")
	public @ResponseBody BaseModel login(HttpSession session,HttpServletRequest request,HttpServletResponse resp,@RequestBody LoginInput loginInput) {
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if(ipAddress!=null){
			loginInput.setIp(ipAddress);
		}else{
			loginInput.setIp(request.getRemoteAddr());
		}
		
		resp.setHeader("Access-Control-Allow-Origin", "*");
		RespLogin respLogin = new RespLogin();
//		LoginOutput result = loginService.login(loginInput);
//		
//		if(result!=null && result.getStatus() == 1){
//			String sessionId=(String)session.getId();
//			result.setSessionId(sessionId);
//			respLogin.setResult(true);
//			respLogin.setData(result);
//			
//			UserInfo userInfo = result.getUserInfo();
//			String strtp = String.valueOf(TimerUtil.getNowDate().getTime());
//			session.setAttribute("time_key", strtp);
//			session.setAttribute("loginSuccess", "yes");
//			session.setAttribute("userInfo", userInfo);
//			session.setAttribute("userId", userInfo.getUserId());
//			session.setAttribute("serviceKey", result.getSecurityKey());
//		}else if(result !=null && result.getStatus() ==2){
//			respLogin.setStatus(2);
//			respLogin.setResult(false);
//			respLogin.setMessage("帳號停用 ! ");
//		}else{
//			respLogin.setStatus(2);
//			respLogin.setResult(false);
//			respLogin.setMessage("帳號密碼錯誤,請確認!");
//		}
		return respLogin;
	}
	
	@AuthView
	@RequestMapping(value="/loginBackend", produces="application/json")
	public @ResponseBody BaseModel loginBackend(HttpSession session,HttpServletRequest request,HttpServletResponse resp,@RequestBody LoginInput loginInput) {
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if(ipAddress!=null){
			loginInput.setIp(ipAddress);
		}else{
			loginInput.setIp(request.getRemoteAddr());
		}
		
		resp.setHeader("Access-Control-Allow-Origin", "*");
		RespLogin respLogin = new RespLogin();
		LoginOutput result = new LoginOutput();
		UserInfo userInfo = new UserInfo();
		userInfo.setUserId(1);
		result.setStatus(1);
		result.setUserInfo(userInfo);
		
//		if(result!=null && result.getStatus() == 1){
//			String sessionId=(String)session.getId();
//			result.setSessionId(sessionId);
			respLogin.setResult(true);
			respLogin.setData(result);
//			
//			AdminInfo adminInfo = result.getAdminInfo();
			String strtp = String.valueOf(TimerUtil.getNowDate().getTime());
			session.setAttribute("time_key", strtp);
			session.setAttribute("loginSuccess", "yes");
//			session.setAttribute("userInfo", adminInfo);
//			session.setAttribute("userId", adminInfo.getUserId());
//			session.setAttribute("serviceKey", result.getSecurityKey());
//		}else if(result !=null && result.getStatus() ==2){
//			respLogin.setStatus(2);
//			respLogin.setResult(false);
//			respLogin.setMessage("帳號停用 ! ");
//		}else{
//			respLogin.setStatus(2);
//			respLogin.setResult(false);
//			respLogin.setMessage("帳號密碼錯誤,請確認!");
//		}
		return respLogin;
	}

}
