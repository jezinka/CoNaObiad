package com.projects.jezinka.conaobiad;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.projects.jezinka.conaobiad.model.MealContract;

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

        ListView listView = (ListView) findViewById(R.id.meal_list_view);
        listView.setAdapter(adapter);

        Button button = (Button) findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(R.string.putMealName);

                final EditText input = new EditText(v.getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
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

        Toolbar myToolbar = (Toolbar) findViewById(R.id.meal_list_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
