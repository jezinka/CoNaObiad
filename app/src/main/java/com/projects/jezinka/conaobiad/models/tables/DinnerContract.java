package com.projects.jezinka.conaobiad.models.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.models.Dinner;
import com.projects.jezinka.conaobiad.models.Meal;
import com.projects.jezinka.conaobiad.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class DinnerContract extends BaseTable implements BaseColumns {

    private static final String TABLE_NAME = "dinner";

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_MEAL_ID = "meal_id";

    public static String getCreateEntriesQuery() {
        return "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_MEAL_ID + " INT, " +
                COLUMN_DATE + " INT," +
                " FOREIGN KEY(" + COLUMN_MEAL_ID + ") REFERENCES " + MealContract.getTableName() + "(" + _ID + ") ON DELETE CASCADE " +
                ")";
    }

    public boolean insert(CoNaObiadDbHelper helper, long mealID, Date date) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("meal_id", mealID);
        contentValues.put("date", date.getTime());

        helper.insert(TABLE_NAME, contentValues);
        return true;
    }

    public boolean update(CoNaObiadDbHelper helper, long dinnerId, Date date) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("date", date.getTime());

        helper.update(TABLE_NAME, contentValues, dinnerId);
        return true;
    }

    @NonNull
    private ArrayList<Dinner> getDinnersForActualWeek(SQLiteOpenHelper helper, Date date) {

        long weekStartDate = TimeUtils.getWeekStartDate(date).getTime();
        String[] sqlArgs = {String.valueOf(weekStartDate), String.valueOf(weekStartDate + TimeUtils.getTimeDeltaMilliseconds())};

        return getArrayList(helper, sqlArgs, getRecordsByDateSql(" where " + COLUMN_DATE + " between ? and ?"));
    }

    @NonNull
    private ArrayList<Dinner> getDinnersForDay(SQLiteOpenHelper helper, Date date) {

        long weekStartDate = date.getTime();
        String[] sqlArgs = {String.valueOf(weekStartDate)};

        return getArrayList(helper, sqlArgs, getRecordsByDateSql(" where " + COLUMN_DATE + " = ? "));
    }

    @NonNull
    Dinner getFromCursor(Cursor res) {
        long id = res.getLong(res.getColumnIndex(_ID));
        long mealId = res.getLong(res.getColumnIndex(COLUMN_MEAL_ID));
        String mealName = res.getString(res.getColumnIndex(MealContract.COLUMN_NAME));
        String recipe = res.getString(res.getColumnIndex(MealContract.columnRecipe));
        Date dinnerDate = new Date(res.getLong(res.getColumnIndex(COLUMN_DATE)));
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
        return "select " + TABLE_NAME + "." + _ID + ", " +
                COLUMN_MEAL_ID + ", " +
                COLUMN_DATE + ", " +
                MealContract.COLUMN_NAME + ", " +
                MealContract.columnRecipe +
                " from " + TABLE_NAME +
                " join " + MealContract.getTableName() +
                " on " + TABLE_NAME + "." + COLUMN_MEAL_ID + "= " + MealContract.getTableName() + "." + _ID +
                whereClause +
                " order by " + COLUMN_DATE;
    }

    public void delete(Long[] ids, CoNaObiadDbHelper helper) {
        helper.delete(TABLE_NAME, ids);
    }

    public void delete(Long id, String columnName, CoNaObiadDbHelper helper) {
        helper.delete(TABLE_NAME, id, columnName);
    }

    private String getCountDinnersQuery(String whereClause) {
        String mealName = MealContract.COLUMN_NAME;
        return "select " + mealName + ", count(" + mealName + ") as quantity " +
                "from " + TABLE_NAME
                + " join " + MealContract.getTableName()
                + " on " + TABLE_NAME + "." + COLUMN_MEAL_ID + "= " + MealContract.getTableName() + "." + _ID
                + whereClause
                + " group by " + mealName
                + " order by 2";
    }

    public Map getDinnerStatistics(String whereClause, CoNaObiadDbHelper helper) {
        LinkedHashMap<String, Long> mealsCount = new LinkedHashMap<>();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.rawQuery(getCountDinnersQuery(whereClause), null);
        if (res != null && res.getCount() > 0) {
            res.moveToFirst();

            do {
                String name = res.getString(0);
                long count = res.getLong(1);
                mealsCount.put(name, count);
            } while (res.moveToNext());
        }

        db.close();
        return mealsCount;
    }

}
