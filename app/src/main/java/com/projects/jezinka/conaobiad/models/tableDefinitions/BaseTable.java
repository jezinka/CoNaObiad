package com.projects.jezinka.conaobiad.models.tableDefinitions;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.ArrayList;

public abstract class BaseTable {

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
