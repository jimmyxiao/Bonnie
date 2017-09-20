package com.bonniedraw.user.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bonniedraw.base.service.BaseService;
import com.bonniedraw.user.dao.AdminInfoMapper;
import com.bonniedraw.user.model.AdminInfo;
import com.bonniedraw.user.service.AdminService;
import com.bonniedraw.util.LogUtils;
import com.bonniedraw.util.TimerUtil;

@Service
public class AdminServiceImpl extends BaseService implements AdminService {
	
	@Autowired
	AdminInfoMapper adminInfoMapper;

	@Override
	public List<AdminInfo> queryAdminList() {
		return adminInfoMapper.queryAdminList();
	}

	@Override
	public AdminInfo queryAdminInfo(Integer adminId) {
		return adminInfoMapper.selectByPrimaryKey(adminId);
	}

	@Override
	public int insertAdminInfo(AdminInfo adminInfo, Integer userId) {
		AdminInfo existAdminInfo = adminInfoMapper.selectByUserCode(adminInfo.getUserCode());
		if(existAdminInfo !=null){
			return -1;
		}
		Date nowDate = TimerUtil.getNowDate();
		adminInfo.setCreatedBy(userId);
		adminInfo.setCreationDate(nowDate);
		adminInfo.setUpdatedBy(userId);
		adminInfo.setUpdateDate(nowDate);
		return adminInfoMapper.insert(adminInfo);
	}

	@Override
	public int updateAdminInfo(AdminInfo adminInfo, Integer userId) {
		int success = 0;
		try {
			Date nowDate = TimerUtil.getNowDate();
			adminInfo.setUpdatedBy(userId);
			adminInfo.setUpdateDate(nowDate);
			adminInfoMapper.updateByPrimaryKeySelective(adminInfo);
			success =1;
		} catch (Exception e) {
			LogUtils.error(getClass(), "updateAdminInfo has error : " + e);
		}
		return success;
	}

	@Override
	public int removeAdminInfo(Integer adminId) {
		return adminInfoMapper.deleteByPrimaryKey(adminId);
	}

}
