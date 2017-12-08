package com.bonniedraw.util;

import java.util.Properties;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.bonniedraw.systemsetup.model.SystemSetup;

public class EmailUtil {
	
	public static boolean send(SystemSetup systemSetup, String to, String subject , String body) throws Exception{
		if(systemSetup!=null 
				&& systemSetup.getMailHost()!=null && systemSetup.getMailPort()!=null 
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
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "BIG5");
			message.setTo(to);
			message.setSubject(subject);
			message.setFrom(new InternetAddress(systemSetup.getMailUsername()));
			mimeMessage.setContent(body,"text/html; charset=utf-8");
			mailSender.send(mimeMessage);
			return true;
		}else{
			return false;
		}
	}
}
