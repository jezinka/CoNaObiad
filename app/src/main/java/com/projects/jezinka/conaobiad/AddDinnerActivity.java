package com.projects.jezinka.conaobiad;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class AddDinnerActivity extends AppCompatActivity {
    private CoNaObiadDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbHelper = new CoNaObiadDbHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dinner);
    }

    public void addDinner(View view) {
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
