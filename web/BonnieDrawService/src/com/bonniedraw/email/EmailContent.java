package com.bonniedraw.email;

public final class EmailContent {
	//localhost
//	static final String LINK =  "http://localhost:8080/BonnieDrawClient/#/complete?token=";
	
	// release
	static final String LINK = "https://www.bonniedraw.com/#/complete?token=";
	
	static final String SUBJECT = "BonnieDRAW 註冊驗證通知";
	
	static final String BODY = 
			"您好！下方連結是您的註冊驗證連結，請於此信通知30分鐘內完成驗證<br>如本人未申請註冊相關項目，請忽略此信件<br>"+
					"<a href=\"link_complete\" target=\"_blank\">link_complete</a>";
}
