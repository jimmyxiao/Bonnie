package com.bonniedraw.works.service;

import java.util.List;
import java.util.Map;

import com.bonniedraw.web_api.model.request.LeaveMsgRequestVO;
import com.bonniedraw.web_api.model.request.SetFollowingRequestVO;
import com.bonniedraw.web_api.model.request.SetLikeRequestVO;
import com.bonniedraw.web_api.model.request.SetTurnInRequestVO;
import com.bonniedraw.web_api.model.request.WorkListRequestVO;
import com.bonniedraw.web_api.model.request.WorksSaveRequestVO;
import com.bonniedraw.web_api.module.WorksResponse;

public interface MobileWorksService {
	public Integer worksSave(WorksSaveRequestVO worksSaveRequestVO);
	public List<WorksResponse> queryAllWorks(WorkListRequestVO workListRequestVO);
	public Map<String, Object> queryAllWorksAndPagination(WorkListRequestVO workListRequestVO);
	public WorksResponse queryWorks(Integer wid);
	public int leavemsg(LeaveMsgRequestVO leaveMsgRequestVO);
	public int setLike(SetLikeRequestVO setLikeRequestVO);
	public int setFollowing(SetFollowingRequestVO setFollowingRequestVO);
	public int setTurnin(SetTurnInRequestVO setTurnInRequestVO);
}
