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
import com.bonniedraw.notification.model.NotiMsgInfo;
import com.bonniedraw.systemsetup.service.DictionaryService;
import com.bonniedraw.systemsetup.service.SystemSetupService;

@Controller
@RequestMapping(value="/systemSetup/notification", produces="application/json")
public class NotiMsgSetController extends BaseController {

	@Autowired
	SystemSetupService systemSetupService;
	
	@Autowired
	DictionaryService dictionaryService;
	
	@RequestMapping(value="/queryNotiMsgSetting")
	public @ResponseBody BaseModel queryNotiMsgSetting(HttpServletRequest request, HttpServletResponse resp,@RequestBody NotiMsgInfo notiMsgInfo) {
		String result = systemSetupService.queryNotiMsgSetting(notiMsgInfo);
		BaseModel baseModel = basicOutput(result);
		return baseModel;
	}
		
	@RequestMapping(value="/saveNotiMsgSetting")
	public @ResponseBody BaseModel saveNotiMsgSetting(HttpSession httpSession,HttpServletRequest request,HttpServletResponse resp,@RequestBody NotiMsgInfo notiMsgInfo) {
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(false);
		int result;
		Integer notiMsgType = notiMsgInfo.getNotiMsgType();
		String languageCode = notiMsgInfo.getLanguageCode();
		if(notiMsgType !=null && (notiMsgType>=1 && notiMsgType<=5) 
				&& languageCode!=null && (dictionaryService.isExistByLanguageCode(languageCode)) ){
				result = systemSetupService.saveNotiMsgSetting(notiMsgInfo);
			if(result == 1){
				baseModel.setResult(true);
				baseModel.setMessage("儲存成功");
			}else{
				baseModel.setMessage("儲存失敗");
			}
		}else{
			baseModel.setMessage("資料異常");
		}
		return baseModel;
	}
	
}
