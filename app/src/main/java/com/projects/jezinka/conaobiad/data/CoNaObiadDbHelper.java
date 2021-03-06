package com.projects.jezinka.conaobiad.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import com.projects.jezinka.conaobiad.models.tables.CategoryContract;
import com.projects.jezinka.conaobiad.models.tables.DinnerContract;
import com.projects.jezinka.conaobiad.models.tables.IngredientCategoryContract;
import com.projects.jezinka.conaobiad.models.tables.IngredientContract;
import com.projects.jezinka.conaobiad.models.tables.MealCategoryContract;
import com.projects.jezinka.conaobiad.models.tables.MealContract;
import com.projects.jezinka.conaobiad.models.tables.MealIngredientContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CoNaObiadDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "CoNaObiadDbHelper";

    Context context;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "CoNaObiad.db";

    public CoNaObiadDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MealContract.getCreateEntriesQuery());
        db.execSQL(DinnerContract.getCreateEntriesQuery());
        db.execSQL(IngredientContract.getCreateEntriesQuery());
        db.execSQL(MealIngredientContract.getCreateEntriesQuery());
        db.execSQL(CategoryContract.getCreateEntriesQuery());
        db.execSQL(IngredientCategoryContract.getCreateEntriesQuery());
        db.execSQL(MealCategoryContract.getCreateEntriesQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTable(MealContract.getTableName(), db);
        dropTable(DinnerContract.getTableName(), db);
        dropTable(IngredientContract.getTableName(), db);
        dropTable(MealIngredientContract.getTableName(), db);
        dropTable(IngredientCategoryContract.getTableName(), db);
        dropTable(MealCategoryContract.getTableName(), db);
        dropTable(CategoryContract.getTableName(), db);
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
        initializeTable("ingredients.txt", IngredientContract.COLUMN_NAME, IngredientContract.getTableName());
    }

    public void initializeCategories() {
        initializeTable("categories.txt", CategoryContract.COLUMN_NAME, CategoryContract.getTableName());
    }

    private void initializeTable(String fileName, String columnName, String tableName) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName), "UTF-8"));) {

            String mLine;
            while ((mLine = reader.readLine()) != null) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(columnName, mLine);

                this.insert(tableName, contentValues);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error during initialization of tables.");
        } 
    }
}
