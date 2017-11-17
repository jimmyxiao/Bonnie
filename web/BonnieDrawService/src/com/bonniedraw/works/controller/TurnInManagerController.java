package com.bonniedraw.works.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bonniedraw.base.controller.BaseController;
import com.bonniedraw.base.model.BaseModel;
import com.bonniedraw.util.ValidateUtil;
import com.bonniedraw.web_api.module.WorksResponse;
import com.bonniedraw.works.model.TurnIn;
import com.bonniedraw.works.model.Works;
import com.bonniedraw.works.service.TurnInManagerService;

@Controller
@RequestMapping(value="turnInManager", produces="application/json")
public class TurnInManagerController extends BaseController {

	@Autowired
	TurnInManagerService turnInManagerService;
	
	@RequestMapping(value="/queryTurnInWorkList")
	public @ResponseBody BaseModel queryTurnInWorkList(HttpServletRequest request, HttpServletResponse response){
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(false);
		List<WorksResponse> turnInWorkList = turnInManagerService.queryTurnInWorkList();
		if(ValidateUtil.isNotEmptyAndSize(turnInWorkList)){
			baseModel.setResult(true);
			baseModel.setData(turnInWorkList);
		}
		return baseModel;
	}
	
	@RequestMapping(value="/queryTurnInList")
	public @ResponseBody BaseModel queryTurnInList(HttpServletRequest request, HttpServletResponse response, @RequestBody Works works){
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(false);
		if(works.getWorksId()!=null){
			List<TurnIn> turnInList = turnInManagerService.queryTurnInList(works.getWorksId());
			if(ValidateUtil.isNotEmptyAndSize(turnInList)){
				baseModel.setResult(true);
				baseModel.setData(turnInList);
			}
		}
		return baseModel;
	}
	
}
