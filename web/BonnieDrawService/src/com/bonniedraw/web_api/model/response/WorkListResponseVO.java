package com.bonniedraw.web_api.model.response;

import java.util.List;

import com.bonniedraw.web_api.model.ApiResponseVO;
import com.bonniedraw.web_api.module.WorksResponse;

public class WorkListResponseVO extends ApiResponseVO {
	private List<WorksResponse> workList;
	private WorksResponse work;
	private int maxPagination;

	public List<WorksResponse> getWorkList() {
		return workList;
	}

	public void setWorkList(List<WorksResponse> workList) {
		this.workList = workList;
	}

	public WorksResponse getWork() {
		return work;
	}

	public void setWork(WorksResponse work) {
		this.work = work;
	}

	public int getMaxPagination() {
		return maxPagination;
	}

	public void setMaxPagination(int maxPagination) {
		this.maxPagination = maxPagination;
	}
	
}
