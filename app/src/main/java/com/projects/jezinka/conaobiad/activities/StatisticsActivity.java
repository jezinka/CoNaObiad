package com.projects.jezinka.conaobiad.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    CoNaObiadDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        dbHelper = new CoNaObiadDbHelper(this);
        DinnerContract dinnerContract = new DinnerContract();

        LinkedHashMap<String, Long> mealData = dinnerContract.getDinnerStatistics(dbHelper);
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
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return (String) set.getEntryForXValue(value, 0.1f).getData();
            }
        });

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setGranularity(1f);

        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.setData(data);

        Toolbar toolbar = (Toolbar) findViewById(R.id.statistics_toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_back_arrow_sketch);
    }
}
