package com.bonniedraw.web_api.model.response;

import java.util.Map;

import com.bonniedraw.web_api.model.ApiResponseVO;

public class HomeRightBarResponseVO extends ApiResponseVO {
	
	private Map<String, Object> dataMap;

	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
	}
	
}
