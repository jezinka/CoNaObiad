package com.projects.jezinka.conaobiad.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.projects.jezinka.conaobiad.R;
import com.projects.jezinka.conaobiad.adapters.CheckboxesArrayAdapterInterface;

public class CheckboxesUtils {

    private CheckboxesUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void toggleCheckboxesAndToolbar(Context context, CheckboxesArrayAdapterInterface adapter, Toolbar myToolbar) {
        int colorId = adapter.isShowCheckboxes() ? R.color.colorPrimary : android.R.color.darker_gray;
        myToolbar.setBackgroundColor(ContextCompat.getColor(context, colorId));

        MenuItem deleteMenuButton = myToolbar.getMenu().findItem(R.id.delete_menu_button);
        deleteMenuButton.setVisible(!deleteMenuButton.isVisible());

        adapter.setShowCheckboxes(!adapter.isShowCheckboxes());
        adapter.notifyDataSetChanged();
    }
}
