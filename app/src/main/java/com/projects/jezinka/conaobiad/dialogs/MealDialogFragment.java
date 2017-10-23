package com.projects.jezinka.conaobiad.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.projects.jezinka.conaobiad.R;


public class MealDialogFragment extends DialogFragment {

    MealDialogListener mListener;

    public static MealDialogFragment newInstance(long mealId, String mealName) {
        MealDialogFragment f = new MealDialogFragment();

        Bundle args = new Bundle();
        args.putLong("mealId", mealId);
        args.putString("mealName", mealName);
        f.setArguments(args);

        return f;
    }

    public static MealDialogFragment newInstance() {
        MealDialogFragment f = new MealDialogFragment();

        Bundle args = new Bundle();
        f.setArguments(args);

        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog_add_meal, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.put_meal_name)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mListener.onDialogPositiveClick(MealDialogFragment.this);
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

        String mealName = getArguments().getString("mealName", "");

        if (!mealName.equals("")) {
            EditText input = (EditText) view.findViewById(R.id.meal_name);
            input.setText(mealName);
        }

        return builder.create();
    }

    public interface MealDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof Activity)) {
            return;
        }

        Activity activity = (Activity) context;

        try {
            mListener = (MealDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement MealDialogListener");
        }
    }
}
