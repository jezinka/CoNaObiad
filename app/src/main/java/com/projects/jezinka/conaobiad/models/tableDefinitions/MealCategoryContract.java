package com.projects.jezinka.conaobiad.models.tableDefinitions;

import android.provider.BaseColumns;

public class MealCategoryContract implements BaseColumns {

    public static String tableName = "meal_category";
    private static String mealColumn = "ingredient_id";
    private static String categoryColumn = "category_id";

    public static String getCreateEntriesQuery() {
        return "CREATE TABLE " + tableName + " (" +
                mealColumn + " int," +
                categoryColumn + " int," +
                " FOREIGN KEY(" + mealColumn + ") REFERENCES " + MealContract.tableName + "(" + _ID + ") ON DELETE CASCADE," +
                " FOREIGN KEY(" + categoryColumn + ") REFERENCES " + CategoryContract.tableName + "(" + _ID + ") ON DELETE CASCADE)";
    }
}
