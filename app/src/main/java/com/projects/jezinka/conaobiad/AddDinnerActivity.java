package com.projects.jezinka.conaobiad;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.projects.jezinka.conaobiad.model.DinnerContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddDinnerActivity extends AppCompatActivity {
    private CoNaObiadDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbHelper = new CoNaObiadDbHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dinner);
    }

    public void addDinner(View view) {
        Date date;

        TextView dateText = (TextView) findViewById(R.id.date_view);
        EditText mealId = (EditText) findViewById(R.id.meal_name_id);
        String meal = mealId.getText().toString();
        DinnerContract dinnerContract = new DinnerContract();
        try {
            String stringDate = dateText.getText().toString();
            date = new SimpleDateFormat("dd.MM.yyyy").parse(stringDate);
        } catch (ParseException ex) {
            date = new Date();
        }

        dinnerContract.insertDinner(view.getContext(), Integer.parseInt(meal), date);
        finish();
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showDinnersDialog(View v) {
        DialogFragment newFragment = new MealPickerFragment();
        newFragment.show(getSupportFragmentManager(), "mealPicker");
    }

}
