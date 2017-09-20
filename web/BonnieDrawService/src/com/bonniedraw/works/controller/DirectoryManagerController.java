package com.bonniedraw.works.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bonniedraw.base.controller.BaseController;
import com.bonniedraw.base.model.BaseModel;
import com.bonniedraw.util.ValidateUtil;
import com.bonniedraw.works.module.NodeTreeModule;
import com.bonniedraw.works.service.DirectoryManagerService;

@Controller
@RequestMapping(value="directoryManager", produces="application/json")
public class DirectoryManagerController extends BaseController {

	@Autowired
	DirectoryManagerService directoryManagerService;
	
	@RequestMapping(value="/queryDirectoryList")
	public @ResponseBody BaseModel queryDirectoryList(HttpServletRequest request, HttpServletResponse response){
		BaseModel baseModel = new BaseModel();
		baseModel.setResult(false);
		List<NodeTreeModule> tree = directoryManagerService.queryDirectoryList();
		if(ValidateUtil.isNotEmptyAndSize(tree)){
			baseModel.setResult(true);
			baseModel.setData(tree);
		}
		return baseModel;
	}
	
	
	
	
}
