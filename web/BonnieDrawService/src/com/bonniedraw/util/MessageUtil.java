package com.bonniedraw.util;



import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Not Uses
 */
public class MessageUtil {
	
	@Autowired
	private ResourceBundleMessageSource messageSource;
	
	public String getMessage(String text,Locale locale){
		if(messageSource==null){
			return text;
		}
		return messageSource.getMessage(text, null ,locale);
	}
	
}
