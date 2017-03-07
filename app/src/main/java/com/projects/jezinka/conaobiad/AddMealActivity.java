package com.projects.jezinka.conaobiad;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class AddMealActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);
    }

    public void addMeal(View view) {
        finish();
    }
}
