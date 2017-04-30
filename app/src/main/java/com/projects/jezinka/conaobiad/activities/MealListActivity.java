package com.projects.jezinka.conaobiad.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.projects.jezinka.conaobiad.R;
import com.projects.jezinka.conaobiad.adapters.IngredientListAdapter;
import com.projects.jezinka.conaobiad.adapters.MealListAdapter;
import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.models.Ingredient;
import com.projects.jezinka.conaobiad.models.Meal;
import com.projects.jezinka.conaobiad.models.tableDefinitions.MealContract;
import com.projects.jezinka.conaobiad.models.tableDefinitions.MealIngredientContract;

import java.util.ArrayList;
import java.util.List;

public class MealListActivity extends AppCompatActivity {

    MealListAdapter adapter;
    private CoNaObiadDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_list);

        final MealContract mealContract = new MealContract();
        final MealIngredientContract mealIngredientContract = new MealIngredientContract();
        dbHelper = new CoNaObiadDbHelper(this);

        adapter = new MealListAdapter(this, android.R.layout.simple_list_item_multiple_choice, mealContract.getAllMealsArray(dbHelper));

        final ListView listView = (ListView) findViewById(R.id.meal_list_view);
        listView.setAdapter(adapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        final FloatingActionButton deleteButton = (FloatingActionButton) findViewById(R.id.delete_meal_button);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listView.getCheckedItemCount() == 0) {
                    deleteButton.setVisibility(View.INVISIBLE);
                } else {
                    deleteButton.setVisibility(View.VISIBLE);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                final Meal meal = adapter.getItem(position);
                AlertDialog.Builder childContextMenuBuilder = new AlertDialog.Builder(view.getContext());
                childContextMenuBuilder.setItems(R.array.meal_actions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                final AlertDialog.Builder builder = getAlertBuilder(view, mealContract, meal);
                                builder.show();
                                break;
                            case 1:
                                showIngredientPickerDialog(view, meal);
                                break;
                            case 2:
                                List<String> result = mealIngredientContract.getIngredientsForMeal(meal, dbHelper);
                                AlertDialog.Builder ingredientsBuilder = new AlertDialog.Builder(view.getContext());

                                ingredientsBuilder.setTitle(R.string.ingredient_list)
                                        .setItems(result.toArray(new String[result.size()]), null)
                                        .show();
                                break;

                        }
                    }
                });
                childContextMenuBuilder.show();
                return true;
            }
        });

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.add_meal_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = getAlertBuilder(v, mealContract, null);
                builder.show();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
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
                deleteButton.setVisibility(View.INVISIBLE);
                mealContract.delete(mealIds.toArray(new Long[mealIds.size()]), dbHelper);
                adapter.updateResults(mealContract.getAllMealsArray(dbHelper));
            }
        });

        Toolbar myToolbar = (Toolbar) findViewById(R.id.meal_list_toolbar);
        myToolbar.setTitle(R.string.meal_list);
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
                String mealName = input.getText().toString().trim();
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

    private void showIngredientPickerDialog(View v, final Meal meal) {
        View addIngredientView = getLayoutInflater().inflate(R.layout.filterable_list_view, new LinearLayout(v.getContext()), false);

        final MealIngredientContract mealIngredientContract = new MealIngredientContract();

        final IngredientListAdapter ingredientListAdapter = new IngredientListAdapter(v.getContext(),
                R.layout.multicheck_list,
                mealIngredientContract.getIngredientsWithMeal(meal, dbHelper));

        final ListView listView = (ListView) addIngredientView.findViewById(R.id.filterable_list_view);
        listView.setAdapter(ingredientListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ingredientListAdapter.getItem(position).setChecked(!ingredientListAdapter.getItem(position).isChecked());
                ingredientListAdapter.notifyDataSetChanged();
            }
        });

        AlertDialog.Builder addIngredientBuilder = new AlertDialog.Builder(v.getContext());

        addIngredientBuilder.setView(addIngredientView)
                .setTitle(R.string.put_ingredient_name)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mealIngredientContract.deleteForMeal(dbHelper, meal.getId());
                        for (int i = 0; i < ingredientListAdapter.getCount(); i++) {
                            Ingredient item = ingredientListAdapter.getItem(i);
                            if (item.isChecked()) {
                                long ingredientId = item.getId();
                                mealIngredientContract.insert(dbHelper, meal.getId(), ingredientId);
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null);

        AlertDialog alertDialog = addIngredientBuilder.create();


        EditText filterEditText = (EditText) addIngredientView.findViewById(R.id.name_filter);
        filterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Filter filter = ingredientListAdapter.getFilter();
                filter.filter(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        alertDialog.show();
    }
}
