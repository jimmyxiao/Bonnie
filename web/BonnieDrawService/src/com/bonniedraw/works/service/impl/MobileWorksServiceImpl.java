package com.bonniedraw.works.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bonniedraw.base.service.BaseService;
import com.bonniedraw.util.LogUtils;
import com.bonniedraw.util.TimerUtil;
import com.bonniedraw.util.ValidateUtil;
import com.bonniedraw.web_api.model.request.LeaveMsgRequestVO;
import com.bonniedraw.web_api.model.request.SetFollowingRequestVO;
import com.bonniedraw.web_api.model.request.SetLikeRequestVO;
import com.bonniedraw.web_api.model.request.SetTurnInRequestVO;
import com.bonniedraw.web_api.model.request.WorkListRequestVO;
import com.bonniedraw.web_api.model.request.WorksSaveRequestVO;
import com.bonniedraw.web_api.module.WorksResponse;
import com.bonniedraw.works.dao.FollowingMapper;
import com.bonniedraw.works.dao.TurnInMapper;
import com.bonniedraw.works.dao.WorksCategoryMapper;
import com.bonniedraw.works.dao.WorksLikeMapper;
import com.bonniedraw.works.dao.WorksMapper;
import com.bonniedraw.works.dao.WorksMsgMapper;
import com.bonniedraw.works.model.Following;
import com.bonniedraw.works.model.TurnIn;
import com.bonniedraw.works.model.Works;
import com.bonniedraw.works.model.WorksCategory;
import com.bonniedraw.works.model.WorksLike;
import com.bonniedraw.works.model.WorksMsg;
import com.bonniedraw.works.service.MobileWorksService;

@Service
public class MobileWorksServiceImpl extends BaseService implements MobileWorksService {

	@Autowired
	WorksMapper worksMapper;
	
	@Autowired
	WorksCategoryMapper worksCategoryMapper;
	
	@Autowired
	WorksMsgMapper worksMsgMapper;
	
	@Autowired
	WorksLikeMapper worksLikeMapper;
	
	@Autowired
	FollowingMapper followingMapper;
	
	@Autowired
	TurnInMapper turnInMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer worksSave(WorksSaveRequestVO worksSaveRequestVO) {
		Integer wid = null;
		Date nowDate = TimerUtil.getNowDate();
		int ac = worksSaveRequestVO.getAc();
		int userId = worksSaveRequestVO.getUserId();
		Integer languageId = worksSaveRequestVO.getLanguageId();
		Integer countryId = worksSaveRequestVO.getCountryId();
		Works works = new Works();
		works.setUserId(worksSaveRequestVO.getUserId());
		works.setPrivacyType(worksSaveRequestVO.getPrivacyType());
		works.setTitle(worksSaveRequestVO.getTitle());
		works.setDescription(worksSaveRequestVO.getDescription());
		works.setLanguageId( (languageId ==null ? 1: languageId) );
		works.setCountryId( (countryId ==null ? 1 : countryId) );
		works.setUpdatedBy(userId);
		works.setUpdateDate(nowDate);
		List<WorksCategory> categoryList = worksSaveRequestVO.getCategoryList();
		
		try {
			if(ac==1){
				works.setCreatedBy(userId);
				works.setCreationDate(nowDate);
				worksMapper.insert(works);
				if(ValidateUtil.isNotEmptyAndSize(categoryList)){
					wid = works.getWorksId();
					for(WorksCategory worksCategory : categoryList){
						worksCategory.setWorksId(wid);
					}
					worksCategoryMapper.insertWorksCategoryList(categoryList);
				}
			}else{
				worksMapper.updateByPrimaryKeySelective(works);
				worksCategoryMapper.updateWorksCategoryList(categoryList);
				wid = works.getWorksId();
			}
		} catch (Exception e) {
			LogUtils.error(getClass(), "worksSave has error : " + e);
			callRollBack();
		}
		return wid;
	}

