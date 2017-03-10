package com.projects.jezinka.conaobiad;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CoNaObiadDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new CoNaObiadDbHelper(this);
        dbHelper.cleanData();
        dbHelper.initializeMealTable();

        List<String> preparedRows = DinnerListHelper.getPreparedRows(new Date(), dbHelper.getAllMeals());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, preparedRows);

        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
    }

    public void addNew(View view) {
        Intent intent = new Intent(this, AddDinnerActivity.class);
        startActivity(intent);
    }
}
