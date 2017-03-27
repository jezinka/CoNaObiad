package com.projects.jezinka.conaobiad;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.projects.jezinka.conaobiad.model.MealContract;

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
        MealContract mealContract = new MealContract();
        final String[] meals = mealContract.getAllMealsArray(dbHelper);
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle(R.string.pick_meal)
                .setItems(meals, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int position) {
                        TextView mealName = (TextView) findViewById(R.id.meal_name_text);
                        mealName.setText(meals[position]);
                    }
                });
        builder.show();
    }

}
