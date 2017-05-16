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
    public static String columnName = "name";

    @NonNull
    private String getAllRecordsQuery() {
        return "select " + _ID + ", " + columnName +
                " from " + tableName +
                " order by " + columnName + " COLLATE NOCASE";
    }

    public boolean insert(CoNaObiadDbHelper helper, String name) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        helper.insert(tableName, contentValues);
        return true;
    }

    public boolean update(CoNaObiadDbHelper helper, String name, Meal meal) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        helper.update(tableName, contentValues, meal.getId());
        return true;
    }

    public ArrayList<Meal> getAllMeals(SQLiteOpenHelper helper) {
        return getArrayList(helper, null, getAllRecordsQuery());
    }

    public static String getCreateEntriesQuery() {
        return "CREATE TABLE " + tableName + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                columnName + " TEXT)";
    }

    @NonNull
    Meal getFromCursor(Cursor res) {
        String name = res.getString(res.getColumnIndex(columnName));
        int id = res.getInt(res.getColumnIndex(_ID));
        return new Meal(id, name);
    }

    public Meal[] getAllMealsArray(SQLiteOpenHelper helper) {
        ArrayList<Meal> result = getAllMeals(helper);
        return result.toArray(new Meal[result.size()]);
    }

    public boolean isAnyMealSaved(SQLiteOpenHelper helper) {
        SQLiteDatabase db = helper.getReadableDatabase();
        return queryNumEntries(db, tableName) > 0;
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
