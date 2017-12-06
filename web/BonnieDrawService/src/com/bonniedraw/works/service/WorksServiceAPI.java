package com.bonniedraw.works.service;

import java.util.List;
import java.util.Map;

import com.bonniedraw.web_api.model.request.LeaveMsgRequestVO;
import com.bonniedraw.web_api.model.request.SetCollectionRequestVO;
import com.bonniedraw.web_api.model.request.SetFollowingRequestVO;
import com.bonniedraw.web_api.model.request.SetLikeRequestVO;
import com.bonniedraw.web_api.model.request.SetTurnInRequestVO;
import com.bonniedraw.web_api.model.request.WorkListRequestVO;
import com.bonniedraw.web_api.model.request.WorksSaveRequestVO;
import com.bonniedraw.web_api.module.CategoryInfoResponse;
import com.bonniedraw.web_api.module.WorksResponse;
import com.bonniedraw.works.model.TagInfo;
import com.bonniedraw.works.model.Works;

public interface WorksServiceAPI {
	public Integer worksSave(WorksSaveRequestVO worksSaveRequestVO);

	public boolean updateWorksFilePath(int wid, int fType, String path);

	public List<WorksResponse> queryAllWorks(WorkListRequestVO workListRequestVO);

	public Map<String, Object> queryAllWorksAndPagination(WorkListRequestVO workListRequestVO);

	public Map<String, Object> queryAllWorksAndPaginationByCategory(WorkListRequestVO workListRequestVO);
	
	public WorksResponse queryWorks(Integer wid, int userId);

	public int leavemsg(LeaveMsgRequestVO leaveMsgRequestVO);

	public int setLike(SetLikeRequestVO setLikeRequestVO);

	public int setFollowing(SetFollowingRequestVO setFollowingRequestVO);

	public int setTurnin(SetTurnInRequestVO setTurnInRequestVO);

	public Map<String, Object> getDrawingPlay(int wid, int userId, int stn, int rc);

	public List<CategoryInfoResponse> getCategoryList(Integer categoryId);

	public int setCollection(SetCollectionRequestVO setCollectionRequestVO);

	public int deleteWork(int userId, int worksId);
	
	public List<TagInfo> getTagList(String tagName);
	
	public Map<String, Object> getKeywordAndWorks(int userId);
	
	public Works getWorksMeta(Integer id);
	
}
