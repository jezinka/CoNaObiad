package com.projects.jezinka.conaobiad;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    @NonNull
    public static Date getWeekStartDate(Date date) {

        if (date == null) {
            date = new Date();
        }

        Calendar calendarInstance = Calendar.getInstance();
        calendarInstance.setTime(date);

        clearTime(calendarInstance);

        int dayOfWeek = calendarInstance.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == MainActivity.firstDayOfWeek) {
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

    public static int getTimeDeltaMilliseconds() {
        return 1000 * 60 * 60 * 24 * MainActivity.planLength;
    }
}
