package com.projects.jezinka.conaobiad;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.projects.jezinka.conaobiad.model.MealContract;

import java.util.ArrayList;

public class MealListActivity extends AppCompatActivity {

    MealListAdapter adapter;
    private CoNaObiadDbHelper dbHelper;
    private String mealName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_list);

        final MealContract mealContract = new MealContract();
        dbHelper = new CoNaObiadDbHelper(this);

        adapter = new MealListAdapter(this, mealContract.getAllMeals(dbHelper));

        final ListView listView = (ListView) findViewById(R.id.meal_list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Button deleteMealButton = (Button) findViewById(R.id.delete_meal_button);
                if (listView.getCheckedItemCount() == 0) {
                    deleteMealButton.setVisibility(View.INVISIBLE);
                } else {
                    deleteMealButton.setVisibility(View.VISIBLE);
                }
            }
        });

        Button button = (Button) findViewById(R.id.add_meal_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(R.string.put_meal_name);

                final EditText input = new EditText(v.getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mealName = input.getText().toString();
                        MealContract meal = new MealContract();
                        meal.insertMeal(builder.getContext(), mealName);
                        adapter.updateResults(mealContract.getAllMeals(dbHelper));
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        final Button deleteMealButton = (Button) findViewById(R.id.delete_meal_button);
        deleteMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray positions = listView.getCheckedItemPositions();
                ArrayList mealNames = new ArrayList();
                for (int i = 0; i < listView.getCount(); i++) {
                    if (positions.get(i)) {
                        mealNames.add(listView.getItemAtPosition(i));
                    }
                }
                listView.clearChoices();
                deleteMealButton.setVisibility(View.INVISIBLE);
                mealContract.deleteMeals(mealNames, dbHelper);
                adapter.updateResults(mealContract.getAllMeals(dbHelper));
            }
        });

        Toolbar myToolbar = (Toolbar) findViewById(R.id.meal_list_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
