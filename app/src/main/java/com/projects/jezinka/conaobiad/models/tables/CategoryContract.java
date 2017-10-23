package com.projects.jezinka.conaobiad.models.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.models.Category;

import java.util.ArrayList;

public class CategoryContract extends BaseTable implements BaseColumns {

    private static final String TABLE_NAME = "category";

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

    public boolean update(CoNaObiadDbHelper dbHelper, String name, long categoryId) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        dbHelper.update(TABLE_NAME, contentValues, categoryId);
        return true;
    }

    public Category[] getAllCategoriesArray(SQLiteOpenHelper helper) {
        ArrayList<Category> result = getAllCategories(helper);
        return result.toArray(new Category[result.size()]);
    }

    @NonNull
    private ArrayList<Category> getAllCategories(SQLiteOpenHelper helper) {
        return getArrayList(helper, null, getAllRecordQuery());
    }

    @NonNull
    private String getAllRecordQuery() {
        return "select " + _ID + ", " + COLUMN_NAME +
                " from " + TABLE_NAME +
                " order by " + COLUMN_NAME + " COLLATE NOCASE";
    }

    @NonNull
    Category getFromCursor(Cursor res) {
        String name = res.getString(res.getColumnIndex(COLUMN_NAME));
        long id = res.getLong(res.getColumnIndex(_ID));
        return new Category(id, name);
    }

    public void delete(Long[] ids, CoNaObiadDbHelper helper) {
        helper.delete(TABLE_NAME, ids);
    }

    public boolean isAnyCategorySaved(SQLiteOpenHelper helper) {
        return isAnyRecordSaved(helper, TABLE_NAME);
    }
}
