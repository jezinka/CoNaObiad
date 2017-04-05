package com.projects.jezinka.conaobiad;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.projects.jezinka.conaobiad.model.Dinner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DinnerListHelper {

    @NonNull
    protected static ArrayList<String> getPreparedRows(List<Dinner> dinners) {
        ArrayList<String> preparedRows = new ArrayList<>();

        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, new Locale("pl", "pl"));

        Calendar calendarInstance = Calendar.getInstance();
        calendarInstance.setTime(TimeUtils.getSaturdayDate(new Date()));

        for (int i = 0; i < TimeUtils.DAYS_IN_PLANNER; i++) {
            Date date = calendarInstance.getTime();

            StringBuffer row = new StringBuffer();
            row.append(new SimpleDateFormat("EEEE").format(date));
            row.append(" - ");
            row.append(df.format(date));
            row.append("\n");

            List<String> dinnersName = new ArrayList<>();
            for (Dinner dinner : dinners) {
                if (dinner.getDate().getTime() == date.getTime()) {
                    dinnersName.add(dinner.getMeal().getName());
                }
            }
            row.append(TextUtils.join(",", dinnersName.toArray(new String[dinnersName.size()])));
            preparedRows.add(row.toString());

            calendarInstance.roll(Calendar.DATE, true);
        }
        return preparedRows;
    }
}
