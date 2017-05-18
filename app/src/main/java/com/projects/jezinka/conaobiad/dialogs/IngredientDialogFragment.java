package com.projects.jezinka.conaobiad.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.projects.jezinka.conaobiad.R;


public class IngredientDialogFragment extends DialogFragment {

    IngredientDialogListener mListener;

    public static IngredientDialogFragment newInstance(long ingredientId, String ingredientName) {
        IngredientDialogFragment f = new IngredientDialogFragment();

        Bundle args = new Bundle();
        args.putLong("ingredientId", ingredientId);
        args.putString("ingredientName", ingredientName);
        f.setArguments(args);

        return f;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog_add_ingredient, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(view)
                .setTitle(R.string.put_ingredient_name)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(IngredientDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        String ingredientName = getArguments().getString("ingredientName", "");

        if (!ingredientName.equals("")) {
            EditText input = (EditText) view.findViewById(R.id.ingredient_name);
            input.setText(ingredientName);
        }

        return builder.create();
    }

    public interface IngredientDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;

        if (context instanceof Activity) {
            activity = (Activity) context;
        } else {
            return;
        }

        try {
            mListener = (IngredientDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement IngredientDialogListener");
        }
    }
}
