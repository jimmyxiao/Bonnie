package com.bonniedraw.works.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonniedraw.base.service.BaseService;
import com.bonniedraw.util.TimerUtil;
import com.bonniedraw.web_api.module.WorksResponse;
import com.bonniedraw.works.dao.WorksMapper;
import com.bonniedraw.works.model.Works;
import com.bonniedraw.works.module.SearchWorkModule;
import com.bonniedraw.works.service.WebWorkService;

@Service
public class WebWorkServiceImpl extends BaseService implements WebWorkService {

	@Autowired
	WorksMapper worksMapper;
	
	@Override
	public List<WorksResponse> queryWorkList(SearchWorkModule searchWorkModule) {
		return worksMapper.queryWorkListBySearchWorkModule(searchWorkModule);
	}

	@Override
	public WorksResponse queryWorkDetail(Works works) {
		WorksResponse result = worksMapper.queryWorkDetail(works);
		if(result !=null){
			result.setLikeCount(result.getLikeList().size());
			result.setMsgCount(result.getMsgList().size());
			return result;
		}
		return null;
	}

	@Override
	public WorksResponse changeStatus(WorksResponse works) {
		works.setUpdateDate(TimerUtil.getNowDate());
		if(works.getStatus()==1){
			works.setStatus(2);
			worksMapper.updateStatusByPrimaryKey(works);
		}else if(works.getStatus() ==2){
			works.setStatus(1);
			worksMapper.updateStatusByPrimaryKey(works);
		}else{
			return null;
		}
		return works;
	}

}
