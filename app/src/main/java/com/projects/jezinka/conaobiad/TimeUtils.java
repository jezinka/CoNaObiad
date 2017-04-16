package com.projects.jezinka.conaobiad;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    @NonNull
    public static Date getSaturdayDate(Date date, Context context) {

        if (date == null) {
            date = new Date();
        }

        Calendar calendarInstance = Calendar.getInstance();
        calendarInstance.setTime(date);

        clearTime(calendarInstance);

        int dayOfWeek = calendarInstance.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == getFirstDay(context)) {
            return calendarInstance.getTime();
        }

        calendarInstance.add(Calendar.DAY_OF_WEEK, -dayOfWeek);
        return calendarInstance.getTime();
    }

    private static void clearTime(Calendar calendarInstance) {
        calendarInstance.set(Calendar.HOUR_OF_DAY, 0);
        calendarInstance.set(Calendar.MINUTE, 0);
        calendarInstance.set(Calendar.SECOND, 0);
        calendarInstance.set(Calendar.MILLISECOND, 0);
    }

    public static int getTimeDeltaMilliseconds(Context context) {
        return 1000 * 60 * 60 * 24 * getDaysInPlanner(context);
    }

    public static int getDaysInPlanner(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String daysNoPrefs = sharedPref.getString(SettingsActivity.PREFS_DAYS_NO, "7");
        return Integer.parseInt(daysNoPrefs);
    }

    private static int getFirstDay(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String firstDayPrefs = sharedPref.getString(SettingsActivity.PREFS_FIRST_DAY, "7");
        return Integer.parseInt(firstDayPrefs);
    }
}
