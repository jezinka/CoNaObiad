package com.projects.jezinka.conaobiad;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MealListHelper mealListHelper = new MealListHelper();
        ArrayList<String> preparedRows = mealListHelper.getPreparedRows();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, preparedRows);

        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
    }

}
