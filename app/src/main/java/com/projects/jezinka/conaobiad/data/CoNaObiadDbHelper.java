package com.projects.jezinka.conaobiad.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.projects.jezinka.conaobiad.models.tableDefinitions.DinnerContract;
import com.projects.jezinka.conaobiad.models.tableDefinitions.IngredientContract;
import com.projects.jezinka.conaobiad.models.tableDefinitions.MealContract;

public class CoNaObiadDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "CoNaObiad.db";

    public CoNaObiadDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(new MealContract().getCreateEntriesQuery());
        db.execSQL(new DinnerContract().getCreateEntriesQuery());
        db.execSQL(new IngredientContract().getCreateEntriesQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(new MealContract().getDropTableQuery());
        db.execSQL(new DinnerContract().getDropTableQuery());
        db.execSQL(new IngredientContract().getDropTableQuery());
        onCreate(db);
    }

    public void insert(String tableName, ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(tableName, null, contentValues);
        db.close();
    }

    public void update(String tableName, ContentValues contentValues, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = {Integer.toString(id)};
        db.update(tableName, contentValues, "_ID = ?", args);
        db.close();
    }

    public void delete(String tableName, Long[] ids) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Delete from " + tableName + " where " + BaseColumns._ID + " in (" + TextUtils.join(",", ids) + ")";
        db.execSQL(query);
    }

    public void delete(String tableName, Long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = {Long.toString(id)};
        db.delete(tableName, "_ID = ?", args);
    }
}
