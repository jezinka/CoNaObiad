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
import com.projects.jezinka.conaobiad.adapters.IngredientListAdapter;
import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.models.Ingredient;
import com.projects.jezinka.conaobiad.models.tableDefinitions.IngredientContract;

import java.util.ArrayList;

public class IngredientListActivity extends AppCompatActivity {

    private CoNaObiadDbHelper helper;
    private IngredientListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_list);
        final IngredientContract ingredientContract = new IngredientContract();
        helper = new CoNaObiadDbHelper(this);

        adapter = new IngredientListAdapter(this, android.R.layout.simple_list_item_multiple_choice, ingredientContract.getAllIngredientsArray(helper));

        final ListView listView = (ListView) findViewById(R.id.ingredient_list_view);
        listView.setAdapter(adapter);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.ingredient_list_toolbar);
        setSupportActionBar(myToolbar);

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.add_ingredient_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = getAlertBuilder(v, ingredientContract, null);
                builder.show();
            }
        });

        final FloatingActionButton deleteButton = (FloatingActionButton) findViewById(R.id.delete_ingredient_button);

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
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Ingredient ingredient = adapter.getItem(position);
                final AlertDialog.Builder builder = getAlertBuilder(view, ingredientContract, ingredient);
                builder.show();
                return true;
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray positions = listView.getCheckedItemPositions();
                ArrayList<Long> ingredientIds = new ArrayList<Long>();
                for (int i = 0; i < listView.getCount(); i++) {
                    if (positions.get(i)) {
                        ingredientIds.add(adapter.getItemId(i));
                    }
                }
                listView.clearChoices();
                deleteButton.setVisibility(View.INVISIBLE);
                ingredientContract.delete(ingredientIds.toArray(new Long[ingredientIds.size()]), helper);
                adapter.updateResults(ingredientContract.getAllIngredientsArray(helper));
            }
        });

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private AlertDialog.Builder getAlertBuilder(View v, final IngredientContract ingredientContract, final Ingredient ingredient) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle(R.string.put_ingredient_name);

        final EditText input = new EditText(v.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        if (ingredient != null) {
            input.setText(ingredient.getName());
        }
        builder.setView(input);

        builder.setPositiveButton(ingredient == null ? R.string.add : R.string.edit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ingredientName = input.getText().toString().trim();
                if (ingredient != null) {
                    ingredientContract.update(helper, ingredientName, ingredient);
                } else {
                    ingredientContract.insert(helper, ingredientName);
                }
                adapter.updateResults(ingredientContract.getAllIngredientsArray(helper));
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
