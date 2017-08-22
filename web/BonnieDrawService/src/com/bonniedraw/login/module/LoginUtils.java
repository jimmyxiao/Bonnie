package com.bonniedraw.login.module;

import java.util.HashMap;
import java.util.Map;

public class LoginUtils {
	public static Map<Integer, Long> loginMap = new HashMap<Integer, Long>();

	public static int checkIsLastLogin(int loginID, long timekey) {
		Long iKey = loginMap.get(loginID);
		if (iKey == null || timekey > iKey.longValue()) {
			loginMap.put(loginID, timekey);
			return 1;
		}

		if (iKey != null && timekey < iKey.longValue()) {
			return 0;
		}
		return 1;
	}
}
