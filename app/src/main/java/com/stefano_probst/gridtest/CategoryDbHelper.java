package com.stefano_probst.gridtest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;


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
        values.put(CategoryEntry.COLUMN_NAME_ICON, "essen");
        values.put(CategoryEntry.COLUMN_NAME_POSITION, 0);
        long newRowId = db.insert(CategoryEntry.TABLE_NAME, null, values);
        Log.i("DB", "Here is the id of the dummy data: " + newRowId);
        values = new ContentValues();
        values.put(CategoryEntry.COLUMN_NAME_NAME, "Test 2");
        values.put(CategoryEntry.COLUMN_NAME_ICON, "kfz");
        values.put(CategoryEntry.COLUMN_NAME_POSITION, 1);
        newRowId = db.insert(CategoryEntry.TABLE_NAME, null, values);
        Log.i("DB", "Here is the id of the dummy data: " + newRowId);
        values = new ContentValues();
        values.put(CategoryEntry.COLUMN_NAME_NAME, "Test 3");
        values.put(CategoryEntry.COLUMN_NAME_ICON, "home");
        values.put(CategoryEntry.COLUMN_NAME_POSITION, 2);
        newRowId = db.insert(CategoryEntry.TABLE_NAME, null, values);
        Log.i("DB", "Here is the id of the dummy data: " + newRowId);
        values = new ContentValues();
        values.put(CategoryEntry.COLUMN_NAME_NAME, "Test 4");
        values.put(CategoryEntry.COLUMN_NAME_ICON, "strom");
        values.put(CategoryEntry.COLUMN_NAME_POSITION, 2);
        newRowId = db.insert(CategoryEntry.TABLE_NAME, null, values);
        Log.i("DB", "Here is the id of the dummy data: " + newRowId);
        values = new ContentValues();
        values.put(CategoryEntry.COLUMN_NAME_NAME, "Test 5");
        values.put(CategoryEntry.COLUMN_NAME_ICON, "trinken");
        values.put(CategoryEntry.COLUMN_NAME_POSITION, 2);
        newRowId = db.insert(CategoryEntry.TABLE_NAME, null, values);
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

    /* Inner class that defines the table contents */
    public static class CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_ICON = "icon";
        public static final String COLUMN_NAME_POSITION = "position";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" +
                    CategoryEntry._ID + " INTEGER PRIMARY KEY," +
                    CategoryEntry.COLUMN_NAME_NAME + " TEXT," +
                    CategoryEntry.COLUMN_NAME_ICON + " TEXT," +
                    CategoryEntry.COLUMN_NAME_POSITION + " INTEGER)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME;

    public Cursor getCategoryList(SQLiteDatabase db){
        Cursor cursor = db.query(
                CategoryEntry.TABLE_NAME, // The table to query
                null, // The columns to return. Return all
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                CategoryEntry.COLUMN_NAME_POSITION + " DESC" // The sort order
        );
        return cursor;
    }

    public String getName(SQLiteDatabase db, long id) {
        String[] columns = {CategoryEntry.COLUMN_NAME_NAME};
        String[] args = {""+id};
        Cursor cursor = db.query(
                CategoryEntry.TABLE_NAME,
                columns,
                CategoryEntry._ID+"=?",
                args,
                null,
                null,
                null
        );
        cursor.moveToNext();
        return cursor.getString(cursor.getColumnIndexOrThrow(CategoryEntry.COLUMN_NAME_NAME));
    }

    public String getIcon(SQLiteDatabase db, long id) {
        String[] columns = {CategoryEntry.COLUMN_NAME_ICON};
        String[] args = {""+id};
        Cursor cursor = db.query(
                CategoryEntry.TABLE_NAME,
                columns,
                CategoryEntry._ID+"=?",
                args,
                null,
                null,
                null
        );
        cursor.moveToNext();
        return cursor.getString(cursor.getColumnIndexOrThrow(CategoryEntry.COLUMN_NAME_ICON));
    }

}
