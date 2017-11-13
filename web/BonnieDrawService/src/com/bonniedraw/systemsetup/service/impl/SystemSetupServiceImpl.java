package com.bonniedraw.systemsetup.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bonniedraw.base.service.BaseService;
import com.bonniedraw.notification.dao.NotiMsgInfoMapper;
import com.bonniedraw.notification.model.NotiMsgInfo;
import com.bonniedraw.systemsetup.dao.SystemSetupMapper;
import com.bonniedraw.systemsetup.model.SystemSetup;
import com.bonniedraw.systemsetup.service.SystemSetupService;
import com.bonniedraw.util.LogUtils;

@Service
public class SystemSetupServiceImpl extends BaseService implements SystemSetupService {

	@Autowired
	SystemSetupMapper systemSetupMapper;
	
	@Autowired
	NotiMsgInfoMapper notiMsgInfoMapper;
	
	@Override
	public SystemSetup querySystemSetup() {
		return systemSetupMapper.selectByPrimaryKey(1);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int insertSystemSetup(SystemSetup systemSetup) {
		int success = 0;
		try {	
			systemSetupMapper.insert(systemSetup);
			success = 1;
		} catch (Exception ex) {
			LogUtils.error(getClass(), "insertSystemSetup has error : " + ex);
			callRollBack();
		}		
		return success;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateSystemSetup(SystemSetup systemSetup) {
		int success = 0;
		try{
			systemSetupMapper.updateByPrimaryKey(systemSetup);
			success = 1;
		}catch(Exception ex){
			LogUtils.error(getClass(), "updateSystemSetup has error :" + ex);
			callRollBack();
		}
		return success;
	}

	@Override
	public String queryNotiMsgSetting(NotiMsgInfo notiMsgInfo) {
		NotiMsgInfo result = notiMsgInfoMapper.selectByPrimaryKey(notiMsgInfo);
		if(result==null){
			return "";
		}
		return result.getMessage1();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int saveNotiMsgSetting(NotiMsgInfo notiMsgInfo) {
		int success = 0;
		try{
			if(notiMsgInfoMapper.selectByPrimaryKey(notiMsgInfo)!=null){
				notiMsgInfoMapper.updateByPrimaryKeySelective(notiMsgInfo);
			}else{
				notiMsgInfoMapper.insertSelective(notiMsgInfo);
			}
			success = 1;
		}catch(Exception ex){
			LogUtils.error(getClass(), "saveNotiMsgSetting has error :" + ex);
			callRollBack();
		}
		return success;
	}

}
