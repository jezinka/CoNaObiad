package com.projects.jezinka.conaobiad.models.tableDefinitions;

import android.provider.BaseColumns;

public class IngredientCategoryContract implements BaseColumns {

    public static String tableName = "ingredient_category";
    private static String columnName = "name";

    public static String getCreateEntriesQuery() {
        return "CREATE TABLE " + tableName + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                columnName + " TEXT)";
    }
}
