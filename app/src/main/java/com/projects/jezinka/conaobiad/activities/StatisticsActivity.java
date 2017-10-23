package com.projects.jezinka.conaobiad.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.projects.jezinka.conaobiad.R;
import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.models.tables.DinnerContract;
import com.projects.jezinka.conaobiad.models.tables.IngredientContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", new Locale("pl-pl"));

    public static final int DINNER_POSITION = 0;
    public static final int INGREDIENT_POSITION = 1;
    public static final String WHOLE_HISTORY_CLAUSE = " where 1 = 1";
    public static final int WHOLE_HISTORY_POSITION = 0;
    public static final int CURRENT_MONTH_POSITION = 1;
    public static final int CURRENT_YEAR_POSITION = 2;
    public static final int CUSTOM_DATES_POSITION = 3;
    CoNaObiadDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        dbHelper = new CoNaObiadDbHelper(this);

        Spinner spinner = (Spinner) findViewById(R.id.statistics_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String whereClause = getWhereClause();
                createChart(whereClause, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                createDinnerBarChart(WHOLE_HISTORY_CLAUSE);
            }
        });

        Spinner timeSpinner = (Spinner) findViewById(R.id.time_duration_spinner);
        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showHideCustomDates(position);
                prepareChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.statistics_toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_back_arrow_sketch);
    }

    private void showHideCustomDates(int position) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.custom_dates_layout);
        switch (position) {
            case WHOLE_HISTORY_POSITION:
            case CURRENT_MONTH_POSITION:
            case CURRENT_YEAR_POSITION:
            default:
                layout.setVisibility(View.GONE);
                break;
            case CUSTOM_DATES_POSITION:
                EditText minDate = (EditText) findViewById(R.id.min_date);
                minDate.setText(df.format(new Date()));

                EditText maxDate = (EditText) findViewById(R.id.max_date);
                maxDate.setText(df.format(new Date()));

                layout.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void createChart(String whereClause, int timeDurationPosition) {
        switch (timeDurationPosition) {
            case INGREDIENT_POSITION:
                createIngredientBarChart(whereClause);
                break;
            case DINNER_POSITION:
            default:
                createDinnerBarChart(whereClause);
                break;
        }
    }

    @NonNull
    private String getWhereClause() {
        String whereClause;
        Spinner timeSpinner = (Spinner) findViewById(R.id.time_duration_spinner);
        switch (timeSpinner.getSelectedItemPosition()) {
            default:
            case WHOLE_HISTORY_POSITION:
                whereClause = WHOLE_HISTORY_CLAUSE;
                break;
            case CURRENT_MONTH_POSITION:
                Calendar cMonth = Calendar.getInstance();
                cMonth.set(Calendar.DAY_OF_MONTH, 1);
                whereClause = " where " + DinnerContract.COLUMN_DATE + " > " + cMonth.getTime().getTime();
                break;
            case CURRENT_YEAR_POSITION:
                Calendar cYear = Calendar.getInstance();
                cYear.set(Calendar.DAY_OF_YEAR, 1);
                whereClause = " where " + DinnerContract.COLUMN_DATE + " > " + cYear.getTime().getTime();
                break;
            case CUSTOM_DATES_POSITION:
                long minDate = getTimeFromEditText(R.id.min_date);
                long maxDate = getTimeFromEditText(R.id.max_date);
                whereClause = " where " + DinnerContract.COLUMN_DATE + " between " + minDate + " and " + maxDate;
                break;
        }
        return whereClause;
    }

    private long getTimeFromEditText(int id) {
        EditText editText = (EditText) findViewById(id);
        String textValue = editText.getText().toString();
        Date date;
        try {
            date = df.parse(textValue);
        } catch (ParseException ex) {
            date = new Date();
        }
        return date.getTime();
    }

    private void createDinnerBarChart(String whereClause) {

        DinnerContract dinnerContract = new DinnerContract();

        LinkedHashMap<String, Long> mealData = dinnerContract.getDinnerStatistics(whereClause, dbHelper);
        HorizontalBarChart chart = createBarChart(mealData);
        chart.invalidate();
    }

    private void createIngredientBarChart(String whereClause) {

        IngredientContract ingredientContract = new IngredientContract();

        LinkedHashMap<String, Long> mealData = ingredientContract.getIngredientsStatistics(whereClause, dbHelper);
        HorizontalBarChart chart = createBarChart(mealData);
        chart.invalidate();
    }

    private HorizontalBarChart createBarChart(LinkedHashMap<String, Long> mealData) {
        List<BarEntry> entries = new ArrayList<>();

        int i = 0;
        for (Map.Entry<String, Long> entry : mealData.entrySet()) {
            String mealName = entry.getKey();
            Long quantity = entry.getValue();
            entries.add(new BarEntry(i, quantity, mealName));
            i++;
        }

        final BarDataSet set = new BarDataSet(entries, "");
        set.setDrawValues(false);

        BarData data = new BarData(set);
        data.setBarWidth(0.2f);

        HorizontalBarChart chart = (HorizontalBarChart) findViewById(R.id.chart);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(i);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                BarEntry entryForXValue = set.getEntryForXValue(value, 0.1f);
                if (entryForXValue != null) {
                    return (String) entryForXValue.getData();
                }
                return "";
            }
        });

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setGranularity(1f);
        yAxis.setStartAtZero(true);

//        Bitmap starBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_square_sketch);
//        chart.setRenderer(new ImageBarChartRenderer(chart, chart.getAnimator(), chart.getViewPortHandler(), starBitmap));

        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.setData(data);
        chart.setVisibleXRange(0, i);
        return chart;
    }

    public void showDatePickerDialog(final View v) {
        Calendar cal = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendarInstance = Calendar.getInstance();
                        calendarInstance.set(year, monthOfYear, dayOfMonth);
                        ((EditText) v).setText(df.format(calendarInstance.getTime()));
                        prepareChart();
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

        datePickerDialog.show();
    }

    private void prepareChart() {
        String whereClause = getWhereClause();
        int timeDurationPosition = ((Spinner) findViewById(R.id.statistics_spinner)).getSelectedItemPosition();
        createChart(whereClause, timeDurationPosition);
    }
}
