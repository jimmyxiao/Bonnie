package com.bonniedraw.user.controller;

import java.util.List;

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
import com.bonniedraw.user.model.AdminInfo;
import com.bonniedraw.user.service.AdminService;

@Controller
@RequestMapping(value="/admin", produces="application/json")
public class AdminController extends BaseController {
	
	@Autowired
	AdminService adminService;
	
	@RequestMapping(value="/queryAdminList", produces="application/json")
	public @ResponseBody BaseModel queryAdminList(HttpServletRequest request,HttpServletResponse resp) {
		List<AdminInfo> adminList = adminService.queryAdminList();
		BaseModel baseModel = basicOutput(adminList);
		return baseModel;
	}
	
	@RequestMapping(value="/queryAdminInfo", produces="application/json")
	public @ResponseBody BaseModel queryAdminInfo(HttpServletRequest request,HttpServletResponse resp,@RequestBody Integer adminId) {
		AdminInfo adminInfo = adminService.queryAdminInfo(adminId);
		BaseModel baseModel = basicOutput(adminInfo);
		return baseModel;
	}
	
	@RequestMapping(value="/insertAdminInfo", produces="application/json")
	public @ResponseBody BaseModel insertAdminInfo(HttpSession httpSession,HttpServletRequest request,HttpServletResponse resp,@RequestBody AdminInfo adminInfo) {
		Integer passId = (Integer) httpSession.getAttribute("pass_id");
		int result = adminService.insertAdminInfo(adminInfo,passId);
		BaseModel baseModel = new BaseModel();
		if(result == 1){
			baseModel.setResult(true);
			baseModel.setMessage("新增成功");
		}else if(result == -1){
			baseModel.setResult(false);
			baseModel.setMessage("此帳號不能使用");
		}else{
			baseModel.setResult(false);
			baseModel.setMessage("新增失敗");
		}
		return baseModel;
	}
	
	@RequestMapping(value="/updateAdminInfo", produces="application/json")
	public @ResponseBody BaseModel updateAdminInfo(HttpSession httpSession,HttpServletRequest request,HttpServletResponse resp,@RequestBody AdminInfo adminInfo) {
		Integer passId = (Integer) httpSession.getAttribute("pass_id");
		int result = adminService.updateAdminInfo(adminInfo, passId);
		BaseModel baseModel = new BaseModel();
		if(result == 1){
			baseModel.setResult(true);
			baseModel.setMessage("更新成功");
		}else{
			baseModel.setResult(false);
			baseModel.setMessage("更新失敗");
		}
		return baseModel;
	}
	
	@RequestMapping(value="/removeAdminInfo", produces="application/json")
	public @ResponseBody BaseModel removeAdminInfo(HttpSession httpSession,HttpServletRequest request,HttpServletResponse resp,@RequestBody Integer adminInfoId) {
		int result = adminService.removeAdminInfo(adminInfoId);
		BaseModel baseModel = new BaseModel();
		if(result == 1){
			baseModel.setResult(true);
			baseModel.setMessage("刪除成功");
		}else{
			baseModel.setResult(false);
			baseModel.setMessage("刪除失敗");
		}
		return baseModel;
	}
	
}
