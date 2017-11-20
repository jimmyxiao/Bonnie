package com.bonniedraw.works.service;

import java.util.List;

import com.bonniedraw.web_api.module.WorksResponse;
import com.bonniedraw.works.model.TurnIn;

public interface TurnInManagerService {
	public List<WorksResponse> queryTurnInWorkList();
	public List<TurnIn> queryTurnInList(Integer worksId);
	public TurnIn changeStatus(TurnIn turnIn);
}
