package com.speakameqb.Xmpp;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by MAX on 21-Sep-16.
 */
public class CommonMethods {
    //private static DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd");
    private static DateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
    //2007-01-01 10:00:00
    // private static DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getCurrentTime() {

        Date today = Calendar.getInstance().getTime();
        return timeFormat.format(today);
    }

    public static String getCurrentDate() {

        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }

    public static String getCurrentDateNewFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = Calendar.getInstance().getTime();
        return simpleDateFormat.format(today);
    }

    public static String getCalculatedDate(String date, String dateFormat, int days) {
        Log.e("Comman_Methods", "getCalculatedDate : " + days);
        DateFormat s_dateFormat = new SimpleDateFormat(dateFormat);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -(days));
        String previous_date = null;
        try {
            previous_date = s_dateFormat.format(cal.getTime());
            Log.e("Comman_Methods", "getCalculatedDate : " + previous_date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return previous_date;
    }
}
