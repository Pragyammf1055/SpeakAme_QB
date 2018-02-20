package com.speakameqb.Classes;

import android.content.Context;
import android.util.Log;

import com.speakameqb.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by MAX on 20-Jan-17.
 */
public class TimeAgo {

    protected Context context;

    public TimeAgo(Context context) {
        this.context = context;
    }

    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    private static int getTimeDistanceInMinutes(long time) {
        long timeDistance = currentDate().getTime() - time;
        Log.d("getTimeDistance", currentDate().getTime() + ":::" + time + ">>" + timeDistance);
        return Math.round((Math.abs(timeDistance) / 1000) / 60);
    }

    public String timeAgo(Date date) {
        return getTimeAgo(date);
        // return timeAgo(date.getTime());
    }

    public String getTimeAgo(Date date) {

        if (date == null) {
            return "";
        }

        long time = date.getTime();

        Date curDate = currentDate();
        long now = curDate.getTime();
        if (time > now || time <= 0) {
            return "";
        }

        int dim = getTimeDistanceInMinutes(time);

        String timeAgo = null;

        if (dim == 0) {
            timeAgo = context.getResources().getString(R.string.date_util_term_less) + " " + context.getResources().getString(R.string.date_util_term_a) + " " + context.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim == 1) {
            return "1 " + context.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim >= 2 && dim <= 44) {
            timeAgo = dim + " " + context.getResources().getString(R.string.date_util_unit_minutes);
        } else if (dim >= 45 && dim <= 89) {
            timeAgo = context.getResources().getString(R.string.date_util_prefix_about) + " " + context.getResources().getString(R.string.date_util_term_an) + " " + context.getResources().getString(R.string.date_util_unit_hour);
        } else if (dim >= 90 && dim <= 1439) {
            timeAgo = context.getResources().getString(R.string.date_util_prefix_about) + " " + (Math.round(dim / 60)) + " " + context.getResources().getString(R.string.date_util_unit_hours);
        } else if (dim >= 1440 && dim <= 2519) {
            timeAgo = "1 " + context.getResources().getString(R.string.date_util_unit_day);
        } else if (dim >= 2520 && dim <= 43199) {
            timeAgo = (Math.round(dim / 1440)) + " " + context.getResources().getString(R.string.date_util_unit_days);
        } else if (dim >= 43200 && dim <= 86399) {
            timeAgo = context.getResources().getString(R.string.date_util_prefix_about) + " " + context.getResources().getString(R.string.date_util_term_a) + " " + context.getResources().getString(R.string.date_util_unit_month);
        } else if (dim >= 86400 && dim <= 525599) {
            timeAgo = (Math.round(dim / 43200)) + " " + context.getResources().getString(R.string.date_util_unit_months);
        } else if (dim >= 525600 && dim <= 655199) {
            timeAgo = context.getResources().getString(R.string.date_util_prefix_about) + " " + context.getResources().getString(R.string.date_util_term_a) + " " + context.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 655200 && dim <= 914399) {
            timeAgo = context.getResources().getString(R.string.date_util_prefix_over) + " " + context.getResources().getString(R.string.date_util_term_a) + " " + context.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 914400 && dim <= 1051199) {
            timeAgo = context.getResources().getString(R.string.date_util_prefix_almost) + " 2 " + context.getResources().getString(R.string.date_util_unit_years);
        } else {
            timeAgo = context.getResources().getString(R.string.date_util_prefix_about) + " " + (Math.round(dim / 525600)) + " " + context.getResources().getString(R.string.date_util_unit_years);
        }

        return timeAgo + " " + context.getResources().getString(R.string.date_util_suffix);
    }

   /* public String timeAgo(long millis) {

        Log.d("LastActivity",new Date().getTime() +"\n"+ millis );
        long diff = new Date().getTime() - millis;

        Resources r = context.getResources();

        String prefix = r.getString(R.string.time_ago_prefix);
        String suffix = r.getString(R.string.time_ago_suffix);

        double seconds = Math.abs(diff) / 1000;
        double minutes = seconds / 60;
        double hours = minutes / 60;
        double days = hours / 24;
        double years = days / 365;

        String words;

        if (seconds < 45) {
            words = r.getString(R.string.time_ago_seconds, Math.round(seconds));
        } else if (seconds < 90) {
            words = r.getString(R.string.time_ago_minute);
        } else if (minutes < 45) {
            words = r.getString(R.string.time_ago_minutes, Math.round(minutes));
        } else if (minutes < 90) {
            words = r.getString(R.string.time_ago_hour);
        } else if (hours < 24) {
            words = r.getString(R.string.time_ago_hours, Math.round(hours));
        } else if (hours < 42) {
            words = r.getString(R.string.time_ago_day);
        } else if (days < 30) {
            words = r.getString(R.string.time_ago_days, Math.round(days));
        } else if (days < 45) {
            words = r.getString(R.string.time_ago_month);
        } else if (days < 365) {
            words = r.getString(R.string.time_ago_months, Math.round(days / 30));
        } else if (years < 1.5) {
            words = r.getString(R.string.time_ago_year);
        } else {
            words = r.getString(R.string.time_ago_years, Math.round(years));
        }

        StringBuilder sb = new StringBuilder();

        if (prefix != null && prefix.length() > 0) {
            sb.append(prefix).append(" ");
        }

        sb.append(words);

        if (suffix != null && suffix.length() > 0) {
            sb.append(" ").append(suffix);
        }

        return sb.toString().trim();
    }*/
}
