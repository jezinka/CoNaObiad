package com.projects.jezinka.conaobiad.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.jezinka.conaobiad.R;
import com.projects.jezinka.conaobiad.activities.MainActivity;
import com.projects.jezinka.conaobiad.adapters.DinnerAdapter;
import com.projects.jezinka.conaobiad.adapters.MealListAdapter;
import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.models.Dinner;
import com.projects.jezinka.conaobiad.models.Meal;
import com.projects.jezinka.conaobiad.models.tables.DinnerContract;
import com.projects.jezinka.conaobiad.models.tables.MealContract;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DinnerDialogFragment extends DialogFragment {

    private TextView dateView;
    private TextView mealNameTextView;

    private CoNaObiadDbHelper dbHelper;
    private DinnerAdapter dinnerAdapter;
    private DinnerContract dinnerContract;

    private final DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, new Locale("pl", "pl"));

    public static DinnerDialogFragment newInstance(long date, Dinner dinner) {
        DinnerDialogFragment f = new DinnerDialogFragment();

        Bundle args = new Bundle();
        args.putLong("date", date);

        if (dinner != null) {
            args.putLong("dinnerId", dinner.getId());
            args.putLong("mealId", dinner.getMealId());
            args.putString("mealName", dinner.getMealName());
        }

        f.setArguments(args);

        return f;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dinnerContract = new DinnerContract();
        dinnerAdapter = ((MainActivity) getActivity()).getAdapter();
        dbHelper = ((MainActivity) getActivity()).getDbHelper();

        final long dinnerId = getArguments().getLong("dinnerId", -1);

        View view = getActivity().getLayoutInflater().inflate(R.layout.custom_dialog_add_dinner, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(dinnerId == -1 ? R.string.add_dinner : R.string.edit_date)
                .setView(view)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(dinnerId == -1 ? R.string.add : R.string.edit,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Date date;

                                try {
                                    String stringDate = dateView.getText().toString();
                                    date = new SimpleDateFormat("dd.MM.yyyy").parse(stringDate);
                                } catch (ParseException ex) {
                                    date = new Date();
                                }

                                if (dinnerId != -1) {
                                    dinnerContract.update(dbHelper, dinnerId, date);
                                } else {
                                    dinnerContract.insert(dbHelper, getArguments().getLong("mealId"), date);
                                }
                                dinnerAdapter.updateResults();
                            }
                        });

        mealNameTextView = (TextView) view.findViewById(R.id.meal_name_text);
        mealNameTextView.setText(getArguments().getString("mealName", ""));

        mealNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getArguments().getLong("mealId", -1) == -1) {
                    showMealPickerDialog();
                } else {
                    Toast.makeText(getActivity(), R.string.only_date_info, Toast.LENGTH_SHORT).show();
                }
            }
        });


        final Date date = new Date(getArguments().getLong("date", new Date().getTime()));
        dateView = (TextView) view.findViewById(R.id.date_view);
        dateView.setText(df.format(date));
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(date);
            }
        });

        return builder.create();
    }

    public void showMealPickerDialog() {
        MealContract mealContract = new MealContract();

        View addDinnerView = getActivity().getLayoutInflater().inflate(R.layout.filterable_list_view, null);

        final MealListAdapter mealAdapter = new MealListAdapter(getContext(), R.layout.single_select_list, mealContract.getAllMealsArray(dbHelper));

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(addDinnerView)
                .setTitle(R.string.pick_meal)
                .create();

        ListView mealListView = (ListView) addDinnerView.findViewById(R.id.filterable_list_view);
        mealListView.setAdapter(mealAdapter);
        mealListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View dialogView, int position, long id) {
                Meal meal = mealAdapter.getItem(position);

                if (meal != null) {
                    mealNameTextView.setText(meal.getName());
                    getArguments().putLong("mealId", meal.getId());
                }

                alertDialog.dismiss();
            }
        });

        EditText filterEditText = (EditText) addDinnerView.findViewById(R.id.name_filter);
        filterEditText.addTextChangedListener(getWatcher(mealAdapter));
        alertDialog.show();
    }

    @NonNull
    private TextWatcher getWatcher(final MealListAdapter mealAdapter) {
        return new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Filter filter = mealAdapter.getFilter();
                filter.filter(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    private void showDatePickerDialog(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendarInstance = Calendar.getInstance();
                        calendarInstance.set(year, monthOfYear, dayOfMonth);
                        dateView.setText(df.format(calendarInstance.getTime()));
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE))
                .show();
    }
}
