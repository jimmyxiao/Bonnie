package com.bonniedraw.works.controller;

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
import com.bonniedraw.util.ValidateUtil;
import com.bonniedraw.works.model.CategoryInfo;
import com.bonniedraw.works.service.DirectoryManagerService;

@Controller
@RequestMapping(value="directoryManager", produces="application/json")
public class DirectoryManagerController extends BaseController {

	@Autowired
	DirectoryManagerService directoryManagerService;
	
	@RequestMapping(value="/getBreadCrumbs")
	public @ResponseBody BaseModel getBreadCrumbs(HttpServletRequest request, HttpServletResponse response, @RequestBody int categoryId ){
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(false);
		List<CategoryInfo> result= directoryManagerService.getBreadCrumbs(categoryId);
		if(ValidateUtil.isNotEmptyAndSize(result)){
			baseModel.setResult(true);
			baseModel.setData(result);
		}
		return baseModel;
	}
	
	@RequestMapping(value="/queryDirectoryList")
	public @ResponseBody BaseModel queryDirectoryList(HttpServletRequest request, HttpServletResponse response, @RequestBody CategoryInfo categoryInfo){
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(false);
		List<CategoryInfo> tree = directoryManagerService.queryDirectoryList(categoryInfo.getCategoryParentId());
		if(ValidateUtil.isNotEmptyAndSize(tree)){
			baseModel.setResult(true);
			baseModel.setData(tree);
		}
		return baseModel;
	}
	
	@RequestMapping(value="/createDirectory")
	public @ResponseBody BaseModel createDirectory(HttpSession httpSession, HttpServletRequest request, HttpServletResponse response, @RequestBody CategoryInfo categoryInfo){
		Integer passId = (Integer) httpSession.getAttribute("pass_id");
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(false);
		if(ValidateUtil.isNotBlank(categoryInfo.getCategoryName())){
			int result = directoryManagerService.createDirectory(categoryInfo, passId);
			if(result == 1){
				baseModel.setResult(true);
				baseModel.setData(result);
			}else if(result == 2){
				baseModel.setMessage("上層資料夾不存在");
			}
		}
		return baseModel;
	}
	
	@RequestMapping(value="/updateDirectory")
	public @ResponseBody BaseModel updateDirectory(HttpSession httpSession, HttpServletRequest request, HttpServletResponse response, @RequestBody CategoryInfo categoryInfo){
		Integer passId = (Integer) httpSession.getAttribute("pass_id");
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(false);
		CategoryInfo result = directoryManagerService.updateDirectory(categoryInfo, passId);
		if(result !=null){
			baseModel.setResult(true);
			baseModel.setData(result);
		}
		return baseModel;
	}
	
	@RequestMapping(value="/removeDirectory")
	public @ResponseBody BaseModel removeDirectory(HttpSession httpSession, HttpServletRequest request, HttpServletResponse response, @RequestBody CategoryInfo categoryInfo){
		Integer passId = (Integer) httpSession.getAttribute("pass_id");
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(false);
		if(categoryInfo.getCategoryId() == null || categoryInfo.getCategoryId()<20){
			baseModel.setMessage("異常刪除資料");
		}else{
			int result = directoryManagerService.removeDirectory(categoryInfo, passId);
			if(result == 1){
				baseModel.setResult(true);
			}
		}
		return baseModel;
	}
	
}
