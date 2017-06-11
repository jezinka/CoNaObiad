package com.projects.jezinka.conaobiad.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.projects.jezinka.conaobiad.R;
import com.projects.jezinka.conaobiad.adapters.MealListAdapter;
import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.dialogs.IngredientPickerDialogFragment;
import com.projects.jezinka.conaobiad.dialogs.MealDialogFragment;
import com.projects.jezinka.conaobiad.models.Meal;
import com.projects.jezinka.conaobiad.models.tableDefinitions.MealContract;
import com.projects.jezinka.conaobiad.models.tableDefinitions.MealIngredientContract;

import java.util.ArrayList;
import java.util.List;

public class MealListActivity extends AppCompatActivity implements MealDialogFragment.MealDialogListener {

    private MealListAdapter adapter;
    private CoNaObiadDbHelper dbHelper;
    private MealContract mealContract;
    private MealIngredientContract mealIngredientContract;
    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_list);

        mealContract = new MealContract();
        mealIngredientContract = new MealIngredientContract();

        dbHelper = new CoNaObiadDbHelper(this);

        adapter = new MealListAdapter(this, R.layout.multicheck_list, mealContract.getAllMealsArray(dbHelper));

        final ListView listView = (ListView) findViewById(R.id.meal_list_view);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                toggleCheckboxesAndToolbar();
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                int viewId = view.getId();
                Meal meal = adapter.getItem(position);

                switch (viewId) {
                    case R.id.text1:
                        showContextMenu(meal);
                        break;
                    case R.id.checkBox:
                        if (meal != null) {
                            meal.setChecked(!meal.isChecked());
                            adapter.notifyDataSetChanged();
                        }
                        break;
                }
            }
        });

        myToolbar = (Toolbar) findViewById(R.id.meal_list_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_back_arrow_sketch);
    }

    private void showContextMenu(final Meal meal) {

        final AlertDialog.Builder contextMenu = new AlertDialog.Builder(this);
        contextMenu.setItems(R.array.meal_actions, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                long mealId = meal != null ? meal.getId() : -1;
                String mealName = meal != null ? meal.getName() : "";

                switch (which) {
                    case 0:
                        showMealDialog(mealId, mealName);
                        break;
                    case 1:
                        showIngredientPickerDialog(mealId);
                        break;
                    case 2:
                        List<String> result = mealIngredientContract.getIngredientsForMeal(mealId, dbHelper);
                        new AlertDialog.Builder(contextMenu.getContext())
                                .setTitle(R.string.ingredient_list)
                                .setItems(result.toArray(new String[result.size()]), null)
                                .show();
                        break;
                    case 3:
                        showAddRecipeDialog(meal);
                        break;
                }
            }
        });
        contextMenu.show();
    }

    private void showAddRecipeDialog(final Meal meal) {
        View view = getLayoutInflater().inflate(R.layout.multiline_dialog, null);

        final EditText recipeText = (EditText) view.findViewById(R.id.multiline_edit_text);
        recipeText.setText(meal.getRecipe());

        new AlertDialog.Builder(this)
                .setTitle(R.string.recipe)
                .setView(view)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mealContract.addRecipe(dbHelper, recipeText.getText().toString(), meal.getId());
                        adapter.updateResults(mealContract.getAllMealsArray(dbHelper));
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void toggleCheckboxesAndToolbar() {
        int colorId = adapter.showCheckboxes ? R.color.colorPrimary : android.R.color.darker_gray;
        myToolbar.setBackgroundColor(ContextCompat.getColor(this, colorId));

        MenuItem deleteMenuButton = myToolbar.getMenu().findItem(R.id.delete_menu_button);
        deleteMenuButton.setVisible(!deleteMenuButton.isVisible());

        adapter.showCheckboxes = !adapter.showCheckboxes;
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_delete_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_menu_button:
                List<Long> mealIds = new ArrayList<>();
                for (int i = 0; i < adapter.getCount(); i++) {
                    Meal meal = adapter.getItem(i);
                    if (meal != null && meal.isChecked()) {
                        mealIds.add(meal.getId());
                    }
                }
                toggleCheckboxesAndToolbar();
                mealContract.delete(mealIds.toArray(new Long[mealIds.size()]), dbHelper);
                adapter.updateResults(mealContract.getAllMealsArray(dbHelper));
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void showMealDialog(long mealId, String mealName) {
        DialogFragment newFragment = MealDialogFragment.newInstance(mealId, mealName);
        newFragment.show(getSupportFragmentManager(), "MealDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment) {

        EditText input = (EditText) dialogFragment.getDialog().findViewById(R.id.meal_name);
        String mealName = input.getText().toString().trim();
        long mealId = dialogFragment.getArguments().getLong("mealId", -1);

        if (mealId != -1) {
            mealContract.update(dbHelper, mealName, mealId);
        } else {
            mealContract.insert(dbHelper, mealName);
        }
        adapter.updateResults(mealContract.getAllMealsArray(dbHelper));
    }

    public void showIngredientPickerDialog(long mealId) {
        DialogFragment newFragment;

        newFragment = IngredientPickerDialogFragment.newInstance(mealId);
        newFragment.show(getSupportFragmentManager(), "IngredientPickerDialogFragment");
    }

    public void addMealButtonClick(View view) {
        DialogFragment newFragment = MealDialogFragment.newInstance();
        newFragment.show(getSupportFragmentManager(), "MealDialogFragment");
    }

    public CoNaObiadDbHelper getDbHelper() {
        return dbHelper;
    }
}
