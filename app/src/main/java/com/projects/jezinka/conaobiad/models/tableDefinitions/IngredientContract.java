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

    String COLUMN_NAME;
    private String SQL_GET_ALL_RECORD;

    public IngredientContract() {
        this.TABLE_NAME = "ingredient";
        this.COLUMN_NAME = "name";

        this.SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME + " TEXT)";

        this.SQL_GET_ALL_RECORD = "select " + _ID + ", " + this.COLUMN_NAME +
                " from " + this.TABLE_NAME +
                " order by " + this.COLUMN_NAME + " COLLATE NOCASE";
    }

    public long insert(CoNaObiadDbHelper dbHelper, String name) {
        String tableName = this.getTableName();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        return dbHelper.insert(tableName, contentValues);
    }

    public boolean update(CoNaObiadDbHelper dbHelper, String name, Ingredient ingredient) {
        String tableName = this.getTableName();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        dbHelper.update(tableName, contentValues, ingredient.getId());
        return true;
    }

    @NonNull
    private ArrayList<Ingredient> getAllIngredients(SQLiteOpenHelper helper) {
        return getArrayList(helper, null, this.SQL_GET_ALL_RECORD);
    }

    @NonNull
    Ingredient getFromCursor(Cursor res) {
        String name = res.getString(res.getColumnIndex(this.COLUMN_NAME));
        int id = res.getInt(res.getColumnIndex(_ID));
        return new Ingredient(id, name);
    }

    public Ingredient[] getAllIngredientsArray(SQLiteOpenHelper helper) {
        ArrayList<Ingredient> result = getAllIngredients(helper);
        return result.toArray(new Ingredient[result.size()]);
    }
}
