package com.projects.jezinka.conaobiad.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.Toast;

import com.projects.jezinka.conaobiad.R;
import com.projects.jezinka.conaobiad.activities.MealListActivity;
import com.projects.jezinka.conaobiad.adapters.IngredientListAdapter;
import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.models.Ingredient;
import com.projects.jezinka.conaobiad.models.tableDefinitions.IngredientContract;
import com.projects.jezinka.conaobiad.models.tableDefinitions.MealIngredientContract;

public class IngredientPickerDialogFragment extends DialogFragment {

    private static final int DRAWABLE_RIGHT = 2;
    private CoNaObiadDbHelper dbHelper;
    private IngredientListAdapter adapter;
    private MealIngredientContract mealIngredientContract;

    public static IngredientPickerDialogFragment newInstance(long mealId) {
        IngredientPickerDialogFragment f = new IngredientPickerDialogFragment();

        Bundle args = new Bundle();
        args.putLong("mealId", mealId);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Long mealId = getArguments().getLong("mealId", -1);
        mealIngredientContract = new MealIngredientContract();
        dbHelper = ((MealListActivity) getActivity()).getDbHelper();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.filterable_list_view, null);

        adapter = new IngredientListAdapter(getActivity(),
                R.layout.multicheck_list,
                mealIngredientContract.getIngredientsWithMeal(mealId, dbHelper));

        adapter.showCheckboxes = true;

        final ListView listView = (ListView) view.findViewById(R.id.filterable_list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Ingredient ingredient = adapter.getItem(position);
                if (ingredient != null) {
                    ingredient.setChecked(!ingredient.isChecked());
                    adapter.notifyDataSetChanged();
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.put_ingredient_name)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mealIngredientContract.deleteForMeal(dbHelper, mealId);
                        for (int i = 0; i < adapter.getCount(true); i++) {
                            Ingredient item = adapter.getItem(i, true);
                            if (item != null && item.isChecked()) {
                                long ingredientId = item.getId();
                                mealIngredientContract.insert(dbHelper, mealId, ingredientId);
                            }
                        }
                    }
                });

        AlertDialog alertDialog = builder.create();

        final EditText filterEditText = (EditText) view.findViewById(R.id.name_filter);
        filterEditText.addTextChangedListener(getWatcher(filterEditText));

        filterEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }

                Drawable drawable = filterEditText.getCompoundDrawables()[DRAWABLE_RIGHT];
                if (drawable == null) {
                    return false;
                }

                if (event.getRawX() >= (filterEditText.getRight() - drawable.getBounds().width())) {
                    return addNewIngredient(filterEditText, mealId);
                }

                return false;
            }
        });

        return alertDialog;
    }

    private boolean addNewIngredient(EditText filterEditText, Long mealId) {
        String text = filterEditText.getText().toString().trim();

        if (text.length() == 0) {
            return false;
        }

        IngredientContract ingredientContract = new IngredientContract();
        long ingredientId = ingredientContract.insert(dbHelper, text);
        mealIngredientContract.insert(dbHelper, mealId, ingredientId);
        adapter.updateResults(mealIngredientContract.getIngredientsWithMeal(mealId, dbHelper));
        filterEditText.setText("");
        Toast.makeText(getActivity(), R.string.ingredient_added_message, Toast.LENGTH_SHORT).show();
        return true;
    }

    @NonNull
    private TextWatcher getWatcher(final EditText filterEditText) {
        return new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Filter filter = adapter.getFilter();
                filter.filter(s, new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int count) {
                        int icon = count == 0 ? android.R.drawable.ic_menu_add : 0;
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
        };
    }
}