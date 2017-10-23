package com.projects.jezinka.conaobiad.models.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.models.Meal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MealContract extends BaseTable implements BaseColumns {

    private static final String TABLE_NAME = "meal";

    public static String getTableName() {
        return TABLE_NAME;
    }

    static String columnRecipe = "recipe";

    public static String getCreateEntriesQuery() {
        return "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME + " TEXT," +
                columnRecipe + " TEXT" +
                ")";
    }

    @NonNull
    private String getAllRecordsQuery() {
        return "select " + _ID + ", " + COLUMN_NAME + ", " + columnRecipe +
                " from " + TABLE_NAME +
                " order by " + COLUMN_NAME + " COLLATE NOCASE";
    }

    public boolean insert(CoNaObiadDbHelper helper, String name) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        helper.insert(TABLE_NAME, contentValues);
        return true;
    }

    public boolean update(CoNaObiadDbHelper helper, String name, long mealId) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        helper.update(TABLE_NAME, contentValues, mealId);
        return true;
    }

    public List getAllMeals(SQLiteOpenHelper helper) {
        return getArrayList(helper, null, getAllRecordsQuery());
    }

    @NonNull
    Meal getFromCursor(Cursor res) {
        String name = res.getString(res.getColumnIndex(COLUMN_NAME));
        long id = res.getLong(res.getColumnIndex(_ID));
        String recipe = res.getString(res.getColumnIndex(columnRecipe));
        return new Meal(id, name, recipe);
    }

    public Meal[] getAllMealsArray(SQLiteOpenHelper helper) {
        List<Meal> result = getAllMeals(helper);
        return result.toArray(new Meal[result.size()]);
    }

    public boolean isAnyMealSaved(SQLiteOpenHelper helper) {
        return isAnyRecordSaved(helper, TABLE_NAME);
    }

    public void delete(Long[] ids, CoNaObiadDbHelper helper) {
        helper.delete(TABLE_NAME, ids);
    }

    public void delete(Long id, CoNaObiadDbHelper helper) {
        helper.delete(TABLE_NAME, id, "_ID");
    }

    public List getRandomMeals(SQLiteOpenHelper helper, int size) {
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
        return "SELECT  " + _ID + ", " + COLUMN_NAME + ", " + columnRecipe + " FROM " + TABLE_NAME +
                " ORDER BY RANDOM() LIMIT " + size;
    }

    public boolean addRecipe(CoNaObiadDbHelper helper, String recipe, long mealId) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("recipe", recipe);

        helper.update(TABLE_NAME, contentValues, mealId);
        return true;
    }
}
