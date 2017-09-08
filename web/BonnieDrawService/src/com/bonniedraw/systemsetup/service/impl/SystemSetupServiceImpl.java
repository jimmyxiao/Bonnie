package com.bonniedraw.systemsetup.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bonniedraw.base.service.BaseService;
import com.bonniedraw.systemsetup.dao.SystemSetupMapper;
import com.bonniedraw.systemsetup.model.SystemSetup;
import com.bonniedraw.systemsetup.service.SystemSetupService;
import com.bonniedraw.util.LogUtils;

@Service
public class SystemSetupServiceImpl extends BaseService implements SystemSetupService {

	@Autowired
	SystemSetupMapper systemSetupMapper;
	
	@Override
	public SystemSetup querySystemSetup() {
		return systemSetupMapper.selectByPrimaryKey(1);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int insertSystemSetup(SystemSetup systemSetup, Integer userId) {
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
	public int updateSystemSetup(SystemSetup systemSetup, Integer userId) {
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

}
