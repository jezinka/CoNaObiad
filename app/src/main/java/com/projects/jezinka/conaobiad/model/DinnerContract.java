package com.projects.jezinka.conaobiad.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.projects.jezinka.conaobiad.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.TimeUtils;

import java.util.ArrayList;
import java.util.Date;

public class DinnerContract extends BaseTable implements BaseColumns {

    String COLUMN_DATE_NAME;
    String COLUMN_MEAL_ID;

    MealContract mealContract;

    private String SQL_GET_RECORDS_BY_DATE;

    public DinnerContract() {
        this.TABLE_NAME = "dinner";
        this.COLUMN_MEAL_ID = "meal_id";
        this.COLUMN_DATE_NAME = "date";

        this.mealContract = new MealContract();

        this.SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_MEAL_ID + " INT, " +
                COLUMN_DATE_NAME + " INT)"; // System.currentTimeMillis()

        this.SQL_GET_RECORDS_BY_DATE = "select " + this.TABLE_NAME + "." + this._ID + ", " +
                this.COLUMN_MEAL_ID + ", " +
                this.COLUMN_DATE_NAME + ", " +
                this.mealContract.COLUMN_NAME_NAME +
                " from " + this.TABLE_NAME +
                " join " + this.mealContract.TABLE_NAME +
                " on " + this.TABLE_NAME + "." + this.COLUMN_MEAL_ID + "= " + this.mealContract.TABLE_NAME + "." + this.mealContract._ID +
                " where " + this.COLUMN_DATE_NAME + " between ? and ?" +
                " order by " + this.COLUMN_DATE_NAME;
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

    public ArrayList<Dinner> getDinnersByDate(SQLiteOpenHelper helper, Date date) {

        ArrayList<Dinner> array_list = new ArrayList<>();
        long saturdayDate = TimeUtils.getSaturdayDate(date).getTime();
        String[] sqlArgs = {String.valueOf(saturdayDate), String.valueOf(saturdayDate + TimeUtils.SEVEN_DAYS_IN_MILLISECONDS)};

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.rawQuery(this.SQL_GET_RECORDS_BY_DATE, sqlArgs);
        if (res != null && res.getCount() > 0) {
            res.moveToFirst();

            do {
                int id = res.getInt(res.getColumnIndex(_ID));
                int mealId = res.getInt(res.getColumnIndex(this.COLUMN_MEAL_ID));
                String mealName = res.getString(res.getColumnIndex(this.mealContract.COLUMN_NAME_NAME));
                Date dinnerDate = new Date(res.getLong(res.getColumnIndex(this.COLUMN_DATE_NAME)));
                Meal meal = new Meal(mealId, mealName);
                array_list.add(new Dinner(id, meal, dinnerDate));
            } while (res.moveToNext());
        }

        db.close();

        return array_list;
    }

    public Dinner[] getDinnersByDateArray(SQLiteOpenHelper helper, Date date) {
        ArrayList<Dinner> result = getDinnersByDate(helper, date);
        return result.toArray(new Dinner[result.size()]);
    }
}
