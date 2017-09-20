package com.bonniedraw.systemsetup.service;

import com.bonniedraw.systemsetup.model.SystemSetup;

public interface SystemSetupService {
	public SystemSetup querySystemSetup();
	public int insertSystemSetup(SystemSetup systemSetup);
	public int updateSystemSetup(SystemSetup systemSetup);
}
