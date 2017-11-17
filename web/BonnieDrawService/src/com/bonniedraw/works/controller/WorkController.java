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
import com.bonniedraw.web_api.module.WorksResponse;
import com.bonniedraw.works.model.Works;
import com.bonniedraw.works.module.SearchWorkModule;
import com.bonniedraw.works.service.WebWorkService;

@Controller
@RequestMapping(value="work",produces="application/json")
public class WorkController extends BaseController {
	
	@Autowired
	WebWorkService webWorkService;
	
	@RequestMapping(value="/queryWorkList", produces="application/json")
	public @ResponseBody BaseModel queryWorkList(HttpServletRequest request, HttpServletResponse resp, @RequestBody SearchWorkModule searchWorkModule) {
		List<WorksResponse> workInfoList = webWorkService.queryWorkList(searchWorkModule);
		BaseModel baseModel = basicOutput(workInfoList);
		return baseModel;
	}
	
	@RequestMapping(value="/queryWorkDetail", produces="application/json")
	public @ResponseBody BaseModel queryWorkDetail(HttpServletRequest request, HttpServletResponse resp, @RequestBody Works works) {
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(false);
		if(works.getWorksId()!=null){
			WorksResponse worksResponse = webWorkService.queryWorkDetail(works);
			if(worksResponse!=null){
				baseModel.setResult(true);
				baseModel.setData(worksResponse);
			}
		}
		return baseModel;
	}
	
	@RequestMapping(value="/changeStatus", produces="application/json")
	public @ResponseBody BaseModel changeStatus(HttpSession httpSession, HttpServletRequest request, HttpServletResponse resp, @RequestBody WorksResponse works) {
		Integer passId = (Integer) httpSession.getAttribute("pass_id");
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(false);
		if(works.getWorksId() !=null && works.getStatus()!=null){
			works.setUpdatedBy(passId);
			WorksResponse result = webWorkService.changeStatus(works);
			if(result !=null){
				baseModel.setResult(true);
				baseModel.setData(result);
			}
		}
		return baseModel;
	}
}
