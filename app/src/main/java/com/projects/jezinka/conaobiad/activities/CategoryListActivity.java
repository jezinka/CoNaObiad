package com.projects.jezinka.conaobiad.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
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
import com.projects.jezinka.conaobiad.adapters.CategoryListAdapter;
import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.dialogs.CategoryDialogFragment;
import com.projects.jezinka.conaobiad.models.Category;
import com.projects.jezinka.conaobiad.models.tableDefinitions.CategoryContract;

import java.util.ArrayList;

public class CategoryListActivity extends AppCompatActivity implements CategoryDialogFragment.CategoryDialogListener {

    private CoNaObiadDbHelper helper;
    private CategoryListAdapter adapter;
    private CategoryContract categoryContract;
    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        myToolbar = (Toolbar) findViewById(R.id.category_list_toolbar);
        setSupportActionBar(myToolbar);

        categoryContract = new CategoryContract();
        helper = new CoNaObiadDbHelper(this);
        adapter = new CategoryListAdapter(this, R.layout.multicheck_list, categoryContract.getAllCategoriesArray(helper));

        final ListView listView = (ListView) findViewById(R.id.category_list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int viewId = view.getId();
                Category category = adapter.getItem(position);

                if (viewId == R.id.text1) {
                    showCategoryDialog(category);
                } else if (viewId == R.id.checkBox && category != null) {
                    category.setChecked(!category.isChecked());
                    adapter.notifyDataSetChanged();
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                toggleCheckboxesAndToolbar();
                return true;
            }
        });

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_back_arrow_sketch);
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
                ArrayList<Long> categoriesIds = new ArrayList<>();

                for (int i = 0; i < adapter.getCount(); i++) {
                    Category category = adapter.getItem(i);
                    if (category != null && category.isChecked()) {
                        categoriesIds.add(category.getId());
                    }
                }
                toggleCheckboxesAndToolbar();
                categoryContract.delete(categoriesIds.toArray(new Long[categoriesIds.size()]), helper);
                adapter.updateResults(categoryContract.getAllCategoriesArray(helper));

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showCategoryDialog(Category category) {
        DialogFragment newFragment;

        long categoryId = category != null ? category.getId() : -1;
        String categoryName = category != null ? category.getName() : "";

        newFragment = CategoryDialogFragment.newInstance(categoryId, categoryName);
        newFragment.show(getSupportFragmentManager(), "CategoryDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment) {

        EditText input = (EditText) dialogFragment.getDialog().findViewById(R.id.category_name);
        String categoryName = input.getText().toString().trim();

        long categoryId = dialogFragment.getArguments().getLong("categoryId", -1);

        if (categoryId != -1) {
            categoryContract.update(helper, categoryName, categoryId);
        } else {
            categoryContract.insert(helper, categoryName);
        }
        adapter.updateResults(categoryContract.getAllCategoriesArray(helper));
    }

    public void addCategoryButtonClick(View view) {
        showCategoryDialog(null);
    }
}
