package com.projects.jezinka.conaobiad.models.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.models.Meal;

import java.util.ArrayList;
import java.util.Random;

public class MealContract extends BaseTable implements BaseColumns {

    public static String tableName = "meal";
    static String columnRecipe = "recipe";

    public static String getCreateEntriesQuery() {
        return "CREATE TABLE " + tableName + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                columnName + " TEXT," +
                columnRecipe + " TEXT" +
                ")";
    }

    @NonNull
    private String getAllRecordsQuery() {
        return "select " + _ID + ", " + columnName + ", " + columnRecipe +
                " from " + tableName +
                " order by " + columnName + " COLLATE NOCASE";
    }

    public boolean insert(CoNaObiadDbHelper helper, String name) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        helper.insert(tableName, contentValues);
        return true;
    }

    public boolean update(CoNaObiadDbHelper helper, String name, long mealId) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        helper.update(tableName, contentValues, mealId);
        return true;
    }

    public ArrayList<Meal> getAllMeals(SQLiteOpenHelper helper) {
        return getArrayList(helper, null, getAllRecordsQuery());
    }

    @NonNull
    Meal getFromCursor(Cursor res) {
        String name = res.getString(res.getColumnIndex(columnName));
        long id = res.getLong(res.getColumnIndex(_ID));
        String recipe = res.getString(res.getColumnIndex(columnRecipe));
        return new Meal(id, name, recipe);
    }

    public Meal[] getAllMealsArray(SQLiteOpenHelper helper) {
        ArrayList<Meal> result = getAllMeals(helper);
        return result.toArray(new Meal[result.size()]);
    }

    public boolean isAnyMealSaved(SQLiteOpenHelper helper) {
        return isAnyRecordSaved(helper, tableName);
    }

    public void delete(Long[] ids, CoNaObiadDbHelper helper) {
        helper.delete(tableName, ids);
    }

    public void delete(Long id, CoNaObiadDbHelper helper) {
        helper.delete(tableName, id, "_ID");
    }

    public ArrayList<Meal> getRandomMeals(SQLiteOpenHelper helper, int size) {
        ArrayList<Meal> mealList = getArrayList(helper, null, getRandomMealsQuery(size));
        if (mealList.size() < size) {
            Random random = new Random();
            while (mealList.size() < size) {
                int randomIndex = random.nextInt(mealList.size());
                mealList.add(mealList.get(randomIndex));
            }
        }
        return mealList;
    }

    private static String getRandomMealsQuery(int size) {
        return "SELECT  " + _ID + ", " + columnName + ", " + columnRecipe + " FROM " + tableName +
                " ORDER BY RANDOM() LIMIT " + size;
    }

    public boolean addRecipe(CoNaObiadDbHelper helper, String recipe, long mealId) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("recipe", recipe);

        helper.update(tableName, contentValues, mealId);
        return true;
    }
}
