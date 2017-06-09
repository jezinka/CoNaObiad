package com.projects.jezinka.conaobiad.models.tableDefinitions;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.models.Dinner;
import com.projects.jezinka.conaobiad.models.Meal;
import com.projects.jezinka.conaobiad.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Date;

public class DinnerContract extends BaseTable implements BaseColumns {

    public static String tableName = "dinner";

    public static String columnDate = "date";
    private static String columnMealId = "meal_id";

    public static String getCreateEntriesQuery() {
        return "CREATE TABLE " + tableName + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                columnMealId + " INT, " +
                columnDate + " INT," +
                " FOREIGN KEY(" + columnMealId + ") REFERENCES " + MealContract.tableName + "(" + _ID + ") ON DELETE CASCADE " +
                ")";
    }

    public boolean insert(CoNaObiadDbHelper helper, long mealID, Date date) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("meal_id", mealID);
        contentValues.put("date", date.getTime());

        helper.insert(tableName, contentValues);
        return true;
    }

    public boolean update(CoNaObiadDbHelper helper, long dinnerId, Date date) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("date", date.getTime());

        helper.update(tableName, contentValues, dinnerId);
        return true;
    }

    @NonNull
    private ArrayList<Dinner> getDinnersForActualWeek(SQLiteOpenHelper helper, Date date) {

        long weekStartDate = TimeUtils.getWeekStartDate(date).getTime();
        String[] sqlArgs = {String.valueOf(weekStartDate), String.valueOf(weekStartDate + TimeUtils.getTimeDeltaMilliseconds())};

        return getArrayList(helper, sqlArgs, getRecordsByDateSql(" where " + columnDate + " between ? and ?"));
    }

    @NonNull
    private ArrayList<Dinner> getDinnersForDay(SQLiteOpenHelper helper, Date date) {

        long weekStartDate = date.getTime();
        String[] sqlArgs = {String.valueOf(weekStartDate)};

        return getArrayList(helper, sqlArgs, getRecordsByDateSql(" where " + columnDate + " = ? "));
    }

    @NonNull
    Dinner getFromCursor(Cursor res) {
        long id = res.getLong(res.getColumnIndex(_ID));
        long mealId = res.getLong(res.getColumnIndex(columnMealId));
        String mealName = res.getString(res.getColumnIndex(MealContract.columnName));
        String recipe = res.getString(res.getColumnIndex(MealContract.columnRecipe));
        Date dinnerDate = new Date(res.getLong(res.getColumnIndex(columnDate)));
        Meal meal = new Meal(mealId, mealName, recipe);
        return new Dinner(id, meal, dinnerDate);
    }

    public Dinner[] getDinnersForActualWeek(SQLiteOpenHelper helper) {
        ArrayList<Dinner> result = getDinnersForActualWeek(helper, new Date());
        return result.toArray(new Dinner[result.size()]);
    }

    public Dinner[] getDinners(SQLiteOpenHelper helper, Date date) {
        ArrayList<Dinner> result = getDinnersForDay(helper, date);
        return result.toArray(new Dinner[result.size()]);
    }

    private String getRecordsByDateSql(String whereClause) {
        return "select " + tableName + "." + _ID + ", " +
                columnMealId + ", " +
                columnDate + ", " +
                MealContract.columnName + ", " +
                MealContract.columnRecipe +
                " from " + tableName +
                " join " + MealContract.tableName +
                " on " + tableName + "." + columnMealId + "= " + MealContract.tableName + "." + _ID +
                whereClause +
                " order by " + columnDate;
    }

    public void delete(Long[] ids, CoNaObiadDbHelper helper) {
        helper.delete(tableName, ids);
    }

    public void delete(Long id, String columnName, CoNaObiadDbHelper helper) {
        helper.delete(tableName, id, columnName);
    }
}
