package com.projects.jezinka.conaobiad.models.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.models.Ingredient;
import com.projects.jezinka.conaobiad.models.Meal;
import com.projects.jezinka.conaobiad.models.MealIngredient;

import java.util.ArrayList;
import java.util.List;

public class MealIngredientContract extends BaseTable implements BaseColumns {

    private static final String CHECKED_COLUMN_NAME = "checked";

    private static final String TABLE_NAME = "meal_ingredient";

    public static String getTableName() {
        return TABLE_NAME;
    }

    static final String COLUMN_INGREDIENT_ID = "ingredient_id";
    static final String COLUMN_MEAL_ID = "meal_id";

    public static String getCreateEntriesQuery() {
        return "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_INGREDIENT_ID + " int, " +
                COLUMN_MEAL_ID + " int, " +
                " FOREIGN KEY(" + COLUMN_MEAL_ID + ") REFERENCES " + MealContract.getTableName() + "(" + _ID + ") ON DELETE CASCADE," +
                " FOREIGN KEY(" + COLUMN_INGREDIENT_ID + ") REFERENCES " + IngredientContract.getTableName() + "(" + _ID + ") ON DELETE CASCADE" +
                ")";
    }

    @NonNull
    private String getIngredientsWithChecked() {
        return "select " +
                IngredientContract.getTableName() + "." + _ID + ", " +
                IngredientContract.getTableName() + "." + IngredientContract.COLUMN_NAME + ", " +
                "  CASE WHEN EXISTS( " +
                "             SELECT 1 from " + IngredientContract.getTableName() + " as ingredient2 " +
                "               join " + MealIngredientContract.getTableName() + " as meal_ingredient2 on ingredient2._id = meal_ingredient2.ingredient_id " +
                "               join " + MealContract.getTableName() + " as meal2 on meal2._id = meal_ingredient2.meal_id " +
                "               where ingredient2._id = " + IngredientContract.getTableName() + "." + _ID + " and meal2." + _ID + " = ? " +
                "        ) THEN 1 " +
                "        ELSE 0  " +
                "    END as checked  " +
                "  from " + IngredientContract.getTableName() +
                " order by checked desc, " +
                IngredientContract.getTableName() + "." + IngredientContract.COLUMN_NAME + " COLLATE NOCASE";
    }

    @NonNull
    private String getIngredientsSortedQuery() {
        return getIngredientsQuery() + " order by 1 COLLATE NOCASE";
    }

    @NonNull
    private String getIngredientsQuery() {

        return "select " + IngredientContract.getTableName() + "." + IngredientContract.COLUMN_NAME +
                " from " + TABLE_NAME +
                " join " + MealContract.getTableName() +
                " on " + MealContract.getTableName() + "." + _ID + "= " + TABLE_NAME + "." + COLUMN_MEAL_ID +
                " join " + IngredientContract.getTableName() +
                " on " + IngredientContract.getTableName() + "." + _ID + "= " + TABLE_NAME + "." + COLUMN_INGREDIENT_ID +
                " where " + MealContract.getTableName() + "." + _ID + "=? ";
    }

    public boolean insert(CoNaObiadDbHelper dbHelper, long mealId, long ingredientId) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("meal_id", mealId);
        contentValues.put("ingredient_id", ingredientId);

        dbHelper.insert(TABLE_NAME, contentValues);
        return true;
    }


    @NonNull
    MealIngredient getFromCursor(Cursor res) {
        return new MealIngredient(0, 0);
    }

    public List<String> getIngredientsForMeal(long mealId, SQLiteOpenHelper helper) {
        ArrayList<String> ingredients = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.rawQuery(getIngredientsSortedQuery(), new String[]{Long.toString(mealId)});

        if (res != null && res.getCount() > 0) {
            res.moveToFirst();

            do {
                ingredients.add(res.getString(0));
            } while (res.moveToNext());
        }

        db.close();
        return ingredients;
    }

    public Ingredient[] getIngredientsWithMeal(long mealId, SQLiteOpenHelper helper) {
        ArrayList<Ingredient> ingredients = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.rawQuery(getIngredientsWithChecked(), new String[]{Long.toString(mealId)});

        if (res != null && res.getCount() > 0) {
            res.moveToFirst();

            do {
                Long id = res.getLong(res.getColumnIndex(_ID));
                String name = res.getString(res.getColumnIndex(IngredientContract.COLUMN_NAME));
                int checked = res.getInt(res.getColumnIndex(CHECKED_COLUMN_NAME));
                ingredients.add(new Ingredient(id, name, checked == 1));
            } while (res.moveToNext());
        }

        db.close();
        return ingredients.toArray(new Ingredient[ingredients.size()]);
    }

    public void deleteForMeal(CoNaObiadDbHelper helper, long mealId) {
        helper.delete(TABLE_NAME, mealId, "meal_id");
    }

    public String getShoppingList(List<Meal> meals, SQLiteOpenHelper helper) {

        ArrayList<String> ingredients = new ArrayList<>();

        String[] queryArgs = getMealIds(meals);
        String query = getShoppingListQuery(meals);

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.rawQuery(query, queryArgs);

        if (res != null && res.getCount() > 0) {
            res.moveToFirst();

            do {
                ingredients.add(res.getString(0));
            } while (res.moveToNext());
        }

        db.close();
        return TextUtils.join("\n", ingredients);
    }

    @NonNull
    private String[] getMealIds(List<Meal> meals) {
        String[] stringArgs = new String[meals.size()];

        for (int i = 0; i < meals.size(); i++) {
            stringArgs[i] = Long.toString(meals.get(i).getId());
        }
        return stringArgs;
    }

    @NonNull
    private String getShoppingListQuery(List<Meal> meals) {
        StringBuilder sb = new StringBuilder();
        sb.append("select no || ' x ' || name from ( select name, count(name) as no from (");

        for (int i = 0; i < meals.size(); i++) {

            sb.append(getIngredientsQuery());

            if (i < meals.size() - 1) {
                sb.append(" union all ");
            }
        }

        sb.append(") group by name order by name collate nocase);");
        return sb.toString();
    }

    public void delete(Long[] ids, CoNaObiadDbHelper helper) {
        helper.delete(TABLE_NAME, ids);
    }

    public void delete(Long id, CoNaObiadDbHelper helper) {
        helper.delete(TABLE_NAME, id, "_ID");
    }
}
