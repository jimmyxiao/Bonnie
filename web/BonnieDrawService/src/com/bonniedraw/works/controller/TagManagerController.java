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
import com.bonniedraw.web_api.module.WorksResponse;
import com.bonniedraw.works.model.CategoryInfo;
import com.bonniedraw.works.model.TagInfo;
import com.bonniedraw.works.model.WorksTag;
import com.bonniedraw.works.module.TagViewModule;
import com.bonniedraw.works.service.TagManagerService;

@Controller
@RequestMapping(value="tagManager", produces="application/json")
public class TagManagerController extends BaseController{
	
	@Autowired
	TagManagerService tagManagerService;
	
	@RequestMapping(value="/queryTagList")
	public @ResponseBody BaseModel queryTagList(HttpServletRequest request, HttpServletResponse response, @RequestBody CategoryInfo categoryInfo){
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(false);
		List<TagInfo> tagList = tagManagerService.queryTagList();
		if(ValidateUtil.isNotEmptyAndSize(tagList)){
			baseModel.setResult(true);
			baseModel.setData(tagList);
		}
		return baseModel;
	}
	
	@RequestMapping(value="/queryTagViewList")
	public @ResponseBody BaseModel queryTagViewList(HttpServletRequest request, HttpServletResponse response, @RequestBody WorksTag worksTag){
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(false);
		List<TagViewModule> tagViewList = tagManagerService.queryTagViewList(); 
		if(ValidateUtil.isNotEmptyAndSize(tagViewList)){
			baseModel.setResult(true);
			baseModel.setData(tagViewList);
		}
		return baseModel;
	}
	
	@RequestMapping(value="/queryTagWorkList")
	public @ResponseBody BaseModel queryTagWorkList(HttpServletRequest request, HttpServletResponse response, @RequestBody TagViewModule tagViewModule){
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(false);
		List<WorksResponse> tagWorksList = tagManagerService.queryTagWorkList(tagViewModule.getWorksIdList()); 
		if(ValidateUtil.isNotEmptyAndSize(tagWorksList)){
			baseModel.setResult(true);
			baseModel.setData(tagWorksList);
		}
		return baseModel;
	}
	
	@RequestMapping(value="/createCustomTag")
	public @ResponseBody BaseModel createCustomTag(HttpSession httpSession, HttpServletRequest request, HttpServletResponse response, @RequestBody TagInfo tagInfo){
		Integer passId = (Integer) httpSession.getAttribute("pass_id");
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(false);
		if(ValidateUtil.isNotBlank(tagInfo.getTagName())){
			int result =  tagManagerService.createCustomTag(tagInfo, passId);
			if(result == 1){
				baseModel.setResult(true);
				baseModel.setData(result);
			}else if(result == 2){
				baseModel.setMessage("名稱重複");
			}
		}
		return baseModel;
	}
	
	@RequestMapping(value="/updateCustomTag")
	public @ResponseBody BaseModel updateCustomTag(HttpSession httpSession, HttpServletRequest request, HttpServletResponse response, @RequestBody TagInfo tagInfo){
		Integer passId = (Integer) httpSession.getAttribute("pass_id");
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(false);
		TagInfo result =  tagManagerService.updateCustomTag(tagInfo, passId);
		if(result !=null){
			baseModel.setResult(true);
			baseModel.setData(result);
		}
		return baseModel;
	}
	
	@RequestMapping(value="/removeCustomTag")
	public @ResponseBody BaseModel removeCustomTag(HttpSession httpSession, HttpServletRequest request, HttpServletResponse response, @RequestBody TagInfo tagInfo){
		Integer passId = (Integer) httpSession.getAttribute("pass_id");
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(false);
		if(tagInfo.getTagId() == null){
			baseModel.setMessage("異常刪除資料");
		}else{
			int result =  tagManagerService.removeCustomTag(tagInfo, passId);
			if(result == 1){
				baseModel.setResult(true);
			}
		}
		return baseModel;
	}
	
}
