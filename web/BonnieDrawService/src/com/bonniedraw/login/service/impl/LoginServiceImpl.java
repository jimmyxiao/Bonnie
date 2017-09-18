package com.bonniedraw.login.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonniedraw.base.service.BaseService;
import com.bonniedraw.login.module.LoginInput;
import com.bonniedraw.login.module.LoginOutput;
import com.bonniedraw.login.service.LoginService;
import com.bonniedraw.user.dao.AdminInfoMapper;
import com.bonniedraw.user.model.AdminInfo;
import com.bonniedraw.util.LogUtils;
import com.bonniedraw.util.TimerUtil;

@Service
public class LoginServiceImpl extends BaseService implements LoginService {

	@Autowired
	AdminInfoMapper adminInfoMapper;

	@Override
	public LoginOutput loginBackend(LoginInput loginInput) {
		LoginOutput loginOutput = null;
		try{		
			AdminInfo adminInfo = adminInfoMapper.inspectPwd(loginInput);
			if(adminInfo != null){
				loginOutput = new LoginOutput();
				Date currentDate = TimerUtil.getNowDate();
				SimpleDateFormat sdf = TimerUtil.getSimpleDateFormat("yyyyMMdd");		
				loginOutput.setStatus(1);
				loginOutput.setSecurityKey(sdf.format(currentDate));
				loginOutput.setAdminInfo(adminInfo);
			}
	    }catch(Exception e){
	    	LogUtils.error(this.getClass() , "loginBackend has error :" + e);
	    }	
		return loginOutput;
	}

}
