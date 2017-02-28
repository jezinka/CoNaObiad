package com.projects.jezinka.conaobiad;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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

        Calendar date = getSaturdayDate();

        for (int i = 0; i < 7; i++) {
            StringBuffer row = new StringBuffer();
            row.append(new SimpleDateFormat("EEEE").format(date.getTime()));
            row.append(" - ");

            row.append(df.format(date.getTime()));
            row.append("\n");
            row.append(MEALS.get(i));
            preparedRows.add(row.toString());

            date.add(Calendar.DATE, 1);
        }
        return preparedRows;
    }

    @NonNull
    private Calendar getSaturdayDate() {
        Calendar calendarInstance = Calendar.getInstance();
        int todaysDay = calendarInstance.get((Calendar.DAY_OF_WEEK));
        if (todaysDay == Calendar.SATURDAY) {
            return calendarInstance;
        }

        calendarInstance.add(Calendar.DATE, -todaysDay);
        return calendarInstance;

    }
}
