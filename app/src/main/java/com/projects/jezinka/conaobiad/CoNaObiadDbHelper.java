package com.projects.jezinka.conaobiad;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.projects.jezinka.conaobiad.model.DinnerContract;
import com.projects.jezinka.conaobiad.model.MealContract;

/**
 * Created by jezinka on 09.03.17.
 */

public class CoNaObiadDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "CoNaObiad.db";

    public CoNaObiadDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(new MealContract().getCreateEntriesQuery());
        db.execSQL(new DinnerContract().getCreateEntriesQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(new MealContract().getDropTableQuery());
        db.execSQL(new DinnerContract().getDropTableQuery());

        onCreate(db);
    }

    public void insertValuesDbHelper(String tableName, ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(tableName, null, contentValues);
        db.close();
    }

    public void updateValuesDbHelper(String tableName, ContentValues contentValues, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = {Integer.toString(id)};
        db.update(tableName, contentValues, "_ID = ?", args);
        db.close();
    }
}
