package com.projects.jezinka.conaobiad;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.projects.jezinka.conaobiad.model.Meal;
import com.projects.jezinka.conaobiad.model.MealContract;

public class MealPickerFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View _view = inflater.inflate(R.layout.filterable_list_view, container, false);
        return _view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        CoNaObiadDbHelper dbHelper = new CoNaObiadDbHelper(getContext());
        MealContract mealContract = new MealContract();
        Meal[] meals = mealContract.getAllMealsArray(dbHelper);
        View view = getActivity().getLayoutInflater().inflate(R.layout.filterable_list_view, new LinearLayout(getActivity()), false);

        final MealListAdapter adapter = new MealListAdapter(getContext(), android.R.layout.simple_list_item_1, meals);

        builder.setView(view).setTitle(R.string.pick_meal);

        ListView mealListView = (ListView) view.findViewById(R.id.filterable_meal_list_view);
        mealListView.setAdapter(adapter);
        mealListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View dialogView, int position, long id) {
                TextView mealName = (TextView) getActivity().findViewById(R.id.meal_name_text);
                mealName.setText(adapter.getItem(position).getName());
                MealPickerFragment.this.dismiss();
            }
        });

        return builder.create();
    }
}
