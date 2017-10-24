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
import com.bonniedraw.util.HashTagUtil;
import com.bonniedraw.util.LogUtils;
import com.bonniedraw.util.TimerUtil;
import com.bonniedraw.util.ValidateUtil;
import com.bonniedraw.web_api.model.request.LeaveMsgRequestVO;
import com.bonniedraw.web_api.model.request.SetCollectionRequestVO;
import com.bonniedraw.web_api.model.request.SetFollowingRequestVO;
import com.bonniedraw.web_api.model.request.SetLikeRequestVO;
import com.bonniedraw.web_api.model.request.SetTurnInRequestVO;
import com.bonniedraw.web_api.model.request.WorkListRequestVO;
import com.bonniedraw.web_api.model.request.WorksSaveRequestVO;
import com.bonniedraw.web_api.module.CategoryInfoResponse;
import com.bonniedraw.web_api.module.WorksResponse;
import com.bonniedraw.works.dao.CategoryInfoMapper;
import com.bonniedraw.works.dao.FollowingMapper;
import com.bonniedraw.works.dao.TurnInMapper;
import com.bonniedraw.works.dao.WorksCategoryMapper;
import com.bonniedraw.works.dao.WorksCollectionMapper;
import com.bonniedraw.works.dao.WorksLikeMapper;
import com.bonniedraw.works.dao.WorksMapper;
import com.bonniedraw.works.dao.WorksMsgMapper;
import com.bonniedraw.works.dao.WorksTagMapper;
import com.bonniedraw.works.model.CategoryInfo;
import com.bonniedraw.works.model.Following;
import com.bonniedraw.works.model.TurnIn;
import com.bonniedraw.works.model.Works;
import com.bonniedraw.works.model.WorksCategory;
import com.bonniedraw.works.model.WorksCollection;
import com.bonniedraw.works.model.WorksLike;
import com.bonniedraw.works.model.WorksMsg;
import com.bonniedraw.works.model.WorksTag;
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
	
	@Autowired
	CategoryInfoMapper categoryInfoMapper;
	
	@Autowired
	WorksCollectionMapper worksCollectionMapper;
	
	@Autowired
	WorksTagMapper worksTagMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer worksSave(WorksSaveRequestVO worksSaveRequestVO) {
		Integer wid = null;
		Date nowDate = TimerUtil.getNowDate();
		int ac = worksSaveRequestVO.getAc();
		int userId = worksSaveRequestVO.getUi();
		Works works = new Works();
		works.setUserId(userId);
		works.setDeviceType(worksSaveRequestVO.getDt());
		works.setPrivacyType(worksSaveRequestVO.getPrivacyType());
		works.setTitle(worksSaveRequestVO.getTitle());
		works.setDescription(worksSaveRequestVO.getDescription());
		works.setLanguageCode(worksSaveRequestVO.getLanguageCode());
		works.setCountryCode(worksSaveRequestVO.getCountryCode());
		works.setUpdatedBy(userId);
		works.setUpdateDate(nowDate);
		List<CategoryInfo> categoryList = worksSaveRequestVO.getCategoryList();
		ArrayList<String> sharpTagList = new ArrayList<String>();
		sharpTagList = HashTagUtil.extractSharpTag(works.getDescription());
		
		try {
			if(ac==1){
				works.setStatus(1);
				works.setCreatedBy(userId);
				works.setCreationDate(nowDate);
				worksMapper.insert(works);
				wid = works.getWorksId();
				if(ValidateUtil.isNotEmptyAndSize(categoryList)){
					insertWorksCatrgorList(categoryList, wid);
				}
				if(ValidateUtil.isNotEmptyAndSize(sharpTagList)){
					insertWorksTagList(sharpTagList, wid);
				}
			}else if(ac==2){
				wid = worksSaveRequestVO.getWorksId();
				works.setWorksId(wid);
				worksMapper.updateByPrimaryKeySelective(works);
				compareCategory(categoryList, wid);
				compareWorksTag(sharpTagList, wid);
			}
		} catch (Exception e) {
			LogUtils.error(getClass(), "worksSave has error : " + e);
			callRollBack();
		}
		return wid;
	}
	
	private void insertWorksCatrgorList(List<CategoryInfo> categoryList, int wid) throws Exception {
		List<WorksCategory> insertList = new ArrayList<WorksCategory>();
		for(CategoryInfo categoryInfo : categoryList){
			WorksCategory worksCategory = new WorksCategory();
			worksCategory.setWorksCategoryId(categoryInfo.getCategoryId());
			worksCategory.setWorksId(wid);
			insertList.add(worksCategory);
		}
		worksCategoryMapper.insertWorksCategoryList(insertList);
	}
	
	private void compareCategory(List<CategoryInfo> categoryList, int wid) throws Exception {
		List<WorksCategory> existWorksCategories = worksCategoryMapper.selectByWorksId(wid);
		//資料庫與參數皆有資料,進行比對並移除陣列內相同的unique id,最終剩下的資料庫資料進行刪除,反之參數資料進行新增
		if(ValidateUtil.isNotEmptyAndSize(existWorksCategories) && ValidateUtil.isNotEmptyAndSize(categoryList)){
			for(WorksCategory worksCategory : existWorksCategories){
				int categoryId = worksCategory.getCategoryId();
				for(CategoryInfo data : categoryList){
					if(categoryId == data.getCategoryId()){
						categoryList.remove(data);
						existWorksCategories.remove(worksCategory);
						break;
					}
				}
			}
			insertWorksCatrgorList(categoryList, wid);
			worksCategoryMapper.deleteWorksCategoryList(existWorksCategories);
		}else if(ValidateUtil.isNotEmptyAndSize(existWorksCategories) && !ValidateUtil.isNotEmptyAndSize(categoryList)){	//資料庫有資料而參數無資料,只執行刪除
			worksCategoryMapper.deleteWorksCategoryList(existWorksCategories);
		}else if(!ValidateUtil.isNotEmptyAndSize(existWorksCategories) && ValidateUtil.isNotEmptyAndSize(categoryList)){	//資料庫無資料而參數有資料,只執行新增
			insertWorksCatrgorList(categoryList, wid);
		}
	}
	
	private void insertWorksTagList(List<String> list, int wid) throws Exception {
		List<WorksTag> insertList = new ArrayList<WorksTag>();
		int order = worksTagMapper.selectNextOrderNum(wid);
		for(String tag : list){
			if(ValidateUtil.isNotBlank(tag)){
				WorksTag worksTag = new WorksTag();
				worksTag.setWorksId(wid);
				worksTag.setTagName(tag);
				worksTag.setTagOrder(order);
				insertList.add(worksTag);
				order++;
			}
		}
		worksTagMapper.insertWorksTagList(insertList);
	}
	
	private void compareWorksTag(ArrayList<String> sharpTagList, int wid) throws Exception {
		List<WorksTag> existWorksTags = worksTagMapper.selectByWorksId(wid);
		if(ValidateUtil.isNotEmptyAndSize(existWorksTags) && ValidateUtil.isNotEmptyAndSize(sharpTagList)){	//比對是否有不同的Tag
			ArrayList<String> insertSharpTagList = (ArrayList<String>) sharpTagList.clone();
			for(int i=0;i<existWorksTags.size();i++){
				WorksTag worksTag = existWorksTags.get(i);
				String worksTagName = worksTag.getTagName();
				for(String data : sharpTagList){
					if(worksTagName.equals(data)){
						sharpTagList.remove(data);
						existWorksTags.remove(worksTag);
						i--;
						break;
					}
				}
			}
			if(ValidateUtil.isNotEmptyAndSize(existWorksTags) || ValidateUtil.isNotEmptyAndSize(sharpTagList)){ 	//當資料庫與敘述其一有資料存在, 進行刪除再新增
				worksTagMapper.deleteByWorksId(wid);
				insertWorksTagList(insertSharpTagList, wid); 
			}
		}else if(ValidateUtil.isNotEmptyAndSize(existWorksTags) && !ValidateUtil.isNotEmptyAndSize(sharpTagList)){	
			worksTagMapper.deleteByWorksId(wid);
		}else if(!ValidateUtil.isNotEmptyAndSize(existWorksTags) && ValidateUtil.isNotEmptyAndSize(sharpTagList)){	
			insertWorksTagList(sharpTagList, wid);
		}
	}
	

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateWorksFilePath(int wid, int fType, String path) {
		Works updateWork = new Works();
		updateWork.setWorksId(wid);
		if(fType == 1){
			updateWork.setImagePath(path);
		}else if(fType == 2){
			updateWork.setBdwPath(path);
		}else{
			return false;
		}
		
		try {
			worksMapper.updateByPrimaryKeySelective(updateWork);
			return true;
		} catch (Exception e) {
			LogUtils.error(getClass(), "updateWorksFilePath has error : " + e);
		}
		return false;
	}
	
	@Override
	public List<WorksResponse> queryAllWorks(WorkListRequestVO workListRequestVO) {
		int userId = workListRequestVO.getUi();
		int wt = workListRequestVO.getWt();
		int rc = workListRequestVO.getRc();
		List<WorksResponse> worksResponseList = new ArrayList<WorksResponse>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("rc", rc);
		
		switch (wt) {
		case 1:
			worksResponseList = worksMapper.queryAllWorks(paramMap);
			if(ValidateUtil.isNotEmptyAndSize(worksResponseList)){
				for(WorksResponse worksResponse:worksResponseList){
					worksResponse.setLikeCount(worksResponse.getLikeList().size());
					worksResponse.setMsgCount(worksResponse.getMsgList().size());
				}
			}
			break;
		case 2:
				worksResponseList = worksMapper.queryPopularWorks(paramMap);
			break;
		case 4:
			worksResponseList = worksMapper.queryNewUploadWorks(paramMap);
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
		case 6:
//			maxPagination = worksMapper.seletMaxPaginationBindUser(pagerMap);
			Integer queryId = workListRequestVO.getQueryId();
			if(queryId!=null && queryId>0 && queryId!=workListRequestVO.getUi()){
				pagerMap.put("queryId", workListRequestVO.getQueryId());
				worksResponseList = worksMapper.queryOtherUserWorksPager(pagerMap);
			}
			break;
		case 7:
			maxPagination = worksMapper.seletMaxPaginationBindCollection(pagerMap);
			worksResponseList = worksMapper.queryCollectionWorksPager(pagerMap);
			break;
		}
		
		resultMap.put("worksResponseList", worksResponseList);
		resultMap.put("maxPagination", maxPagination);
		return resultMap;
	}
	
	@Override
	public Map<String, Object> queryAllWorksAndPaginationByCategory(WorkListRequestVO workListRequestVO) {
		int wt = workListRequestVO.getWt();
		int rc = workListRequestVO.getRc();
		Integer stn = workListRequestVO.getStn();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<WorksResponse> worksResponseList = new ArrayList<WorksResponse>();
		int maxPagination = 0;
		Map<String, Object> pagerMap = new HashMap<String, Object>();
		pagerMap.put("offset", (rc*(stn-1)));
		pagerMap.put("limit", rc);
		pagerMap.put("userId", workListRequestVO.getUi());
		List<Integer> worksIdList = worksCategoryMapper.selectWorksIdList(wt);
		if(ValidateUtil.isNotEmptyAndSize(worksIdList)){
			maxPagination = (int) Math.ceil( Double.parseDouble(String.valueOf(worksIdList.size())) / Double.parseDouble(String.valueOf(rc)) );
			pagerMap.put("list", worksIdList);
			worksResponseList = worksMapper.queryCategoryWorksPager(pagerMap);
		}
		
		resultMap.put("worksResponseList", worksResponseList);
		resultMap.put("maxPagination", maxPagination);
		return resultMap;
	}
	
	@Override
	public WorksResponse queryWorks(Integer wid, int userId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("wid", wid);
		paramMap.put("userId", userId);
		WorksResponse worksResponse = worksMapper.queryWorks(paramMap);
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
		int fn = leaveMsgRequestVO.getFn();
		WorksMsg worksMsg = new WorksMsg();
		worksMsg.setWorksId(leaveMsgRequestVO.getWorksId());
		worksMsg.setUserId(leaveMsgRequestVO.getUi());
		try {
			if(fn==1){
				worksMsg.setMessage(leaveMsgRequestVO.getMessage());
				Date nowDate = TimerUtil.getNowDate();
				worksMsg.setCreationDate(nowDate);
				worksMsgMapper.insert(worksMsg);
			}else{
				int worksMsgId = leaveMsgRequestVO.getMsgId();
				worksMsg.setWorksMsgId(worksMsgId);
				WorksMsg existWorksMsg = worksMsgMapper.selectExistMsg(worksMsg);
				if(existWorksMsg!=null){
					worksMsgMapper.deleteByPrimaryKey(worksMsgId);
				}
			}
			success = 1;
		} catch (Exception e) {
			LogUtils.error(getClass(), "leavemsg has error : " + e);
			callRollBack();
		}
		return success;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int setLike(SetLikeRequestVO setLikeRequestVO) {
		int success= 2;
		int fn = setLikeRequestVO.getFn();
		WorksLike worksLike = new WorksLike();
		worksLike.setUserId(setLikeRequestVO.getUi());
		worksLike.setWorksId(setLikeRequestVO.getWorksId());
		worksLike.setLikeType(setLikeRequestVO.getLikeType());	
		try {
			if(fn==1){
				worksLikeMapper.insert(worksLike);
			}else{
				worksLikeMapper.deleteByNotPrimaryKey(worksLike);
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
			LogUtils.error(getClass(), "setTurnin has error : " + e);
			callRollBack();
		}
		return success;
	}
	
	@Override
	public List<Point> getDrawingPlay(int wid, int userId) {
		List<Point> pointList = new ArrayList<Point>();
		Works works = worksMapper.selectByPrimaryKey(wid);
		if(works!=null){
			if(works.getUserId() == userId || (works.getStatus()==1 && works.getPrivacyType()==1 && ValidateUtil.isNotBlank(works.getBdwPath())) ){
				byte[] bdwBytes = null;
				String path = works.getBdwPath();
				String rootPath = System.getProperty("catalina.home");
				String filePath = rootPath + path;
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
			}
		}
		return pointList;
	}

	private void getAllChildern(List<CategoryInfoResponse> categoryList){
		if(ValidateUtil.isNotEmptyAndSize(categoryList)){
			for(CategoryInfoResponse categoryInfoResponse: categoryList){
				Integer categoryId = categoryInfoResponse.getCategoryId();
				categoryInfoResponse.setCategoryList(categoryInfoMapper.getChildernList(categoryId));
				getAllChildern(categoryInfoResponse.getCategoryList());
			}
		}
	}
	
	@Override
	public List<CategoryInfoResponse> getCategoryList(Integer categoryId) {
		List<CategoryInfoResponse> categoryList = categoryInfoMapper.getCategoryList(categoryId);
		getAllChildern(categoryList);
		return categoryList;
	}

	@Override
	public int setCollection(SetCollectionRequestVO setCollectionRequestVO) {
		int fn = setCollectionRequestVO.getFn();
		int worksId = setCollectionRequestVO.getWorksId();
		int userId = setCollectionRequestVO.getUi();
		WorksCollection worksCollection = new WorksCollection();
		worksCollection.setWorksId(worksId);
		worksCollection.setUserId(userId);
		try {
			WorksCollection existWorksCollection = worksCollectionMapper.selectByWorksAndUser(worksCollection);
			if(fn==1){
				if(existWorksCollection!=null && existWorksCollection.getCollectionType()==0){
					existWorksCollection.setCollectionType(1);
					worksCollectionMapper.updateByPrimaryKey(existWorksCollection);
				}else if(existWorksCollection ==null ){
					worksCollection.setCollectionType(1);
					worksCollectionMapper.insert(worksCollection);
				}
			}else{
				if(existWorksCollection !=null && existWorksCollection.getCollectionType() == 1){
					existWorksCollection.setCollectionType(0);
					worksCollectionMapper.updateByPrimaryKey(existWorksCollection);
				}
			}
		} catch (Exception e) {
			LogUtils.error(getClass(), "setCollection has error : " + e);
			callRollBack();
			return 2;
		}
		return 1;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int deleteWork(int userId, int worksId) {
		Works works = worksMapper.selectByPrimaryKey(worksId);
		try {
			if(works !=null && works.getUserId().equals(userId)){
				worksMapper.deleteByPrimaryKey(worksId);
			}else{
				return 3;
			}
		} catch (Exception e) {
			LogUtils.error(getClass(), "deleteWork has error : " + e);
			callRollBack();
			return 2;
		}
		return 1;
	}

}
