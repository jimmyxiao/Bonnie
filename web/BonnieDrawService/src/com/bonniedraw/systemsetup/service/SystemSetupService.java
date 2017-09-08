package com.bonniedraw.systemsetup.service;

import com.bonniedraw.systemsetup.model.SystemSetup;

public interface SystemSetupService {
	public SystemSetup querySystemSetup();
	public int insertSystemSetup(SystemSetup systemSetup,Integer userId);
	public int updateSystemSetup(SystemSetup systemSetup,Integer userId);
}
