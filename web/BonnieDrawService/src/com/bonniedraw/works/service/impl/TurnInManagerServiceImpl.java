package com.bonniedraw.works.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonniedraw.base.service.BaseService;
import com.bonniedraw.web_api.module.WorksResponse;
import com.bonniedraw.works.dao.TurnInMapper;
import com.bonniedraw.works.dao.WorksMapper;
import com.bonniedraw.works.model.TurnIn;
import com.bonniedraw.works.service.TurnInManagerService;

@Service
public class TurnInManagerServiceImpl extends BaseService implements TurnInManagerService {

	@Autowired
	WorksMapper worksMapper;
	
	@Autowired
	TurnInMapper turnInMapper;
	
	@Override
	public List<WorksResponse> queryTurnInWorkList() {
		return worksMapper.queryTurnInWorkList();
	}
	
	@Override
	public List<TurnIn> queryTurnInList(Integer worksId) {
		return turnInMapper.queryTurnInList(worksId);
	}

	@Override
	public TurnIn changeStatus(TurnIn turnIn) {
		if(turnIn.getStatus() ==1){
			turnIn.setStatus(2);
			turnInMapper.updateStatusByPrimaryKey(turnIn);
		}else if(turnIn.getStatus() ==2){
			turnIn.setStatus(1);
			turnInMapper.updateStatusByPrimaryKey(turnIn);
		}else{
			return null;
		}
		return turnIn;
	}

}
