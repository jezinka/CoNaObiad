package com.projects.jezinka.conaobiad.model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.projects.jezinka.conaobiad.CoNaObiadDbHelper;

import java.util.Date;

/**
 * Created by jezinka on 11.03.17.
 */

public class DinnerContract extends BaseTable implements BaseColumns {

    String COLUMN_DATE_NAME;
    String COLUMN_MEAL_ID;

    public DinnerContract() {
        this.TABLE_NAME = "dinner";
        this.COLUMN_MEAL_ID = "meal_id";
        this.COLUMN_DATE_NAME = "date";

        this.SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_MEAL_ID + " INT, " +
                COLUMN_DATE_NAME + " INT)"; // System.currentTimeMillis()
    }

    public boolean insertDinner(CoNaObiadDbHelper coNaObiadDbHelper, int mealID, Date date) {
        SQLiteDatabase db = coNaObiadDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_MEAL_ID, mealID);
        contentValues.put(COLUMN_DATE_NAME, date.getTime());
        db.insert(this.TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }
}
