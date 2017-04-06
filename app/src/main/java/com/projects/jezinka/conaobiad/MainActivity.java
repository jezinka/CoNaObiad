package com.projects.jezinka.conaobiad;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.projects.jezinka.conaobiad.model.DinnerContract;
import com.projects.jezinka.conaobiad.model.MealContract;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private CoNaObiadDbHelper dbHelper;
    private MealContract mealContract;
    private DinnerContract dinnerContract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new CoNaObiadDbHelper(this);
        mealContract = new MealContract();
        dinnerContract = new DinnerContract();

        List<String> preparedRows = new ArrayList<>();

        if (!mealContract.isAnyMealSaved(dbHelper)) {
            showEmptyMealListMessage(this);
        } else {
            preparedRows = DinnerListHelper.getPreparedRows(dinnerContract.getDinnersByDate(dbHelper, null));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, preparedRows);

        ListView listView = (ListView) findViewById(R.id.dinner_list_view);
        listView.setAdapter(adapter);

        Button addDinnerButton = (Button) findViewById(R.id.new_dinner_button);
        addDinnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = addNewDinnerBuilder(v);
                builder.show();
            }
        });

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(myToolbar);
    }

    private void showEmptyMealListMessage(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(R.string.empty_meal_list_message);
        builder.setCancelable(true);

        builder.setPositiveButton(
                R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(builder.getContext(), MealListActivity.class);
                        startActivity(intent);
                    }
                });

        builder.setNegativeButton(
                R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private AlertDialog.Builder addNewDinnerBuilder(View v) {
        final View view = getLayoutInflater().inflate(R.layout.custom_dialog_add_dinner, new LinearLayout(v.getContext()), false);
        final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle(R.string.add_dinner);
        builder.setView(view);

        final TextView dateView = (TextView) view.findViewById(R.id.date_view);
        final TextView mealName = (TextView) view.findViewById(R.id.meal_name_text);
        final TextView mealId = (TextView) view.findViewById(R.id.meal_name_id);
        final ImageButton datePickerButton = (ImageButton) view.findViewById(R.id.calendar_button);

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v, dateView);
            }
        });

        mealName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMealPickerDialog(v, mealName, mealId);
            }
        });

        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Date date;
                String mealIdText = mealId.getText().toString();

                try {
                    String stringDate = dateView.getText().toString();
                    date = new SimpleDateFormat("dd.MM.yyyy").parse(stringDate);
                } catch (ParseException ex) {
                    date = new Date();
                }

                dinnerContract.insertDinner(view.getContext(), Integer.parseInt(mealIdText), date);
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

    private void showMealPickerDialog(View v, final TextView mealName, final TextView mealId) {
        View addDinnerView = getLayoutInflater().inflate(R.layout.filterable_list_view, new LinearLayout(v.getContext()), false);
        final MealListAdapter adapter = new MealListAdapter(v.getContext(), android.R.layout.simple_list_item_1, mealContract.getAllMealsArray(dbHelper));

        final AlertDialog.Builder addDinnerDialogBuilder = new AlertDialog.Builder(v.getContext());
        addDinnerDialogBuilder.setView(addDinnerView);
        addDinnerDialogBuilder.setTitle(R.string.pick_meal);

        final AlertDialog alertDialog = addDinnerDialogBuilder.create();

        ListView mealListView = (ListView) addDinnerView.findViewById(R.id.filterable_meal_list_view);
        mealListView.setAdapter(adapter);
        mealListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View dialogView, int position, long id) {
                mealName.setText(adapter.getItem(position).getName());
                mealId.setText(String.valueOf(adapter.getItem(position).getId()));
                alertDialog.dismiss();
            }
        });

        EditText filterEditText = (EditText) addDinnerView.findViewById(R.id.meal_name_filter);
        filterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Filter filter = adapter.getFilter();
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

    private void showDatePickerDialog(View v, final TextView dateView) {
        Calendar todayCalendarInstance = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendarInstance = Calendar.getInstance();
                calendarInstance.set(year, monthOfYear, dayOfMonth);
                DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, new Locale("pl", "pl"));
                dateView.setText(df.format(calendarInstance.getTime()));
            }
        }, todayCalendarInstance.get(Calendar.YEAR), todayCalendarInstance.get(Calendar.MONTH), todayCalendarInstance.get(Calendar.DATE));
        datePickerDialog.show();
    }
}
