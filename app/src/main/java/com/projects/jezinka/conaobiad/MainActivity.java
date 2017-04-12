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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.projects.jezinka.conaobiad.model.Dinner;
import com.projects.jezinka.conaobiad.model.DinnerContract;
import com.projects.jezinka.conaobiad.model.MealContract;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private CoNaObiadDbHelper dbHelper;
    private MealContract mealContract;
    private DinnerContract dinnerContract;
    private DinnerExpandableListAdapter dinnerExpandableListAdapter;

    private final DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, new Locale("pl", "pl"));

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

        dinnerExpandableListAdapter = new DinnerExpandableListAdapter(this, dinnerContract.getDinners(dbHelper));

        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.expandable_dinner_list_view);
        expandableListView.setAdapter(dinnerExpandableListAdapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, final View v, final int groupPosition, final int childPosition, final long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setItems(R.array.dinner_child_actions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Date date = dinnerExpandableListAdapter.getGroup(groupPosition);
                        switch (which) {
                            case 0:
                                final AlertDialog.Builder builder = addNewDinnerBuilder(v, date);
                                builder.show();
                                break;
                            case 1:
                                dinnerContract.deleteDinner(id, dbHelper);
                                dinnerExpandableListAdapter.updateResults(dinnerContract.getDinners(dbHelper));
                                break;
                            case 2:
                                Dinner dinner = dinnerExpandableListAdapter.getChild(groupPosition, childPosition);
                                final AlertDialog.Builder builder2 = addNewDinnerBuilder(v, date, dinner);
                                builder2.show();
                                break;
                        }
                    }
                });
                builder.show();
                return true;
            }
        });

        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int itemType = ExpandableListView.getPackedPositionType(id);
                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    Date date = dinnerExpandableListAdapter.getGroup(position);
                    final AlertDialog.Builder builder = addNewDinnerBuilder(view, date);
                    builder.show();
                    return true;
                }

                return false;

            }
        });

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
        return addNewDinnerBuilder(v, null, null);
    }

    private AlertDialog.Builder addNewDinnerBuilder(View v, Date date) {
        return addNewDinnerBuilder(v, date, null);
    }

    private AlertDialog.Builder addNewDinnerBuilder(View v, final Date date, final Dinner dinner) {
        final View view = getLayoutInflater().inflate(R.layout.custom_dialog_add_dinner, new LinearLayout(v.getContext()), false);
        final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle(R.string.add_dinner);
        builder.setView(view);

        final TextView dateView = (TextView) view.findViewById(R.id.date_view);
        final TextView mealName = (TextView) view.findViewById(R.id.meal_name_text);
        final TextView mealId = (TextView) view.findViewById(R.id.meal_name_id);

        if (date != null) {
            dateView.setText(df.format(date));
        } else {
            dateView.setText(df.format(new Date()));
        }

        if (dinner != null) {
            mealName.setText(dinner.getMeal().getName());
            mealId.setText(String.valueOf(dinner.getMeal().getId()));
        }

        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v, dateView, date);
            }
        });

        if (dinner == null) {
            mealName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMealPickerDialog(v, mealName, mealId);
                }
            });
        }

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

                if (dinner != null) {
                    dinnerContract.updateDinner(view.getContext(), dinner, date);
                } else {

                dinnerContract.insertDinner(view.getContext(), Integer.parseInt(mealIdText), date);
                }
                dinnerExpandableListAdapter.updateResults(dinnerContract.getDinners(dbHelper));
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

    private void showDatePickerDialog(View v, final TextView dateView, Date date) {
        Calendar todayCalendarInstance = Calendar.getInstance();

        if (date != null) {
            todayCalendarInstance.setTime(date);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendarInstance = Calendar.getInstance();
                calendarInstance.set(year, monthOfYear, dayOfMonth);
                dateView.setText(df.format(calendarInstance.getTime()));
            }
        }, todayCalendarInstance.get(Calendar.YEAR), todayCalendarInstance.get(Calendar.MONTH), todayCalendarInstance.get(Calendar.DATE));
        datePickerDialog.show();
    }
}
