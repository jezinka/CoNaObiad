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

    private static final List<String> MEALS = Arrays.asList("bigos", "rosół", "zapiekanka", "pierogi", "pomidorowa", "pesto", "meksykański ryż czerwony");

    @NonNull
    protected ArrayList<String> getPreparedRows() {
        ArrayList<String> preparedRows = new ArrayList<String>();

        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, new Locale("pl", "pl"));

        Calendar calendarInstance = getSaturdayDate(new Date());

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
    private Calendar getSaturdayDate(Date date) {
        Calendar calendarInstance = Calendar.getInstance();
        calendarInstance.setTime(date);

        int dayOfWeek = calendarInstance.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek != Calendar.SATURDAY) {
            calendarInstance.add(Calendar.DATE, -dayOfWeek);
        }

        return calendarInstance;
    }
}
