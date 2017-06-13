package com.projects.jezinka.conaobiad.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.projects.jezinka.conaobiad.models.tableDefinitions.DinnerContract;
import com.projects.jezinka.conaobiad.models.tableDefinitions.IngredientCategoryContract;
import com.projects.jezinka.conaobiad.models.tableDefinitions.IngredientContract;
import com.projects.jezinka.conaobiad.models.tableDefinitions.MealContract;
import com.projects.jezinka.conaobiad.models.tableDefinitions.MealIngredientContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CoNaObiadDbHelper extends SQLiteOpenHelper {

    Context context;
    private static final int DATABASE_VERSION = 11;
    private static final String DATABASE_NAME = "CoNaObiad.db";

    public CoNaObiadDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MealContract.getCreateEntriesQuery());
        db.execSQL(DinnerContract.getCreateEntriesQuery());
        db.execSQL(IngredientContract.getCreateEntriesQuery());
        db.execSQL(MealIngredientContract.getCreateEntriesQuery());
        db.execSQL(IngredientCategoryContract.getCreateEntriesQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTable(MealContract.tableName, db);
        dropTable(DinnerContract.tableName, db);
        dropTable(IngredientContract.tableName, db);
        dropTable(MealIngredientContract.tableName, db);
        dropTable(IngredientCategoryContract.tableName, db);
        onCreate(db);
    }

    public long insert(String tableName, ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insert(tableName, null, contentValues);
        db.close();
        return id;
    }

    public void update(String tableName, ContentValues contentValues, long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = {Long.toString(id)};
        db.update(tableName, contentValues, "_ID = ?", args);
        db.close();
    }

    public void delete(String tableName, Long[] ids) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "DELETE FROM " + tableName + " WHERE " + BaseColumns._ID + " IN (" + TextUtils.join(",", ids) + ")";
        db.execSQL(query);
    }

    public void delete(String tableName, Long id, String identityColumn) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = {Long.toString(id)};
        String whereClause = identityColumn + " = ?";
        db.delete(tableName, whereClause, args);
    }

    private void dropTable(String tableName, SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
    }

    public void initializeIngredients() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open("ingredients.txt"), "UTF-8"));

            String mLine;
            while ((mLine = reader.readLine()) != null) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(IngredientContract.columnName, mLine);

                this.insert(IngredientContract.tableName, contentValues);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
