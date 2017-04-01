package com.projects.jezinka.conaobiad.model;

import android.content.ContentValues;
import android.content.Context;
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

    public boolean insertDinner(Context context, int mealID, Date date) {
        String tableName = this.getTableName();

        ContentValues contentValues = new ContentValues();
        contentValues.put("meal_id", mealID);
        contentValues.put("date", date.getTime());

        CoNaObiadDbHelper helper = new CoNaObiadDbHelper(context);
        helper.insertValuesDbHelper(tableName, contentValues);
        return true;
    }

    public boolean updateDinner(Context context, Dinner dinner) {
        String tableName = this.getTableName();

        ContentValues contentValues = new ContentValues();
        contentValues.put("meal_id", dinner.getMeal().getId());
        contentValues.put("date", dinner.getDate().getTime());

        CoNaObiadDbHelper helper = new CoNaObiadDbHelper(context);
        helper.updateValuesDbHelper(tableName, contentValues, dinner.getId());
        return true;
    }

}
