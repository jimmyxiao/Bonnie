package com.bonniedraw.works.service;

import java.util.List;

import com.bonniedraw.web_api.module.WorksResponse;
import com.bonniedraw.works.model.Works;
import com.bonniedraw.works.module.SearchWorkModule;

public interface WebWorkService {
	public List<WorksResponse> queryWorkList(SearchWorkModule searchWorkModule);
	public WorksResponse queryWorkDetail(Works works);
	public WorksResponse changeStatus(WorksResponse works);
}
