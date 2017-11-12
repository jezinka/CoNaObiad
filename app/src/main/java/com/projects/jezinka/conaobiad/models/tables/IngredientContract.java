package com.projects.jezinka.conaobiad.models.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.models.Ingredient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class IngredientContract extends BaseTable implements BaseColumns {

    private static final String TABLE_NAME = "ingredient";
    private static final String JOIN = " join ";
    private static final String ON = " on ";

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getCreateEntriesQuery() {
        return "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME + " TEXT)";
    }

    public long insert(CoNaObiadDbHelper dbHelper, String name) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        return dbHelper.insert(TABLE_NAME, contentValues);
    }

    public boolean update(CoNaObiadDbHelper dbHelper, String name, long ingredientId) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        dbHelper.update(TABLE_NAME, contentValues, ingredientId);
        return true;
    }

    public Ingredient[] getAllIngredientsArray(SQLiteOpenHelper helper) {
        ArrayList<Ingredient> result = getAllIngredients(helper);
        return result.toArray(new Ingredient[result.size()]);
    }

    @NonNull
    private ArrayList<Ingredient> getAllIngredients(SQLiteOpenHelper helper) {
        return getArrayList(helper, null, getAllRecordQuery());
    }

    @NonNull
    private String getAllRecordQuery() {
        return "select " + _ID + ", " + COLUMN_NAME +
                " from " + TABLE_NAME +
                " order by " + COLUMN_NAME + " COLLATE NOCASE";
    }

    @NonNull
    Ingredient getFromCursor(Cursor res) {
        String name = res.getString(res.getColumnIndex(COLUMN_NAME));
        long id = res.getLong(res.getColumnIndex(_ID));
        return new Ingredient(id, name);
    }

    public void delete(Long[] ids, CoNaObiadDbHelper helper) {
        helper.delete(TABLE_NAME, ids);
    }

    public void delete(Long id, CoNaObiadDbHelper helper) {
        helper.delete(TABLE_NAME, id, "_ID");
    }

    public boolean isAnyIngredientSaved(SQLiteOpenHelper helper) {
        return isAnyRecordSaved(helper, TABLE_NAME);
    }

    private String getCountIngredientsQuery(String whereClause) {
        String ingredientName = TABLE_NAME + '.' + COLUMN_NAME;
        return "select " + ingredientName + ", count(" + ingredientName + ") as quantity " +
                "from " + TABLE_NAME
                + JOIN + MealIngredientContract.getTableName()
                + ON + TABLE_NAME + "." + _ID + "= " + MealIngredientContract.getTableName() + "." + MealIngredientContract.COLUMN_INGREDIENT_ID
                + JOIN + MealContract.getTableName()
                + ON + MealIngredientContract.getTableName() + "." + MealIngredientContract.COLUMN_MEAL_ID + "= " + MealContract.getTableName() + "." + _ID
                + JOIN + DinnerContract.getTableName()
                + ON + DinnerContract.getTableName() + "." + DinnerContract.COLUMN_MEAL_ID + "= " + MealContract.getTableName() + "." + _ID
                + whereClause
                + " group by " + ingredientName
                + " order by 2";
    }

    public Map getIngredientsStatistics(String whereClause, CoNaObiadDbHelper helper) {
        LinkedHashMap<String, Long> ingredientsCount = new LinkedHashMap<>();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.rawQuery(getCountIngredientsQuery(whereClause), null);
        if (res != null && res.getCount() > 0) {
            res.moveToFirst();

            do {
                String name = res.getString(0);
                long count = res.getLong(1);
                ingredientsCount.put(name, count);
            } while (res.moveToNext());
        }

        db.close();
        return ingredientsCount;
    }
}

