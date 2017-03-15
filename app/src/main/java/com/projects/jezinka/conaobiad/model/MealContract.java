package com.projects.jezinka.conaobiad.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.projects.jezinka.conaobiad.CoNaObiadDbHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jezinka on 11.03.17.
 */

public class MealContract extends BaseTable implements BaseColumns {

    String COLUMN_NAME_NAME;

    public MealContract() {
        this.TABLE_NAME = "meal";
        this.COLUMN_NAME_NAME = "name";
        this.SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_NAME + " TEXT)";
    }

    public boolean insertMeal(CoNaObiadDbHelper coNaObiadDbHelper, String name) {
        SQLiteDatabase db = coNaObiadDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        db.insert(this.TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public ArrayList<String> getAllMeals(SQLiteOpenHelper sqLiteOpenHelper) {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + this.TABLE_NAME, null);
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

    public void initializeMealTable(CoNaObiadDbHelper coNaObiadDbHelper) {
        List<String> meals = Arrays.asList("bigos", "rosół", "zapiekanka", "pierogi", "pomidorowa", "pesto", "meksykański ryż czerwony");
        for (int i = 0; i < meals.size(); i++) {
            insertMeal(coNaObiadDbHelper, meals.get(i));
        }
    }

    public boolean isAnyMealSaved(SQLiteOpenHelper sqLiteOpenHelper) {
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, "meal");
        return count > 0;
    }
}
