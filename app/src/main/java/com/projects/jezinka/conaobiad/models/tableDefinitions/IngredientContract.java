package com.projects.jezinka.conaobiad.models.tableDefinitions;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;
import com.projects.jezinka.conaobiad.models.Ingredient;

import java.util.ArrayList;

public class IngredientContract extends BaseTable implements BaseColumns {

    private String COLUMN_NAME_NAME;
    private String SQL_GET_ALL_RECORD;

    public IngredientContract() {
        this.TABLE_NAME = "ingredient";
        this.COLUMN_NAME_NAME = "name";

        this.SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_NAME + " TEXT)";

        this.SQL_GET_ALL_RECORD = "select " + _ID + ", " + this.COLUMN_NAME_NAME +
                " from " + this.TABLE_NAME +
                " order by " + this.COLUMN_NAME_NAME + " COLLATE NOCASE";
    }

    public boolean insert(CoNaObiadDbHelper dbHelper, String name) {
        String tableName = this.getTableName();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        dbHelper.insert(tableName, contentValues);
        return true;
    }

    public boolean update(CoNaObiadDbHelper dbHelper, String name, Ingredient ingredient) {
        String tableName = this.getTableName();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        dbHelper.update(tableName, contentValues, ingredient.getId());
        return true;
    }

    private ArrayList<Ingredient> getAllIngredients(SQLiteOpenHelper helper) {
        ArrayList<Ingredient> array_list = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.rawQuery(this.SQL_GET_ALL_RECORD, null);
        if (res != null && res.getCount() > 0) {
            res.moveToFirst();

            do {
                String name = res.getString(res.getColumnIndex(this.COLUMN_NAME_NAME));
                int id = res.getInt(res.getColumnIndex(_ID));
                array_list.add(new Ingredient(id, name));
            } while (res.moveToNext());
        }

        db.close();

        return array_list;
    }

    public Ingredient[] getAllIngredientsArray(SQLiteOpenHelper helper) {
        ArrayList<Ingredient> result = getAllIngredients(helper);
        return result.toArray(new Ingredient[result.size()]);
    }
}
