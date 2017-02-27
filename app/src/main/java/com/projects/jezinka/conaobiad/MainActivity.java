package com.projects.jezinka.conaobiad;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final List<String> MEALS = Arrays.asList("bigos", "rosół", "zapiekanka", "pierogi", "pomidorowa", "pesto", "meksykański ryż czerwony");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<String> preparedRows = getPreparedRows();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, preparedRows);

        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
    }

    @NonNull
    private ArrayList<String> getPreparedRows() {
        ArrayList<String> preparedRows = new ArrayList<String>();
        ArrayList<String> weekdays = getListOfWeekdays();


        Calendar saturdayDate = getSaturdayDate();

        for (int i = 0; i < weekdays.size(); i++) {
            StringBuffer row = new StringBuffer();
            row.append(weekdays.get(i));
            row.append(" - ");
            saturdayDate.add(Calendar.DATE, 1);
            row.append(saturdayDate.getTime().toString());
            row.append("\n");
            row.append(MEALS.get(i));

            preparedRows.add(row.toString());
        }
        return preparedRows;
    }

    @NonNull
    private Calendar getSaturdayDate() {
        Calendar calendarInstance = Calendar.getInstance();
        int todaysday = calendarInstance.get((Calendar.DAY_OF_WEEK));
        if (todaysday == Calendar.SATURDAY) {
            return calendarInstance;
        }

        calendarInstance.add(Calendar.DATE, -todaysday-1);
        return calendarInstance;

    }

    @NonNull
    private ArrayList<String> getListOfWeekdays() {
        ArrayList<String> weekdays = new ArrayList<String>();
        for (int i = 1; i <= 7; i++) {
            weekdays.add(new DateFormatSymbols().getWeekdays()[i]);
        }

        Collections.rotate(weekdays, 1);
        return weekdays;
    }
}
