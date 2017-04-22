package com.projects.jezinka.conaobiad.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;

import java.util.ArrayList;

import static android.database.DatabaseUtils.queryNumEntries;

public class MealContract extends BaseTable implements BaseColumns {

    String COLUMN_NAME_NAME;
    private String SQL_GET_ALL_RECORD;

    public MealContract() {
        this.TABLE_NAME = "meal";
        this.COLUMN_NAME_NAME = "name";

        this.SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_NAME + " TEXT)";

        this.SQL_GET_ALL_RECORD = "select " + this._ID + ", " + this.COLUMN_NAME_NAME +
                " from " + this.TABLE_NAME +
                " order by " + this.COLUMN_NAME_NAME + " COLLATE NOCASE";
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
        ArrayList<Meal> array_list = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.rawQuery(this.SQL_GET_ALL_RECORD, null);
        if (res != null && res.getCount() > 0) {
            res.moveToFirst();

            do {
                String name = res.getString(res.getColumnIndex(this.COLUMN_NAME_NAME));
                int id = res.getInt(res.getColumnIndex(this._ID));
                array_list.add(new Meal(id, name));
            } while (res.moveToNext());
        }

        db.close();

        return array_list;
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
