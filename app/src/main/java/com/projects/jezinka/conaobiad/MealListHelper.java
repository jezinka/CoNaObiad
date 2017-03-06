package com.projects.jezinka.conaobiad;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jezinka on 28.02.17.
 */
public class MealListHelper {

    static List<String> MEALS = Arrays.asList("bigos", "rosół", "zapiekanka", "pierogi", "pomidorowa", "pesto", "meksykański ryż czerwony");

    @NonNull
    protected ArrayList<String> getPreparedRows(Date todayDate) {
        ArrayList<String> preparedRows = new ArrayList<String>();

        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, new Locale("pl", "pl"));

        Calendar calendarInstance = Calendar.getInstance();
        calendarInstance.setTime(getSaturdayDate(todayDate));


        for (int i = 0; i < 7; i++) {
            Date date = calendarInstance.getTime();

            StringBuffer row = new StringBuffer();
            row.append(new SimpleDateFormat("EEEE").format(date));
            row.append(" - ");

            row.append(df.format(date));
            row.append("\n");
            row.append(MEALS.get(i));
            preparedRows.add(row.toString());

            calendarInstance.roll(Calendar.DATE, true);
        }
        return preparedRows;
    }

    @NonNull
    private Date getSaturdayDate(Date date) {

        if (date == null) {
            date = new Date();
        }

        Calendar calendarInstance = Calendar.getInstance();
        calendarInstance.setTime(date);

        int dayOfWeek = calendarInstance.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY) {
            return date;
        }

        calendarInstance.add(Calendar.DAY_OF_WEEK, -dayOfWeek);
        return calendarInstance.getTime();
    }
}
