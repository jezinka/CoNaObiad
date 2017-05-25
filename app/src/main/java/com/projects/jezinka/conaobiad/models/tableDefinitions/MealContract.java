package com.projects.jezinka.conaobiad.models.tableDefinitions;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.models.Meal;

import java.util.ArrayList;

import static android.database.DatabaseUtils.queryNumEntries;

public class MealContract extends BaseTable implements BaseColumns {

    public static String tableName = "meal";
    static String columnName = "name";
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
        SQLiteDatabase db = helper.getReadableDatabase();
        return queryNumEntries(db, tableName) > 0;
    }

    public void delete(Long[] ids, CoNaObiadDbHelper helper) {
        helper.delete(tableName, ids);
    }

    public void delete(Long id, CoNaObiadDbHelper helper) {
        helper.delete(tableName, id, "_ID");
    }

    public ArrayList<Meal> getRandomMeals(SQLiteOpenHelper helper, int size) {
        return getArrayList(helper, null, getRandomMealsQuery(size));
    }

    private static String getRandomMealsQuery(int size) {
        return "SELECT  " + _ID + ", " + columnName + " FROM " + tableName +
                " ORDER BY RANDOM() LIMIT " + size;
    }

    public boolean addRecipe(CoNaObiadDbHelper helper, String recipe, long mealId) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("recipe", recipe);

        helper.update(tableName, contentValues, mealId);
        return true;
    }
}
