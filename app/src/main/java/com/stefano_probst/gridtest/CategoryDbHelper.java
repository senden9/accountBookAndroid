package com.stefano_probst.gridtest;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.stefano_probst.gridtest.CategoryContract.CategoryEntry;
import static com.stefano_probst.gridtest.CategoryContract.SQL_CREATE_ENTRIES;
import static com.stefano_probst.gridtest.CategoryContract.SQL_DELETE_ENTRIES;


public class CategoryDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SQLite.db";

    public CategoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        // TODO: Remove dummy entries.
        ContentValues values = new ContentValues();
        values.put(CategoryEntry.COLUMN_NAME_NAME, "Test Category");
        values.put(CategoryEntry.COLUMN_NAME_ICON, "@android/some-path");
        values.put(CategoryEntry.COLUMN_NAME_POSITION, 0);
        long newRowId = db.insert(CategoryEntry.TABLE_NAME, null, values);
        Log.i("DB", "Here is the id of the dummy data: " + newRowId);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: Relpace!
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


}
