package com.sctw.bonniedraw.utility;

import android.content.Context;

import com.sctw.bonniedraw.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Fatorin on 2017/11/10.
 */

public class DateFormatString {

    public static String getDate(Context context, long time) {
        // 格式為日期
        SimpleDateFormat SDF_FULL = new SimpleDateFormat(context.getString(R.string.date_format), Locale.getDefault());
        Calendar mNowCalendar = Calendar.getInstance();// 現在時間
        Calendar mMsgCalendar = Calendar.getInstance();// 當天
        mMsgCalendar.setTimeInMillis(time);
        // 現在時間
        int nowYear = mNowCalendar.get(Calendar.YEAR);
        int nowMouth = mNowCalendar.get(Calendar.MONTH) + 1;
        int nowDay = mNowCalendar.get(Calendar.DAY_OF_MONTH);
        int nowHour = mNowCalendar.get(Calendar.HOUR);
        int nowMin = mNowCalendar.get(Calendar.MINUTE);
        int nowSec = mNowCalendar.get(Calendar.SECOND);
        int nowAM_PM = mNowCalendar.get(Calendar.AM_PM);

        // 留言時間
        int msgYear = mMsgCalendar.get(Calendar.YEAR);
        int msgMouth = mMsgCalendar.get(Calendar.MONTH) + 1;
        int msgDay = mMsgCalendar.get(Calendar.DAY_OF_MONTH);
        int msgHour = mMsgCalendar.get(Calendar.HOUR);
        int msgMin = mMsgCalendar.get(Calendar.MINUTE);
        int msgSec = mMsgCalendar.get(Calendar.SECOND);
        int msgAM_PM = mMsgCalendar.get(Calendar.AM_PM);

        // 依序檢查年月日時間
        if (nowYear == msgYear) {
            if (nowMouth == msgMouth) {
                if (nowDay == msgDay) {
                    if (nowHour == msgHour) {
                        if (nowMin == msgMin) {
                            if (nowSec - msgSec == 0) {
                                return context.getString(R.string.just_now);
                            } else {
                                return context.getString(R.string.a_few_seconds_ago);
                            }
                        } else {
                            return nowMin - msgMin + context.getString(R.string.a_few_minutes_ago);
                        }
                    } else {
                        return nowHour - msgHour + context.getString(R.string.a_few_hours_ago);
                    }
                } else {
                    if (nowDay - msgDay < 2) {
                        return context.getString(R.string.yesterday) + checkAMorPM(msgAM_PM) + msgHour + context.getString(R.string.hour) + msgMin + context.getString(R.string.min);
                    } else if (nowDay - msgDay < 3) {
                        return context.getString(R.string.the_day_before_yesterday) + checkAMorPM(msgAM_PM) + msgHour + context.getString(R.string.hour) + msgMin + context.getString(R.string.min);
                    } else if (nowDay - msgDay < 7) {
                        return nowDay - msgDay + context.getString(R.string.a_few_days_ago);
                    } else {
                        return SDF_FULL.format(time);
                    }
                }
            } else {
                return SDF_FULL.format(time);
            }
        } else {
            return SDF_FULL.format(time);
        }
    }

    private static String checkAMorPM(int type) {
        switch (type) {
            case 0:
                //上午
                return "上午";
            case 1:
                //下午
                return "下午";
            default:
                return "";
        }
    }
}
