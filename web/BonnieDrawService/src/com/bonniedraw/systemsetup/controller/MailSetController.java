package com.bonniedraw.systemsetup.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bonniedraw.base.controller.BaseController;
import com.bonniedraw.base.model.BaseModel;
import com.bonniedraw.systemsetup.model.SystemSetup;
import com.bonniedraw.systemsetup.service.SystemSetupService;

@Controller
@RequestMapping(value="/systemSetup/mail", produces="application/json")
public class MailSetController extends BaseController {

	@Autowired
	SystemSetupService systemSetupService;
	
	@RequestMapping(value="/queryMailSetting")
	public @ResponseBody BaseModel queryMailSetting(HttpServletRequest request,HttpServletResponse resp) {
		SystemSetup systemSetup = systemSetupService.querySystemSetup();
		BaseModel baseModel = basicOutput(systemSetup);
		return baseModel;
	}
		
	@RequestMapping(value="/saveMailSetting")
	public @ResponseBody BaseModel saveMailSetting(HttpSession httpSession,HttpServletRequest request,HttpServletResponse resp,@RequestBody SystemSetup systemSetup) {
		int result;
		if(systemSetup.getSystemSetupId() == null){
			result = systemSetupService.insertSystemSetup(systemSetup);
		}else{
			result = systemSetupService.updateSystemSetup(systemSetup);
		}
		BaseModel baseModel = new BaseModel();
		if(result == 1){
			baseModel.setResult(true);
			baseModel.setMessage("儲存成功");
		}else{
			baseModel.setResult(false);
			baseModel.setMessage("儲存失敗");
		}
		return baseModel;
	}
	
}
