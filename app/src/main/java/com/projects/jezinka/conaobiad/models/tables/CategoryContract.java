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

    private static final String tableName = "category";

    public static String getTableName() {
        return tableName;
    }

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

    public boolean update(CoNaObiadDbHelper dbHelper, String name, long categoryId) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        dbHelper.update(tableName, contentValues, categoryId);
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
        return "select " + _ID + ", " + columnName +
                " from " + tableName +
                " order by " + columnName + " COLLATE NOCASE";
    }

    @NonNull
    Category getFromCursor(Cursor res) {
        String name = res.getString(res.getColumnIndex(columnName));
        long id = res.getLong(res.getColumnIndex(_ID));
        return new Category(id, name);
    }

    public void delete(Long[] ids, CoNaObiadDbHelper helper) {
        helper.delete(tableName, ids);
    }

    public boolean isAnyCategorySaved(SQLiteOpenHelper helper) {
        return isAnyRecordSaved(helper, tableName);
    }
}
