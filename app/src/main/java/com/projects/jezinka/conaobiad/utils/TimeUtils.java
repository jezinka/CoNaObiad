package com.projects.jezinka.conaobiad.utils;

import android.support.annotation.NonNull;

import com.projects.jezinka.conaobiad.activities.MainActivity;

import org.joda.time.DateTime;

public class TimeUtils {

    private TimeUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static DateTime getWeekStartDate() {
        return getWeekStartDate(new DateTime());
    }

    @NonNull
    public static DateTime getWeekStartDate(DateTime date) {

        date = clearTime(date);

        int dayOfWeek = date.getDayOfWeek();

        if (dayOfWeek == MainActivity.getFirstDayOfWeek()) {
            return date;
        }

        while (date.getDayOfWeek() != MainActivity.getFirstDayOfWeek()) {
            date = date.minusDays(1);
        }

        return date;
    }

    private static DateTime clearTime(DateTime date) {
        return date.withTimeAtStartOfDay();
    }

    public static int getTimeDeltaMilliseconds() {
        return 1000 * 60 * 60 * 24 * MainActivity.getPlanLength();
    }
}
