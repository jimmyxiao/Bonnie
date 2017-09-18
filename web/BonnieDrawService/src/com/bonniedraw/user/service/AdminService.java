package com.bonniedraw.user.service;

import java.util.List;

import com.bonniedraw.user.model.AdminInfo;

public interface AdminService {
	List<AdminInfo> queryAdminList();
	AdminInfo queryAdminInfo(Integer adminId);
	int insertAdminInfo(AdminInfo adminInfo,Integer userId);
	int updateAdminInfo(AdminInfo adminInfo,Integer userId);
	int removeAdminInfo(Integer adminId);
}
