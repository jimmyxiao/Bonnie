package com.bonniedraw.works.service;

import java.util.List;

import com.bonniedraw.web_api.model.response.UserInfoQueryResponseVO;
import com.bonniedraw.works.model.Works;
import com.bonniedraw.works.module.SearchWorkModule;

public interface WebWorkService {
	public List<Works> queryWorkList(SearchWorkModule searchWorkModule);
	public UserInfoQueryResponseVO queryWorkDetail(Works works);
	public Works changeStatus(Works works);
}
