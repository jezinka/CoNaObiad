package com.projects.jezinka.conaobiad.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.projects.jezinka.conaobiad.R;
import com.projects.jezinka.conaobiad.adapters.MealListAdapter;
import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.models.Meal;
import com.projects.jezinka.conaobiad.models.tableDefinitions.MealContract;

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

        adapter = new MealListAdapter(this, android.R.layout.simple_list_item_multiple_choice, mealContract.getAllMealsArray(dbHelper));

        final ListView listView = (ListView) findViewById(R.id.meal_list_view);
        listView.setAdapter(adapter);

        final FloatingActionButton deleteMealButton = (FloatingActionButton) findViewById(R.id.delete_meal_button);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listView.getCheckedItemCount() == 0) {
                    deleteMealButton.setVisibility(View.INVISIBLE);
                } else {
                    deleteMealButton.setVisibility(View.VISIBLE);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Meal meal = adapter.getItem(position);
                final AlertDialog.Builder builder = getAlertBuilder(view, mealContract, meal);
                builder.show();
                return true;
            }
        });

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.add_meal_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = getAlertBuilder(v, mealContract, null);
                builder.show();
            }
        });

        deleteMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray positions = listView.getCheckedItemPositions();
                ArrayList<Long> mealIds = new ArrayList<Long>();
                for (int i = 0; i < listView.getCount(); i++) {
                    if (positions.get(i)) {
                        mealIds.add(adapter.getItemId(i));
                    }
                }
                listView.clearChoices();
                deleteMealButton.setVisibility(View.INVISIBLE);
                mealContract.delete(mealIds.toArray(new Long[mealIds.size()]), dbHelper);
                adapter.updateResults(mealContract.getAllMealsArray(dbHelper));
            }
        });

        Toolbar myToolbar = (Toolbar) findViewById(R.id.meal_list_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private AlertDialog.Builder getAlertBuilder(View v, final MealContract mealContract, final Meal meal) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle(R.string.put_meal_name);

        final EditText input = new EditText(v.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        if (meal != null) {
            input.setText(meal.getName());
        }
        builder.setView(input);

        builder.setPositiveButton(meal == null ? R.string.add : R.string.edit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mealName = input.getText().toString().trim();
                if (meal != null) {
                    mealContract.update(dbHelper, mealName, meal);
                } else {
                    mealContract.insert(dbHelper, mealName);
                }
                adapter.updateResults(mealContract.getAllMealsArray(dbHelper));
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder;
    }
}
