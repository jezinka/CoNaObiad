package com.projects.jezinka.conaobiad.activities;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.projects.jezinka.conaobiad.R;
import com.projects.jezinka.conaobiad.adapters.IngredientListAdapter;
import com.projects.jezinka.conaobiad.adapters.MealListAdapter;
import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.models.Ingredient;
import com.projects.jezinka.conaobiad.models.Meal;
import com.projects.jezinka.conaobiad.models.tableDefinitions.IngredientContract;
import com.projects.jezinka.conaobiad.models.tableDefinitions.MealContract;
import com.projects.jezinka.conaobiad.models.tableDefinitions.MealIngredientContract;

import java.util.ArrayList;
import java.util.List;

public class MealListActivity extends AppCompatActivity {

    private static final int DRAWABLE_RIGHT = 2;

    private MealListAdapter adapter;
    private CoNaObiadDbHelper dbHelper;
    private MealContract mealContract;
    private MealIngredientContract mealIngredientContract;
    private IngredientContract ingredientContract;
    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_list);

        mealContract = new MealContract();
        mealIngredientContract = new MealIngredientContract();
        ingredientContract = new IngredientContract();

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
                        AlertDialog.Builder childContextMenuBuilder = getBuilder(view, meal);
                        childContextMenuBuilder.show();
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

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.add_meal_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = getAlertBuilder(v, null);
                builder.show();
            }
        });

        myToolbar = (Toolbar) findViewById(R.id.meal_list_toolbar);
        myToolbar.setTitle(R.string.meal_list);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @NonNull
    private AlertDialog.Builder getBuilder(final View view, final Meal meal) {

        AlertDialog.Builder childContextMenuBuilder = new AlertDialog.Builder(view.getContext());
        childContextMenuBuilder.setItems(R.array.meal_actions, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        final AlertDialog.Builder builder = getAlertBuilder(view, meal);
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
        return childContextMenuBuilder;
    }

    private AlertDialog.Builder getAlertBuilder(View v, final Meal meal) {
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

        final IngredientListAdapter ingredientListAdapter = new IngredientListAdapter(v.getContext(),
                R.layout.multicheck_list,
                mealIngredientContract.getIngredientsWithMeal(meal, dbHelper));

        ingredientListAdapter.showCheckboxes = true;

        final ListView listView = (ListView) addIngredientView.findViewById(R.id.filterable_list_view);
        listView.setAdapter(ingredientListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Ingredient ingredient = ingredientListAdapter.getItem(position);
                if (ingredient != null) {
                    ingredient.setChecked(!ingredient.isChecked());
                    ingredientListAdapter.notifyDataSetChanged();
                }
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
                            if (item != null && item.isChecked()) {
                                long ingredientId = item.getId();
                                mealIngredientContract.insert(dbHelper, meal.getId(), ingredientId);
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null);

        AlertDialog alertDialog = addIngredientBuilder.create();


        final EditText filterEditText = (EditText) addIngredientView.findViewById(R.id.name_filter);
        filterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Filter filter = ingredientListAdapter.getFilter();
                filter.filter(s, new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int count) {
                        int icon = count == 0 ? 0 : android.R.drawable.ic_menu_add;
                        filterEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, icon, 0);
                    }
                });
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        filterEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Drawable drawable = filterEditText.getCompoundDrawables()[DRAWABLE_RIGHT];
                    if (drawable != null) {
                        Rect bounds = drawable.getBounds();
                        if (event.getRawX() >= (filterEditText.getRight() - bounds.width())) {
                            String text = filterEditText.getText().toString().trim();
                            if (text.length() != 0) {
                                long ingredientId = ingredientContract.insert(dbHelper, text);
                                mealIngredientContract.insert(dbHelper, meal.getId(), ingredientId);
                                ingredientListAdapter.updateResults(mealIngredientContract.getIngredientsWithMeal(meal, dbHelper));
                                filterEditText.setText("");
                                Toast.makeText(v.getContext(), R.string.ingredient_added_message, Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        });

        alertDialog.show();
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
}
