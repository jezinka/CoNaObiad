package com.projects.jezinka.conaobiad.models.tables;

import android.provider.BaseColumns;

public class IngredientCategoryContract implements BaseColumns {

    private IngredientCategoryContract() {
        throw new IllegalStateException("Contract class");
    }

    private static final String TABLE_NAME = "ingredient_category";

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getCreateEntriesQuery() {

        String ingredientColumn = "ingredient_id";
        String categoryColumn = "category_id";

        return "CREATE TABLE " + TABLE_NAME + " (" +
                ingredientColumn + " int," +
                categoryColumn + " int," +
                " FOREIGN KEY(" + ingredientColumn + ") REFERENCES " + IngredientContract.getTableName() + "(" + _ID + ") ON DELETE CASCADE," +
                " FOREIGN KEY(" + categoryColumn + ") REFERENCES " + CategoryContract.getTableName() + "(" + _ID + ") ON DELETE CASCADE)";
    }
}
