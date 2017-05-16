package com.projects.jezinka.conaobiad.models.tableDefinitions;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.models.Ingredient;

import java.util.ArrayList;

public class IngredientContract extends BaseTable implements BaseColumns {

    public static String tableName = "ingredient";
    static String columnName = "name";

    public static String getCreateEntriesQuery() {
        return "CREATE TABLE " + tableName + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                columnName + " TEXT)";
    }

    public long insert(CoNaObiadDbHelper dbHelper, String name) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        return dbHelper.insert(tableName, contentValues);
    }

    public boolean update(CoNaObiadDbHelper dbHelper, String name, Ingredient ingredient) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        dbHelper.update(tableName, contentValues, ingredient.getId());
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
        return "select " + _ID + ", " + columnName +
                " from " + tableName +
                " order by " + columnName + " COLLATE NOCASE";
    }

    @NonNull
    Ingredient getFromCursor(Cursor res) {
        String name = res.getString(res.getColumnIndex(columnName));
        long id = res.getLong(res.getColumnIndex(_ID));
        return new Ingredient(id, name);
    }

    public void delete(Long[] ids, CoNaObiadDbHelper helper) {
        helper.delete(tableName, ids);
    }

    public void delete(Long id, CoNaObiadDbHelper helper) {
        helper.delete(tableName, id, "_ID");
    }
}
