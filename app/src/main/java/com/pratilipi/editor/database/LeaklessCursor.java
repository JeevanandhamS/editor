package com.pratilipi.editor.database;

import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;

/**
 * Created by jeeva on 03/09/17.
 */
public class LeaklessCursor extends SQLiteCursor {

    final SQLiteDatabase mDatabase;

    public LeaklessCursor(SQLiteDatabase database, SQLiteCursorDriver driver, String table, SQLiteQuery query) {
        super(database, driver, table, query);
        mDatabase = database;
    }

    @Override
    public void close() {
        super.close();
        if (mDatabase != null) {
            mDatabase.close();
        }
    }
}