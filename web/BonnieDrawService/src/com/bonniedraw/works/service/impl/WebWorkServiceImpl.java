package com.bonniedraw.works.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bonniedraw.base.service.BaseService;
import com.bonniedraw.user.model.UserInfo;
import com.bonniedraw.web_api.model.response.UserInfoQueryResponseVO;
import com.bonniedraw.works.model.Works;
import com.bonniedraw.works.module.SearchWorkModule;
import com.bonniedraw.works.service.WebWorkService;

@Service
public class WebWorkServiceImpl extends BaseService implements WebWorkService {

	@Override
	public List<Works> queryWorkList(SearchWorkModule searchWorkModule) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserInfoQueryResponseVO queryWorkDetail(Works works) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Works changeStatus(Works works) {
		// TODO Auto-generated method stub
		return null;
	}

}
