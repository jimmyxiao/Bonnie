package com.bonniedraw.util.auth;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.bonniedraw.login.module.LoginUtils;

public class AuthInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
		if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
			HttpSession session = request.getSession();
			String loginFailedUrl = request.getContextPath() + "/loginFailed";

			Object userId = session.getAttribute("userId");
			if ( userId != null){
				String login_id = userId.toString();
				String time_key = (String) session.getAttribute("time_key");
				int isLastLogin = LoginUtils.checkIsLastLogin(Integer.valueOf(login_id), Long.parseLong(time_key));
				if (isLastLogin != 1){
					response.sendRedirect(loginFailedUrl);
					return false;
				}
			}else{
				response.sendRedirect(loginFailedUrl);
				return false;
			}

			String login_result = (String) session.getAttribute("loginSuccess");
			if(login_result != null && login_result.equals("yes")){
				return true;
			}else{
				response.sendRedirect(loginFailedUrl);
				return false;
			}
		}
		return false;
	}
	
}