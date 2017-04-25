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

    String COLUMN_NAME;
    private String SQL_GET_ALL_RECORD;

    public MealContract() {
        this.TABLE_NAME = "meal";
        this.COLUMN_NAME = "name";

        this.SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME + " TEXT)";

        this.SQL_GET_ALL_RECORD = "select " + _ID + ", " + this.COLUMN_NAME +
                " from " + this.TABLE_NAME +
                " order by " + this.COLUMN_NAME + " COLLATE NOCASE";
    }

    public boolean insert(CoNaObiadDbHelper helper, String name) {
        String tableName = this.getTableName();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        helper.insert(tableName, contentValues);
        return true;
    }

    public boolean update(CoNaObiadDbHelper helper, String name, Meal meal) {
        String tableName = this.getTableName();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        helper.update(tableName, contentValues, meal.getId());
        return true;
    }

    public ArrayList<Meal> getAllMeals(SQLiteOpenHelper helper) {
        return getArrayList(helper, null, this.SQL_GET_ALL_RECORD);
    }

    @NonNull
    Meal getFromCursor(Cursor res) {
        String name = res.getString(res.getColumnIndex(this.COLUMN_NAME));
        int id = res.getInt(res.getColumnIndex(_ID));
        return new Meal(id, name);
    }

    public Meal[] getAllMealsArray(SQLiteOpenHelper helper) {
        ArrayList<Meal> result = getAllMeals(helper);
        return result.toArray(new Meal[result.size()]);
    }

    public boolean isAnyMealSaved(SQLiteOpenHelper helper) {
        SQLiteDatabase db = helper.getReadableDatabase();
        return queryNumEntries(db, this.getTableName()) > 0;
    }
}
