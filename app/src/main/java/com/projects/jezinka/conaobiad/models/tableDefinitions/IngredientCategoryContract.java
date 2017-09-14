package com.projects.jezinka.conaobiad.models.tableDefinitions;

import android.provider.BaseColumns;

public class IngredientCategoryContract implements BaseColumns {

    public static String tableName = "ingredient_category";
    private static String ingredientColumn = "ingredient_id";
    private static String categoryColumn = "category_id";

    public static String getCreateEntriesQuery() {
        return "CREATE TABLE " + tableName + " (" +
                ingredientColumn + " int," +
                categoryColumn + " int," +
                " FOREIGN KEY(" + ingredientColumn + ") REFERENCES " + IngredientContract.tableName + "(" + _ID + ") ON DELETE CASCADE," +
                " FOREIGN KEY(" + categoryColumn + ") REFERENCES " + CategoryContract.tableName + "(" + _ID + ") ON DELETE CASCADE)";
    }
}
