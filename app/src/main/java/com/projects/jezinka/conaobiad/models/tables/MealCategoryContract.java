package com.projects.jezinka.conaobiad.models.tables;

import android.provider.BaseColumns;

public class MealCategoryContract implements BaseColumns {

    private static final String tableName = "meal_category";

    public static String getTableName() {
        return tableName;
    }

    private static String mealColumn = "ingredient_id";
    private static String categoryColumn = "category_id";

    public static String getCreateEntriesQuery() {
        return "CREATE TABLE " + tableName + " (" +
                mealColumn + " int," +
                categoryColumn + " int," +
                " FOREIGN KEY(" + mealColumn + ") REFERENCES " + MealContract.getTableName() + "(" + _ID + ") ON DELETE CASCADE," +
                " FOREIGN KEY(" + categoryColumn + ") REFERENCES " + CategoryContract.getTableName() + "(" + _ID + ") ON DELETE CASCADE)";
    }
}
