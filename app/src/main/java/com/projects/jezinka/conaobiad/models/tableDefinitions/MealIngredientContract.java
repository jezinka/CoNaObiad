package com.projects.jezinka.conaobiad.models.tableDefinitions;

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

    public static String tableName = "meal_ingredient";

    public static String columnMealId = "meal_id";
    public static String columnIngredientId = "ingredient_id";

    @NonNull
    private String getIngredientsWithChecked() {
        return "select ingredient._id, ingredient.name, " +
                "  CASE WHEN EXISTS( " +
                "             SELECT 1 from ingredient as ingredient2    " +
                "               join meal_ingredient as meal_ingredient2  on ingredient2._id= meal_ingredient2.ingredient_id     " +
                "               join meal as meal2 on meal2._id= meal_ingredient2.meal_id     " +
                "               where ingredient2._id = ingredient._id and meal2._id = ? " +
                "        ) THEN 1 " +
                "        ELSE 0  " +
                "    END as checked  " +
                "  from ingredient " +
                " order by checked desc, ingredient.name COLLATE NOCASE";
    }

    @NonNull
    private String getIngredientsSortedQuery() {
        return getIngredientsQuery() + " order by 1 COLLATE NOCASE";
    }

    @NonNull
    private String getIngredientsQuery() {

        return "select " + IngredientContract.tableName + "." + IngredientContract.columnName +
                " from " + tableName +
                " join " + MealContract.tableName +
                " on " + MealContract.tableName + "." + _ID + "= " + tableName + "." + columnMealId +
                " join " + IngredientContract.tableName +
                " on " + IngredientContract.tableName + "." + _ID + "= " + tableName + "." + columnIngredientId +
                " where " + MealContract.tableName + "." + _ID + "=? ";
    }

    public boolean insert(CoNaObiadDbHelper dbHelper, long mealId, long ingredientId) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("meal_id", mealId);
        contentValues.put("ingredient_id", ingredientId);

        dbHelper.insert(tableName, contentValues);
        return true;
    }

    public static String getCreateEntriesQuery() {
        return "CREATE TABLE " + tableName + " (" +
                columnIngredientId + " int, " +
                columnMealId + " int, " +
                " FOREIGN KEY(" + columnMealId + ") REFERENCES " + MealContract.tableName + "(" + _ID + ") ON DELETE CASCADE," +
                " FOREIGN KEY(" + columnIngredientId + ") REFERENCES " + IngredientContract.tableName + "(" + _ID + ") ON DELETE CASCADE" +
                ")";
    }

    @NonNull
    MealIngredient getFromCursor(Cursor res) {
        return new MealIngredient(0, 0);
    }

    public List<String> getIngredientsForMeal(Meal meal, SQLiteOpenHelper helper) {
        ArrayList<String> array_list = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.rawQuery(getIngredientsSortedQuery(), new String[]{Long.toString(meal.getId())});

        if (res != null && res.getCount() > 0) {
            res.moveToFirst();

            do {
                array_list.add(res.getString(0));
            } while (res.moveToNext());
        }

        db.close();
        return array_list;
    }

    public Ingredient[] getIngredientsWithMeal(Meal meal, SQLiteOpenHelper helper) {
        ArrayList<Ingredient> array_list = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.rawQuery(getIngredientsWithChecked(), new String[]{Long.toString(meal.getId())});

        if (res != null && res.getCount() > 0) {
            res.moveToFirst();

            do {
                int id = res.getInt(0);
                String name = res.getString(1);
                int checked = res.getInt(2);
                array_list.add(new Ingredient(id, name, checked == 1));
            } while (res.moveToNext());
        }

        db.close();
        return array_list.toArray(new Ingredient[array_list.size()]);
    }

    public void deleteForMeal(CoNaObiadDbHelper helper, long mealId) {
        helper.delete(tableName, mealId, "meal_id");
    }

    public String getShoppingList(List<Meal> meals, SQLiteOpenHelper helper) {

        ArrayList<String> array_list = new ArrayList<>();

        String[] queryArgs = getMealIds(meals);
        String query = getShoppingListQuery(meals);

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.rawQuery(query, queryArgs);

        if (res != null && res.getCount() > 0) {
            res.moveToFirst();

            do {
                String name = res.getString(0);
                int count = res.getInt(1);
                array_list.add(count + " x " + name + "\n");
            } while (res.moveToNext());
        }

        db.close();
        return TextUtils.join("\n", array_list);
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
        StringBuffer sb = new StringBuffer();
        sb.append("select name, count(name) from (");

        for (int i = 0; i < meals.size(); i++) {

            sb.append(getIngredientsQuery());

            if (i < meals.size() - 1) {
                sb.append(" union all ");
            }
        }

        sb.append(") group by name order by name collate nocase;");
        return sb.toString();
    }

    public static String getDropTableQuery() {
        return "DROP TABLE IF EXISTS " + tableName;
    }

    public void delete(Long[] ids, CoNaObiadDbHelper helper) {
        helper.delete(tableName, ids);
    }

    public void delete(Long id, CoNaObiadDbHelper helper) {
        helper.delete(tableName, id, "_ID");
    }
}