	@Override
	public List<WorksResponse> queryAllWorks(WorkListRequestVO workListRequestVO) {
		int wt = workListRequestVO.getWt();
		int rc = workListRequestVO.getRc();
		Integer stn = workListRequestVO.getStn();
		List<WorksResponse> worksResponseList = new ArrayList<WorksResponse>();
		
		switch (wt) {
		case 1:
			worksResponseList = worksMapper.queryAllWorks();
			if(ValidateUtil.isNotEmptyAndSize(worksResponseList)){
				for(WorksResponse worksResponse:worksResponseList){
					worksResponse.setLikeCount(worksResponse.getLikeList().size());
					worksResponse.setMsgCount(worksResponse.getMsgList().size());
				}
			}
			break;
		case 2:
			if(ValidateUtil.isNotNumNone(stn)){
				Map<String, Integer> pagerMap = new HashMap<String, Integer>();
				pagerMap.put("offset", (rc*(stn-1)));
				pagerMap.put("limit", rc);
				worksResponseList = worksMapper.queryPopularWorksPager(pagerMap);
			}else{
				worksResponseList = worksMapper.queryPopularWorks(rc);
			}
			break;
		case 4:
			worksResponseList = worksMapper.queryNewUploadWorks(rc);
			break;
		}
		
		return worksResponseList;
	}
	
	@Override
	public WorksResponse queryWorks(Integer wid) {
		WorksResponse worksResponse = worksMapper.queryWorks(wid);
		if(worksResponse!=null){
			worksResponse.setLikeCount(worksResponse.getLikeList().size());
			worksResponse.setMsgCount(worksResponse.getMsgList().size());
		}
		return worksResponse;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int leavemsg(LeaveMsgRequestVO leaveMsgRequestVO) {
		int success= 2;
		WorksMsg worksMsg = new WorksMsg();
		worksMsg.setWorksId(leaveMsgRequestVO.getWorksId());
		try {
			worksMsgMapper.insert(worksMsg);
			success = 1;
		} catch (Exception e) {
			LogUtils.error(getClass(), "setLike has error : " + e);
			callRollBack();
		}
		return success;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int setLike(SetLikeRequestVO setLikeRequestVO) {
		int success= 2;
		WorksLike worksLike = new WorksLike();
		worksLike.setUserId(setLikeRequestVO.getUi());
		worksLike.setWorksId(setLikeRequestVO.getWorksId());
		worksLike.setLikeType(setLikeRequestVO.getLikeType());
		try {
			worksLikeMapper.insert(worksLike);
			success = 1;
		} catch (Exception e) {
			LogUtils.error(getClass(), "setLike has error : " + e);
			callRollBack();
		}
		return success;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int setFollowing(SetFollowingRequestVO setFollowingRequestVO) {
		int success= 2;
		Following following = new Following();
		following.setUserId(setFollowingRequestVO.getUi());
		following.setFollowingUserId(setFollowingRequestVO.getFollowingUserId());
		try {
			if(setFollowingRequestVO.getFn() == 1){
				followingMapper.insert(following);
			}else{
				followingMapper.deleteByNotPrimaryKey(following);
			}
			success = 1;
		} catch (Exception e) {
			LogUtils.error(getClass(), "setLike has error : " + e);
			callRollBack();
		}
		return success;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int setTurnin(SetTurnInRequestVO setTurnInRequestVO) {
		int success = 2;
		Date nowDate = TimerUtil.getNowDate();
		TurnIn turnIn = new TurnIn();
		turnIn.setStatus(1);
		turnIn.setCreationDate(nowDate);
		turnIn.setUserId(setTurnInRequestVO.getUi());
		turnIn.setWorksId(setTurnInRequestVO.getWorksId());
		turnIn.setTurnInType(setTurnInRequestVO.getTurnInType());
		turnIn.setDescription(setTurnInRequestVO.getDescription());
		try {
			turnInMapper.insert(turnIn);
			success = 1;
		} catch (Exception e) {
			LogUtils.error(getClass(), "leavemsg has error : " + e);
			callRollBack();
		}
		return success;
	}

}
