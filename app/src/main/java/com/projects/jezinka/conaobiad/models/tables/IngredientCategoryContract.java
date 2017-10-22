package com.projects.jezinka.conaobiad.models.tables;

import android.provider.BaseColumns;

public class IngredientCategoryContract implements BaseColumns {

    private static final String tableName = "ingredient_category";

    public static String getTableName() {
        return tableName;
    }

    private static String ingredientColumn = "ingredient_id";
    private static String categoryColumn = "category_id";

    public static String getCreateEntriesQuery() {
        return "CREATE TABLE " + tableName + " (" +
                ingredientColumn + " int," +
                categoryColumn + " int," +
                " FOREIGN KEY(" + ingredientColumn + ") REFERENCES " + IngredientContract.getTableName() + "(" + _ID + ") ON DELETE CASCADE," +
                " FOREIGN KEY(" + categoryColumn + ") REFERENCES " + CategoryContract.getTableName() + "(" + _ID + ") ON DELETE CASCADE)";
    }
}
