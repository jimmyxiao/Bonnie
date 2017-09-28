package com.bonniedraw.works.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bonniedraw.base.service.BaseService;
import com.bonniedraw.file.Point;
import com.bonniedraw.util.BinaryUtil;
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
import com.bonniedraw.works.service.WorksServiceAPI;

@Service
public class WorksServiceAPIImpl extends BaseService implements WorksServiceAPI {

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
				worksResponseList = worksMapper.queryPopularWorks(rc);
			break;
		case 4:
			worksResponseList = worksMapper.queryNewUploadWorks(rc);
			break;
		}
		return worksResponseList;
	}
	
	@Override
	public Map<String, Object> queryAllWorksAndPagination(WorkListRequestVO workListRequestVO) {
		int wt = workListRequestVO.getWt();
		int rc = workListRequestVO.getRc();
		Integer stn = workListRequestVO.getStn();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<WorksResponse> worksResponseList = new ArrayList<WorksResponse>();
		int maxPagination = 0;
		
		Map<String, Integer> pagerMap = new HashMap<String, Integer>();
		pagerMap.put("offset", (rc*(stn-1)));
		pagerMap.put("limit", rc);
		pagerMap.put("userId", workListRequestVO.getUi());
		switch (wt) {
		case 1:
			maxPagination = worksMapper.seletMaxPaginationBindFollow(pagerMap);
			worksResponseList = worksMapper.queryTrackWorksPager(pagerMap);
			break;
		case 2:
			maxPagination = worksMapper.seletMaxPagination(rc);
			worksResponseList = worksMapper.queryPopularWorksPager(pagerMap);
			break;
		case 4:
			break;
		case 5:
			maxPagination = worksMapper.seletMaxPaginationBindUser(pagerMap);
			worksResponseList = worksMapper.queryUserWorksPager(pagerMap);
			break;
		}
		
		resultMap.put("worksResponseList", worksResponseList);
		resultMap.put("maxPagination", maxPagination);
		return resultMap;
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
	
	@Override
	public List<Point> getDrawingPlay(int wid, int userId) {
		List<Point> pointList = new ArrayList<Point>();
		byte[] bdwBytes = null;
		StringBuffer path = new StringBuffer();
		path.append(wid).append(".bdw");
		String rootPath = System.getProperty("catalina.home");
		String filePath = rootPath + "/files/" + path;
		File file = new File(filePath);
		try{
			if(file.exists()){
				bdwBytes = org.apache.commons.io.FileUtils.readFileToByteArray(file);
				for(int i=0; i< bdwBytes.length; i+=20){
					int length = BinaryUtil.combindTwoBytes(bdwBytes[i], bdwBytes[i+1]);
					int functionCode = BinaryUtil.combindTwoBytes(bdwBytes[i+2], bdwBytes[i+3]);
					int xPos = BinaryUtil.combindTwoBytes(bdwBytes[i+4], bdwBytes[i+5]);
					int yPos = BinaryUtil.combindTwoBytes(bdwBytes[i+6], bdwBytes[i+7]);
					String color = String.valueOf(
							BinaryUtil.combindTwoBytes((byte) BinaryUtil.combindTwoBytes(bdwBytes[i+8], bdwBytes[i+9]) , (byte) BinaryUtil.combindTwoBytes(bdwBytes[i+10], bdwBytes[i+11]))); 
					int action = bdwBytes[i+12];
					int size = BinaryUtil.combindTwoBytes(bdwBytes[i+13], bdwBytes[i+14]);
					int brush = bdwBytes[i+15];
					int time = BinaryUtil.combindTwoBytes(bdwBytes[i+16], bdwBytes[i+17]);
					int reserve = BinaryUtil.combindTwoBytes(bdwBytes[i+18], bdwBytes[i+19]);
					Point point = new Point();
					point.setLength(length);
					point.setFc(functionCode);
					point.setxPos(xPos);
					point.setyPos(yPos);
					point.setColor(color);
					point.setAction(action);
					point.setSize(size);
					point.setBrush(brush);
					point.setTime(time);
					point.setReserve(reserve);
					pointList.add(point);
				}
			}
		}catch(IOException e){
			LogUtils.fileConteollerError(filePath + " loadFile has error : 輸出發生異常 =>" +e);
		}
		return pointList;
	}

}
