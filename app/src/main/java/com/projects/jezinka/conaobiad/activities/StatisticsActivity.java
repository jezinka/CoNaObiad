package com.projects.jezinka.conaobiad.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
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
import com.projects.jezinka.conaobiad.models.tableDefinitions.DinnerContract;
import com.projects.jezinka.conaobiad.models.tableDefinitions.IngredientContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    static final SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", new Locale("pl-pl"));

    public static final int DINNER_POSITION = 0;
    public static final int INGREDIENT_POSITION = 1;
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
                switch (position) {
                    case INGREDIENT_POSITION:
                        createIngredientBarChart();
                        break;
                    case DINNER_POSITION:
                    default:
                        createDinnerBarChart();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                createDinnerBarChart();
            }
        });

        Spinner timeSpinner = (Spinner) findViewById(R.id.time_duration_spinner);
        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout layout = (LinearLayout) findViewById(R.id.custom_dates_layout);
                switch (position) {
                    case 0:
                    case 1:
                        layout.setVisibility(View.GONE);
                        break;
                    case 2:
                        EditText minDate = (EditText) findViewById(R.id.min_date);
                        minDate.setText(df.format(new Date()));

                        EditText maxDate = (EditText) findViewById(R.id.max_date);
                        maxDate.setText(df.format(new Date()));

                        layout.setVisibility(View.VISIBLE);
                        break;
                }
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

    private void createDinnerBarChart() {

        DinnerContract dinnerContract = new DinnerContract();

        LinkedHashMap<String, Long> mealData = dinnerContract.getDinnerStatistics(dbHelper);
        createBarChart(mealData);
    }

    private void createIngredientBarChart() {

        IngredientContract ingredientContract = new IngredientContract();

        LinkedHashMap<String, Long> mealData = ingredientContract.getIngredientsStatistics(dbHelper);
        createBarChart(mealData);
    }

    private void createBarChart(LinkedHashMap<String, Long> mealData) {
        List<BarEntry> entries = new ArrayList<>();

        int i = 0;
        for (Map.Entry<String, Long> entry : mealData.entrySet()) {
            String mealName = entry.getKey();
            Long quantity = entry.getValue();
            entries.add(new BarEntry(i, quantity, mealName));
            i++;
        }

        final BarDataSet set = new BarDataSet(entries, "");
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
                return (String) set.getEntryForXValue(value, 0.1f).getData();
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
        chart.invalidate();
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
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

        datePickerDialog.show();
    }
}
