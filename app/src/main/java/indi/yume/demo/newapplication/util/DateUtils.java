package indi.yume.demo.newapplication.util;

import android.text.TextUtils;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yume on 15/8/3.
 */
public class DateUtils {

    public static String dateStringToString(String date) {
        String[] ymds = date.split(" ")[0].split("-");
        String ymd = ymds[0] + "年" + ymds[1] + "月" + ymds[2] + "日";
        return ymd + " " + date.split(" ")[1];
    }

    public static String getStringPattern(String date, String pattern) {
        String[] ymds = date.split(" ")[0].split("-");
        if (TextUtils.equals(pattern, "yyyy")) {
            return ymds[0];
        } else if (TextUtils.equals(pattern, "mm")) {
            return ymds[1];
        } else {
            return ymds[2];
        }
    }

    public static String msToDate(String time) {
        long longTime = Long.valueOf(time);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date msDate = new Date(longTime);
        String date = format.format(msDate);
        return dateStringToString(date);
    }

    public static int getDatePattern(Date date, String pattern) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = format.format(date);
        return Integer.valueOf(getStringPattern(strDate, pattern));
    }

    public static long stringToMs(String strDate) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            strDate += ":59";
            Date date = format.parse(strDate);
            return date.getTime();
        } catch (ParseException e) {
            Log.d("DateUtils", e.getMessage());
            return 0;
        }
    }

    public static long dateToMs(Date date) {
        return date.getTime();
    }

}
