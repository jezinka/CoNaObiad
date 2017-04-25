package com.projects.jezinka.conaobiad.models.tableDefinitions;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.models.Meal;
import com.projects.jezinka.conaobiad.models.MealIngredient;

import java.util.ArrayList;
import java.util.List;

public class MealIngredientContract extends BaseTable implements BaseColumns {

    String COLUMN_MEAL_ID;
    String COLUMN_INGREDIENT_ID;

    private MealContract mealContract;
    private IngredientContract ingredientContract;

    private String SQL_GET_INGREDIENTS;

    public MealIngredientContract() {

        this.TABLE_NAME = "meal_ingredient";
        this.COLUMN_INGREDIENT_ID = "ingredient_id";
        this.COLUMN_MEAL_ID = "meal_id";

        this.mealContract = new MealContract();
        this.ingredientContract = new IngredientContract();

        this.SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                this.COLUMN_INGREDIENT_ID + " int, " +
                this.COLUMN_MEAL_ID + " int, " +
                " FOREIGN KEY(" + COLUMN_MEAL_ID + ") REFERENCES " + this.mealContract.getTableName() + "(" + _ID + ")," +
                " FOREIGN KEY(" + COLUMN_INGREDIENT_ID + ") REFERENCES " + this.ingredientContract.getTableName() + "(" + _ID + ")" +
                ")";

        this.SQL_GET_INGREDIENTS =
                "select " + this.ingredientContract.getTableName() + "." + this.ingredientContract.COLUMN_NAME +
                        " from " + this.getTableName() +
                        " join " + this.mealContract.getTableName() +
                        " on " + this.mealContract.getTableName() + "." + _ID + "= " + this.getTableName() + "." + this.COLUMN_MEAL_ID +
                        " join " + this.ingredientContract.getTableName() +
                        " on " + this.ingredientContract.getTableName() + "." + _ID + "= " + this.getTableName() + "." + this.COLUMN_INGREDIENT_ID +
                        " where " + this.mealContract.getTableName() + "." + _ID + "=?;";
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

    public List<String> getIngredientsForMeal(Meal meal, SQLiteOpenHelper helper) {
        ArrayList<String> array_list = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.rawQuery(this.SQL_GET_INGREDIENTS, new String[]{Integer.toString(meal.getId())});

        if (res != null && res.getCount() > 0) {
            res.moveToFirst();

            do {
                array_list.add(res.getString(0));
            } while (res.moveToNext());
        }

        db.close();
        return array_list;
    }
}
