package com.bonniedraw.web_api.model.response;

import java.util.List;

import com.bonniedraw.web_api.model.ApiResponseVO;
import com.bonniedraw.web_api.module.WorksResponse;

public class WorkListResponseVO extends ApiResponseVO {
	private List<WorksResponse> workList;

	public List<WorksResponse> getWorkList() {
		return workList;
	}

	public void setWorkList(List<WorksResponse> workList) {
		this.workList = workList;
	}

}
