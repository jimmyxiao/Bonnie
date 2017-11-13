package com.sctw.bonniedraw.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Fatorin on 2017/11/10.
 */

public class DateFormatString {
    private static final SimpleDateFormat SDF_FULL = new SimpleDateFormat("yyyy年MM月dd日 ahh:mm", Locale.TAIWAN);
    private static final int DAY_MS = 60 * 60 * 24;
    private static final int HR_MS = 60 * 60;
    private static final int MIN_MS = 60;
    //小於1分鐘顯示秒，小於1小時顯示分鐘，小於1天顯示小時，小於2天顯示昨天

    public static String getDate(long time) {
        //格式為日期
        Calendar mCalendar = Calendar.getInstance();
        long now = System.currentTimeMillis();
        int sub = (int) (now - time) / 1000; //秒
        mCalendar.setTimeInMillis(sub * 1000L);

        if (sub < MIN_MS) {
            return "數秒前";
        } else if (sub < HR_MS) {
            return sub / 60 + "分前";
        } else if (sub < DAY_MS) {
            return sub / 3600 + "小時前";
        } else if (sub < DAY_MS * 2) {
            int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
            String subHour = String.valueOf(hour - sub / 3600);
            String AM_PM = "";
            switch (mCalendar.get(Calendar.AM_PM)) {
                case 0:
                    //上午
                    AM_PM = "上午";
                    break;
                case 1:
                    //下午
                    AM_PM = "下午";
                    break;
            }
            return "昨天" + AM_PM + subHour + "點";
        } else {
            return SDF_FULL.format(time);
        }
    }
}
