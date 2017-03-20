package com.projects.jezinka.conaobiad.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

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

        this.SQL_GET_ALL_RECORD = "select " + this.COLUMN_NAME_NAME + " from " + this.TABLE_NAME + " order by " + this.COLUMN_NAME_NAME;
    }

    public boolean insertMeal(Context context, String name) {
        String tableName = this.getTableName();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        CoNaObiadDbHelper helper = new CoNaObiadDbHelper(context);
        helper.insertValuesDbHelper(tableName, contentValues);
        return true;
    }

    public ArrayList<String> getAllMeals(SQLiteOpenHelper helper) {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.rawQuery(this.SQL_GET_ALL_RECORD, null);
        if (res != null && res.getCount() > 0) {
            res.moveToFirst();

            do {
                String mealName = res.getString(res.getColumnIndex(this.COLUMN_NAME_NAME));
                array_list.add(mealName);
            } while (res.moveToNext());
        }

        db.close();

        return array_list;
    }

    public boolean isAnyMealSaved(SQLiteOpenHelper helper) {
        SQLiteDatabase db = helper.getReadableDatabase();
        return queryNumEntries(db, "meal") > 0;
    }
}
