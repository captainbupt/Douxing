package com.badou.mworking.util;

import android.content.Context;

import com.badou.mworking.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeTransfer {
    /**
     * 设置月日*
     */
    public static String long2StringDate(Context context, long timeMills) {
        SimpleDateFormat df = new SimpleDateFormat("MM" + "-"
                + "dd");
        String today = df.format(Calendar.getInstance().getTime());
        String result = df.format(new Date(timeMills));
        return today.equals(result) ? context.getResources().getString(R.string.time_text_jinTian) : result;
    }

    public static String long2StringDateUnit(long timeMills) {
        SimpleDateFormat df = new SimpleDateFormat("MM月dd日");
        return df.format(new Date(timeMills));
    }

    /**
     * 设置分秒*
     */
    public static String long2StringTime(long timeMills) {
        SimpleDateFormat df = new SimpleDateFormat("mm" + ":" + "ss");
        return df.format(new Date(timeMills));
    }

    public static String long2StringTimeHour(long timeMills) {
        SimpleDateFormat df = new SimpleDateFormat("HH" + ":" + "mm");
        return df.format(new Date(timeMills));
    }

    /**
     * 设置月日 时间*
     */
    public static String long2StringDetailDate(Context context, long timeMills) {
        return long2StringDate(context, timeMills) + " " + long2StringTimeHour(timeMills);
    }

    /**
     * 超过一小时的时间
     */
    public static String getMills2S(long totalMills, long currtTime) {

        long yu_mills = totalMills - currtTime;
        long yu_s = yu_mills / 1000;

        long diaplay_m = yu_s / 60;
        long diaplay_s = yu_s % 60;
        String mm = "";
        String ss = "";
        if (diaplay_m < 10) {
            mm = "0" + diaplay_m;
        } else {
            mm = "" + diaplay_m;
        }
        if (diaplay_s < 10) {
            ss = "0" + diaplay_s;
        } else {
            ss = "" + diaplay_s;
        }
        return mm + ":" + ss;

    }

}
