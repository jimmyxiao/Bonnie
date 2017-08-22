package com.bonniedraw.email;

import java.util.Properties;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.bonniedraw.util.LogUtils;

public class EmailProcess {

	public static boolean sendValideMail() {
		boolean success = false;
		try {
			String email = "";
			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			Properties mailProperties = new Properties();
			// mailProperties.put("mail.transport.protocol", systemSetup.getMailProtocol());
			mailProperties.put("mail.smtp.auth", true);
			mailProperties.put("mail.smtp.starttls.enable", true);
			mailProperties.put("mail.smtp.debug", true);
			// mailProperties.put("mail.from.email", systemSetup.getMailUsername());
			mailSender.setJavaMailProperties(mailProperties);
			// mailSender.setHost(systemSetup.getMailHost());
			// mailSender.setPort(systemSetup.getMailPort());
			// mailSender.setUsername(systemSetup.getMailUsername());
			// mailSender.setPassword(systemSetup.getMailPassword());

			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage,	false, "BIG5");
			String to = email;
			String subject = "";
			String body = "";
			message.setTo(to);
			message.setSubject(subject);
			message.setFrom(new InternetAddress(""));
			mimeMessage.setContent(body, "text/html; charset=utf-8");
			mailSender.send(mimeMessage);
			success = true;
		} catch (Exception e) {
			LogUtils.error(EmailProcess.class , "send valide mail has error : " + e);
		}
		return success;
	}

}
