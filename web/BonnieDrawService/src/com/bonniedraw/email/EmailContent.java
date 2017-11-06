package com.bonniedraw.email;

public final class EmailContent {
	//localhost
//	public static final String LINK =  "http://localhost:8080/BonnieDrawClient/#/complete?token=";
//	public static final String LOGIN_LINK = "http://localhost:8080/BonnieDrawClient/#/login";
	
	// release
	public static final String LINK = "https://www.bonniedraw.com/#/complete?token=";
	public static final String LOGIN_LINK = "https://www.bonniedraw.com/#/login";
	
	public static final String SUBJECT = "BonnieDRAW 註冊驗證通知";
	public static final String BODY = 
			"您好！下方連結是您的註冊驗證連結，請於此信通知30分鐘內完成驗證<br>如本人未申請註冊相關項目，請忽略此信件<br>"+
					"<a href=\"link_complete\" target=\"_blank\">link_complete</a>";
	
	public static final String EMAIL_SUBJECT = "BonnieDRAW 找回密碼";
	public static String getEmailBody(String userPwd ){
		return	"您好！您的密碼為 " + userPwd +" ，請至下方連結重新登入BonnieDRAW。<br><br>"	+ 
				"<a href=\" "+ LOGIN_LINK + " \">登入BonnieDRAW </a><br><br>"; 
	}
}
