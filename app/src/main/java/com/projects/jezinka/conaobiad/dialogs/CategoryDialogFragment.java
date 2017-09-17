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


public class CategoryDialogFragment extends DialogFragment {

    CategoryDialogListener mListener;

    public static CategoryDialogFragment newInstance(long categoryId, String categoryName) {
        CategoryDialogFragment f = new CategoryDialogFragment();

        Bundle args = new Bundle();
        args.putLong("categoryId", categoryId);
        args.putString("categoryName", categoryName);
        f.setArguments(args);

        return f;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog_add_category, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.put_category_name)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(CategoryDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        String categoryName = getArguments().getString("categoryName", "");

        if (!categoryName.equals("")) {
            EditText input = (EditText) view.findViewById(R.id.category_name);
            input.setText(categoryName);
        }

        return builder.create();
    }

    public interface CategoryDialogListener {
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
            mListener = (CategoryDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement CategoryDialogListener");
        }
    }
}
