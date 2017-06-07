package com.projects.jezinka.conaobiad.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.jezinka.conaobiad.R;
import com.projects.jezinka.conaobiad.adapters.DinnerAdapter;
import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.dialogs.DinnerDialogFragment;
import com.projects.jezinka.conaobiad.models.Dinner;
import com.projects.jezinka.conaobiad.models.Meal;
import com.projects.jezinka.conaobiad.models.tableDefinitions.DinnerContract;
import com.projects.jezinka.conaobiad.models.tableDefinitions.MealContract;
import com.projects.jezinka.conaobiad.models.tableDefinitions.MealIngredientContract;
import com.projects.jezinka.conaobiad.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CoNaObiadDbHelper dbHelper;
    private MealContract mealContract;
    private DinnerContract dinnerContract;
    private DinnerAdapter dinnerAdapter;

    private static int firstDayOfWeek;
    private static int planLength;

    static boolean preferenceChanged = false;

    public static int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public static int getPlanLength() {
        return planLength;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new CoNaObiadDbHelper(this);
        mealContract = new MealContract();
        dinnerContract = new DinnerContract();

        if (!mealContract.isAnyMealSaved(dbHelper)) {
            showEmptyMealListMessage(this);
        }

        planLength = SettingsActivity.getPlanLength(this);
        firstDayOfWeek = SettingsActivity.getFirstDay(this);

        dinnerAdapter = new DinnerAdapter(this);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(dinnerAdapter);

        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder childContextMenuBuilder = new AlertDialog.Builder(view.getContext());
                childContextMenuBuilder.setItems(R.array.dinner_child_actions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Date date = dinnerAdapter.getItem(position);
                        Dinner dinner = dinnerAdapter.getDinner(date);
                        switch (which) {
                            case 0:
                                showNewDinnerDialog(date);
                                break;
                            case 1:
                                showRecipeDialog(dinner);
                                break;
                            case 2:
                                dinnerContract.delete(date.getTime(), DinnerContract.columnDate, dbHelper);
                                dinnerAdapter.updateResults();
                                break;
                            case 3:
                                showNewDinnerDialog(date, dinner);
                                break;
                        }
                    }
                });
                childContextMenuBuilder.show();
                return true;
            }
        });

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(myToolbar);
    }

    public void showRecipeDialog(Dinner dinner) {
        TextView recipeText = new TextView(this);
        if (dinner != null) {
            recipeText.setText(dinner.getRecipe());
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.recipe)
                .setView(recipeText)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (preferenceChanged) {
            preferenceChanged = false;
            recreate();
        }
    }

    private void showEmptyMealListMessage(final Context context) {
        new AlertDialog.Builder(context)
                .setMessage(R.string.empty_meal_list_message)
                .setCancelable(true)
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(context, MealListActivity.class);
                        startActivity(intent);
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.meal_list_item:
                Intent intent = new Intent(this, MealListActivity.class);
                startActivity(intent);
                return true;

            case R.id.ingredient_list_item:
                Intent ingredientsIntent = new Intent(this, IngredientListActivity.class);
                startActivity(ingredientsIntent);
                return true;

            case R.id.settings_item:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;

            case R.id.application_info_item:
                showInfoDialog();
                return true;

            case R.id.shopping_list_item:
                showShoppingListDialog();
                return true;

            case R.id.fill_list_item:
                fillList();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void showInfoDialog() {
        final AlertDialog d = new AlertDialog.Builder(this)
                .setPositiveButton(android.R.string.ok, null)
                .setMessage(getDSPMessage())
                .create();
        d.show();

        ((TextView) d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    @SuppressWarnings("deprecation")
    private Spanned getDSPMessage() {
        String html = "Aplikacja została stworzona na potrzeby konkursu <a href=\"http://dajsiepoznac.pl\">DSP 2017</a>";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    private void showShoppingListDialog() {

        Dinner[] dinners = dinnerContract.getDinners(dbHelper);

        if (dinners.length == 0) {
            Toast.makeText(this, R.string.empty_dinner_list_message, Toast.LENGTH_LONG).show();
            return;
        }

        List<Meal> meals = new ArrayList<>();

        for (Dinner dinner : dinners) {
            Meal meal = dinner.getMeal();
            meals.add(meal);
        }

        MealIngredientContract mealIngredientContract = new MealIngredientContract();
        new AlertDialog.Builder(this)
                .setTitle(R.string.shopping_list)
                .setPositiveButton(android.R.string.ok, null)
                .setMessage(mealIngredientContract.getShoppingList(meals, dbHelper))
                .show();
    }

    public void showIngredients(final Dinner dinner) {
        MealIngredientContract mealIngredientContract = new MealIngredientContract();

        List<Meal> meals = new ArrayList<>();
        meals.add(dinner.getMeal());

        new AlertDialog.Builder(this)
                .setTitle(R.string.shopping_list)
                .setPositiveButton(android.R.string.ok, null)
                .setMessage(mealIngredientContract.getShoppingList(meals, dbHelper))
                .show();
    }

    private void fillList() {

        Calendar calendarInstance = Calendar.getInstance();
        calendarInstance.setTime(TimeUtils.getWeekStartDate(new Date()));

        //TODO: wstaw w puste

        List<Meal> meals = mealContract.getRandomMeals(dbHelper, getPlanLength());

        for (Meal meal : meals) {
            dinnerContract.insert(dbHelper, meal.getId(), calendarInstance.getTime());
            calendarInstance.add(Calendar.DATE, 1);
        }

        dinnerAdapter.updateResults();
    }

    public void showNewDinnerDialog(View view) {
        showNewDinnerDialog(new Date(), null);
    }

    public void showNewDinnerDialog(Date date) {
        showNewDinnerDialog(date, null);
    }

    private void showNewDinnerDialog(Date date, Dinner dinner) {
        DialogFragment newFragment = DinnerDialogFragment.newInstance(date.getTime(), dinner);
        newFragment.show(getSupportFragmentManager(), "DinnerDialogFragment");
    }

    public CoNaObiadDbHelper getDbHelper() {
        return this.dbHelper;
    }

    public DinnerAdapter getAdapter() {
        return this.dinnerAdapter;
    }
}
