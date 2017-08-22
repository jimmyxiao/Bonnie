package com.bonniedraw.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimerUtil {
	
//	private static final Locale LOCALE = Locale.TAIWAN;
	private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Asia/Taipei");
	
	public static final SimpleDateFormat getSimpleDateFormat(String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
		return sdf;
	}
	
	public static final Date getNowDate(){
		Calendar cSchedStartCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cSchedStartCal.setTimeZone(TIME_ZONE);
		return cSchedStartCal.getTime();
	}
	
}
