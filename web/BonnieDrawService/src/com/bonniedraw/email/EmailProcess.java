package com.bonniedraw.email;

import java.util.Properties;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import com.bonniedraw.systemsetup.model.SystemSetup;
import com.bonniedraw.systemsetup.service.SystemSetupService;
import com.bonniedraw.user.model.UserInfo;
import com.bonniedraw.util.LogUtils;
import com.bonniedraw.util.ValidateUtil;

public class EmailProcess {
	
	public static boolean sendForgetMail(SystemSetupService systemSetupService , UserInfo userInfo) {
		boolean success = false;
		return success;
	}
	
	public static boolean sendValideMail(SystemSetupService systemSetupService , UserInfo userInfo) {
		boolean success = false;
		try {
			String email = userInfo.getEmail();
			SystemSetup systemSetup = systemSetupService.querySystemSetup();
			if(systemSetup!=null && systemSetup.getMailHost()!=null && systemSetup.getMailPort()!=null 
					&& systemSetup.getMailUsername()!=null && systemSetup.getMailPassword()!=null ){
				JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
				Properties mailProperties = new Properties();
				if(ValidateUtil.isNotBlank(systemSetup.getMailProtocol())){
					mailProperties.put("mail.transport.protocol", systemSetup.getMailProtocol());
				}
				mailProperties.put("mail.smtp.auth", true);
				mailProperties.put("mail.smtp.starttls.enable", true);
				mailProperties.put("mail.smtp.debug", true);
				mailProperties.put("mail.from.email", systemSetup.getMailUsername());
				mailProperties.put("mail.smtp.ssl.trust", systemSetup.getMailHost());
				mailProperties.put("mail.smtp.socketFactory.port", systemSetup.getMailHost());
				mailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				mailSender.setJavaMailProperties(mailProperties);
				mailSender.setHost(systemSetup.getMailHost());
				mailSender.setPort(systemSetup.getMailPort());
				mailSender.setUsername(systemSetup.getMailUsername());
				mailSender.setPassword(systemSetup.getMailPassword());
	
				MimeMessage mimeMessage = mailSender.createMimeMessage();
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage,	false, "BIG5");
				String to = email;
				String subject = EmailContent.SUBJECT;
				String link = EmailContent.LINK + userInfo.getRegData();
				String body = new String(EmailContent.BODY).replace("link_complete", link);
				message.setTo(to);
				message.setSubject(subject);
				message.setFrom(new InternetAddress(systemSetup.getMailUsername()));
				mimeMessage.setContent(body, "text/html; charset=utf-8");
				mailSender.send(mimeMessage);
				success = true;
			}
		} catch (Exception e) {
			LogUtils.error(EmailProcess.class , "send valide mail has error : " + e);
		}
		return success;
	}

}
