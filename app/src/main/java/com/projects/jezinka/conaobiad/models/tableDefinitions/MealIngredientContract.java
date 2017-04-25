package com.projects.jezinka.conaobiad.models.tableDefinitions;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.models.MealIngredient;

public class MealIngredientContract extends BaseTable implements BaseColumns {

    private String COLUMN_MEAL_ID;
    private String COLUMN_INGREDIENT_ID;

    private MealContract mealContract;
    private IngredientContract ingredientContract;

    public MealIngredientContract() {

        this.TABLE_NAME = "meal_ingredient";
        this.COLUMN_INGREDIENT_ID = "ingredient_id";
        this.COLUMN_MEAL_ID = "meal_id";

        this.mealContract = new MealContract();
        this.ingredientContract = new IngredientContract();

        this.SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                this.COLUMN_INGREDIENT_ID + " int, " +
                this.COLUMN_MEAL_ID + " int, " +
                " FOREIGN KEY(" + COLUMN_MEAL_ID + ") REFERENCES " + this.mealContract.getTableName() + "(" + _ID + ")," +
                " FOREIGN KEY(" + COLUMN_INGREDIENT_ID + ") REFERENCES " + this.ingredientContract.getTableName() + "(" + _ID + ")" +
                ")";
    }

    public boolean insert(CoNaObiadDbHelper dbHelper, int mealId, int ingredientId) {
        String tableName = this.getTableName();

        ContentValues contentValues = new ContentValues();
        contentValues.put("meal_id", mealId);
        contentValues.put("ingredient_id", ingredientId);

        dbHelper.insert(tableName, contentValues);
        return true;
    }

    @NonNull
    MealIngredient getFromCursor(Cursor res) {
        return new MealIngredient(0, 0);
    }
}
