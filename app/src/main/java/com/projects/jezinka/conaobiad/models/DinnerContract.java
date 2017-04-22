package com.projects.jezinka.conaobiad.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.utils.TimeUtils;

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
                COLUMN_DATE_NAME + " INT," +
                " FOREIGN KEY(" + COLUMN_MEAL_ID + ") REFERENCES " + this.mealContract.getTableName() + "(" + this.COLUMN_MEAL_ID + ")" +
                ")";

        this.SQL_GET_RECORDS_BY_DATE = "select " + this.TABLE_NAME + "." + this._ID + ", " +
                this.COLUMN_MEAL_ID + ", " +
                this.COLUMN_DATE_NAME + ", " +
                this.mealContract.COLUMN_NAME_NAME +
                " from " + this.TABLE_NAME +
                " join " + this.mealContract.getTableName() +
                " on " + this.TABLE_NAME + "." + this.COLUMN_MEAL_ID + "= " + this.mealContract.getTableName() + "." + this.mealContract._ID +
                " where " + this.COLUMN_DATE_NAME + " between ? and ?" +
                " order by " + this.COLUMN_DATE_NAME;
    }

    public boolean insert(CoNaObiadDbHelper helper, int mealID, Date date) {
        String tableName = this.getTableName();

        ContentValues contentValues = new ContentValues();
        contentValues.put("meal_id", mealID);
        contentValues.put("date", date.getTime());

        helper.insert(tableName, contentValues);
        return true;
    }

    public boolean update(CoNaObiadDbHelper helper, Dinner dinner, Date date) {
        String tableName = this.getTableName();

        ContentValues contentValues = new ContentValues();
        contentValues.put("date", date.getTime());

        helper.update(tableName, contentValues, dinner.getId());
        return true;
    }

    private ArrayList<Dinner> getDinnersByDate(SQLiteOpenHelper helper, Date date) {

        ArrayList<Dinner> array_list = new ArrayList<>();
        long weekStartDate = TimeUtils.getWeekStartDate(date).getTime();
        String[] sqlArgs = {String.valueOf(weekStartDate), String.valueOf(weekStartDate + TimeUtils.getTimeDeltaMilliseconds())};

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

    public Dinner[] getDinners(SQLiteOpenHelper helper) {
        return getDinnersByDateArray(helper, new Date());
    }

    private Dinner[] getDinnersByDateArray(SQLiteOpenHelper helper, Date date) {
        ArrayList<Dinner> result = getDinnersByDate(helper, date);
        return result.toArray(new Dinner[result.size()]);
    }
}
