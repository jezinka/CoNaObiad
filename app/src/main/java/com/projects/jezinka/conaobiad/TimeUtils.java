package com.projects.jezinka.conaobiad;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    public static final int DAYS_IN_PLANNER = 7;
    public final static int SEVEN_DAYS_IN_MILLISECONDS = 1000 * 60 * 60 * 24 * DAYS_IN_PLANNER;

    @NonNull
    public static Date getSaturdayDate(Date date) {

        if (date == null) {
            date = new Date();
        }

        Calendar calendarInstance = Calendar.getInstance();
        calendarInstance.setTime(date);

        calendarInstance.set(Calendar.HOUR_OF_DAY, 0);
        calendarInstance.set(Calendar.MINUTE, 0);
        calendarInstance.set(Calendar.SECOND, 0);
        calendarInstance.set(Calendar.MILLISECOND, 0);

        int dayOfWeek = calendarInstance.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY) {
            return date;
        }

        calendarInstance.add(Calendar.DAY_OF_WEEK, -dayOfWeek);
        return calendarInstance.getTime();
    }
}
