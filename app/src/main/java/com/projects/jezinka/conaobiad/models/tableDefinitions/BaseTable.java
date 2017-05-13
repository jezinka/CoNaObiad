package com.projects.jezinka.conaobiad.models.tableDefinitions;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.projects.jezinka.conaobiad.data.CoNaObiadDbHelper;

import java.util.ArrayList;

public abstract class BaseTable {

    public static String tableName;

    public String getDropTableQuery() {
        return "DROP TABLE IF EXISTS " + tableName;
    }

    public abstract String getCreateEntriesQuery();

    public void delete(Long[] ids, CoNaObiadDbHelper helper) {
        helper.delete(tableName, ids);
    }

    public void delete(Long id, CoNaObiadDbHelper helper) {
        helper.delete(tableName, id, "_ID");
    }

    abstract Object getFromCursor(Cursor res);

    @NonNull
    ArrayList getArrayList(SQLiteOpenHelper helper, String[] sqlArgs, String sqlQuery) {
        ArrayList array_list = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res = db.rawQuery(sqlQuery, sqlArgs);
        if (res != null && res.getCount() > 0) {
            res.moveToFirst();

            do {
                array_list.add(getFromCursor(res));
            } while (res.moveToNext());
        }

        db.close();
        return array_list;
    }
}
