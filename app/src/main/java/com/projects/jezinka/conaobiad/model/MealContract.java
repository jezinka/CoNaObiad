package com.projects.jezinka.conaobiad.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.projects.jezinka.conaobiad.CoNaObiadDbHelper;

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

        this.SQL_GET_ALL_RECORD = "select " + this._ID + ", " + this.COLUMN_NAME_NAME + " from " + this.TABLE_NAME + " order by " + this.COLUMN_NAME_NAME;
    }

    public boolean insertMeal(Context context, String name) {
        String tableName = this.getTableName();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        CoNaObiadDbHelper helper = new CoNaObiadDbHelper(context);
        helper.insertValuesDbHelper(tableName, contentValues);
        return true;
    }

    public boolean updateMeal(Context context, String name, Meal meal) {
        String tableName = this.getTableName();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        CoNaObiadDbHelper helper = new CoNaObiadDbHelper(context);
        helper.updateValuesDbHelper(tableName, contentValues, meal);
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
        return queryNumEntries(db, "meal") > 0;
    }

    public void deleteMeals(Long[] mealIds, SQLiteOpenHelper helper) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String query = "Delete from " + this.getTableName() + " where " + this._ID + " in (" + TextUtils.join(",", mealIds) + ")";
        db.execSQL(query);
    }
}
