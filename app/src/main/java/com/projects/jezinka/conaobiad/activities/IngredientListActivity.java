package com.projects.jezinka.conaobiad.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.projects.jezinka.conaobiad.R;
import com.projects.jezinka.conaobiad.adapters.IngredientListAdapter;
import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.dialogs.IngredientDialogFragment;
import com.projects.jezinka.conaobiad.models.Ingredient;
import com.projects.jezinka.conaobiad.models.tables.IngredientContract;
import com.projects.jezinka.conaobiad.utils.CheckboxesUtils;

import java.util.ArrayList;

public class IngredientListActivity extends AppCompatActivity implements IngredientDialogFragment.IngredientDialogListener {

    private CoNaObiadDbHelper helper;
    private IngredientListAdapter adapter;
    private IngredientContract ingredientContract;
    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_list);

        ingredientContract = new IngredientContract();
        helper = new CoNaObiadDbHelper(this);
        adapter = new IngredientListAdapter(this, R.layout.multicheck_list, ingredientContract.getAllIngredientsArray(helper));

        final ListView listView = (ListView) findViewById(R.id.ingredient_list_view);
        listView.setAdapter(adapter);

        myToolbar = (Toolbar) findViewById(R.id.ingredient_list_toolbar);
        setSupportActionBar(myToolbar);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int viewId = view.getId();
                Ingredient ingredient = adapter.getItem(position);

                if (viewId == R.id.text1) {
                    showIngredientDialog(ingredient);
                } else if (viewId == R.id.checkBox && ingredient != null) {
                    ingredient.setChecked(!ingredient.isChecked());
                    adapter.notifyDataSetChanged();
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                CheckboxesUtils.toggleCheckboxesAndToolbar(parent.getContext(), adapter, myToolbar);
                return true;
            }
        });

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_back_arrow_sketch);
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
                ArrayList<Long> ingredientIds = new ArrayList<>();

                for (int i = 0; i < adapter.getCount(); i++) {
                    Ingredient ingredient = adapter.getItem(i);
                    if (ingredient != null && ingredient.isChecked()) {
                        ingredientIds.add(ingredient.getId());
                    }
                }
                CheckboxesUtils.toggleCheckboxesAndToolbar(this, adapter, myToolbar);
                ingredientContract.delete(ingredientIds.toArray(new Long[ingredientIds.size()]), helper);
                adapter.updateResults(ingredientContract.getAllIngredientsArray(helper));

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showIngredientDialog(Ingredient ingredient) {
        DialogFragment newFragment;

        long ingredientId = ingredient != null ? ingredient.getId() : -1;
        String ingredientName = ingredient != null ? ingredient.getName() : "";

        newFragment = IngredientDialogFragment.newInstance(ingredientId, ingredientName);
        newFragment.show(getSupportFragmentManager(), "IngredientDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment) {

        EditText input = (EditText) dialogFragment.getDialog().findViewById(R.id.ingredient_name);
        String ingredientName = input.getText().toString().trim();

        long ingredientId = dialogFragment.getArguments().getLong("ingredientId", -1);

        if (ingredientId != -1) {
            ingredientContract.update(helper, ingredientName, ingredientId);
        } else {
            ingredientContract.insert(helper, ingredientName);
        }
        adapter.updateResults(ingredientContract.getAllIngredientsArray(helper));
    }

    public void addIngredientButtonClick(View view) {
        showIngredientDialog(null);
    }
}
