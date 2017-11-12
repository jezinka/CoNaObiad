package com.projects.jezinka.conaobiad.models.tables;

import android.provider.BaseColumns;

public class MealCategoryContract implements BaseColumns {
    private static final String TABLE_NAME = "meal_category";

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getCreateEntriesQuery() {

        String mealColumn = "ingredient_id";
        String categoryColumn = "category_id";

        return "CREATE TABLE " + TABLE_NAME + " (" +
                mealColumn + " int," +
                categoryColumn + " int," +
                " FOREIGN KEY(" + mealColumn + ") REFERENCES " + MealContract.getTableName() + "(" + _ID + ") ON DELETE CASCADE," +
                " FOREIGN KEY(" + categoryColumn + ") REFERENCES " + CategoryContract.getTableName() + "(" + _ID + ") ON DELETE CASCADE)";
    }
}
