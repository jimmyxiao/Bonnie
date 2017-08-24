package com.bonniedraw.login.service;

import com.bonniedraw.login.module.LoginInput;
import com.bonniedraw.login.module.LoginOutput;

public interface LoginService {
	public LoginOutput login(LoginInput loginInput);
	public LoginOutput loginBackend(LoginInput loginInput);
}
