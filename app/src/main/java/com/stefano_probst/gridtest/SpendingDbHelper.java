package com.stefano_probst.gridtest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.Calendar;

public class SpendingDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SQLite2.db";

    public SpendingDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        // TODO: Create dummy entries.
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
    public static class SpendingEntry implements BaseColumns {
        public static final String TABLE_NAME = "Spending";
        public static final String COLUMN_NAME_SUBJECT = "subject";
        public static final String COLUMN_NAME_VALUE = "value";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String FOREIGN_NAME_CATEGORY = "fk_Spending_Category";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SpendingEntry.TABLE_NAME + " (" +
            SpendingEntry._ID + " INTEGER PRIMARY KEY," +
            SpendingEntry.COLUMN_NAME_SUBJECT + " TEXT NOT NULL," +
            SpendingEntry.COLUMN_NAME_VALUE + " INT NOT NULL," +
            SpendingEntry.COLUMN_NAME_DATE + " LONG NOT NULL," +
            SpendingEntry.COLUMN_NAME_CATEGORY + " INT NOT NULL)";
            //"INDEX `" + SpendingEntry.FOREIGN_NAME_CATEGORY + "_idx` (`" + SpendingEntry.COLUMN_NAME_CATEGORY + "` ASC)," +
            //    "CONSTRAINT `" + SpendingEntry.FOREIGN_NAME_CATEGORY + "`" +
            //    "FOREIGN KEY (`" + SpendingEntry.COLUMN_NAME_CATEGORY + "`)" +
            //    "REFERENCES `" + CategoryDbHelper.CategoryEntry.TABLE_NAME + "` (`" + CategoryDbHelper.CategoryEntry._ID + "`)" +
            //    "ON DELETE NO ACTION" +
            //    "ON UPDATE NO ACTION" +
            //" )";


    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SpendingEntry.TABLE_NAME;

    private static long getDateTime(Calendar date) {
        return date.getTimeInMillis();
    }

    public static long addEntry(SQLiteDatabase db, String subject, int value, Calendar datetime, int category_id){
        ContentValues values = new ContentValues();
        values.put(SpendingEntry.COLUMN_NAME_SUBJECT, subject);
        values.put(SpendingEntry.COLUMN_NAME_VALUE, value);
        values.put(SpendingEntry.COLUMN_NAME_DATE, getDateTime(datetime));
        values.put(SpendingEntry.COLUMN_NAME_CATEGORY, category_id);
        long new_id = db.insert(SpendingEntry.TABLE_NAME, null, values);
        Log.d("SpendingDB", "New spending entry with ID " + new_id + ".");
        return new_id;
    }

    // Get summed spendings on a specific day for all categories.
    public float getSpendings(SQLiteDatabase db, Calendar day){
        Calendar from = (Calendar)day.clone();
        Calendar to = (Calendar)day.clone();
        from.set(Calendar.HOUR, 0);
        from.set(Calendar.MINUTE, 0);
        from.set(Calendar.SECOND, 0);
        to.set(Calendar.HOUR, 23);
        to.set(Calendar.MINUTE, 59);
        to.set(Calendar.SECOND, 59);
        return getSpendings(db, from, to);
    }

    // Get summed spendings on a specific day from a category.
    public float getSpendings(SQLiteDatabase db, Calendar day, long category_id){
        Calendar from = (Calendar)day.clone();
        Calendar to = (Calendar)day.clone();
        from.set(Calendar.HOUR, 0);
        from.set(Calendar.MINUTE, 0);
        from.set(Calendar.SECOND, 0);
        to.set(Calendar.HOUR, 23);
        to.set(Calendar.MINUTE, 59);
        to.set(Calendar.SECOND, 59);
        return getSpendings(db, from, to , category_id);
    }

    // Get summed spendings from a specific category between 2 dates (datetime).
    public float getSpendings(SQLiteDatabase db, Calendar from, Calendar to, long category_id){
        String query = "SELECT SUM("+ SpendingEntry.COLUMN_NAME_VALUE +") FROM "+ SpendingEntry.TABLE_NAME +" WHERE "+ SpendingEntry.COLUMN_NAME_CATEGORY +" = "+ String.valueOf(category_id) +" AND "+ SpendingEntry.COLUMN_NAME_DATE +" >= "+ getDateTime(from) +" AND "+ SpendingEntry.COLUMN_NAME_DATE +" <= "+ getDateTime(to) +" ;";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        float retVal = cursor.getInt(0);
        cursor.close();
        return retVal;
    }

    // Get summed spendings for all categories between 2 dates (datetime).
    public float getSpendings(SQLiteDatabase db, Calendar from, Calendar to){
        String query = "SELECT SUM("+ SpendingEntry.COLUMN_NAME_VALUE +") FROM "+ SpendingEntry.TABLE_NAME +" WHERE " + SpendingEntry.COLUMN_NAME_DATE +" >= "+ getDateTime(from) +" AND "+ SpendingEntry.COLUMN_NAME_DATE +" <= "+ getDateTime(to) +" ;";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        float retVal = cursor.getInt(0);
        cursor.close();
        return retVal;
    }

    public Cursor getCategoryCursor(SQLiteDatabase db, long category_id){
        String [] whereArgs = {String.valueOf(category_id)};
        String [] columns = {
                SpendingEntry._ID,
                SpendingEntry.COLUMN_NAME_VALUE,
                SpendingEntry.COLUMN_NAME_DATE,
                SpendingEntry.COLUMN_NAME_SUBJECT
        };
        Cursor cursor = db.query(
                SpendingEntry.TABLE_NAME, // The table to query
                columns, // The columns to return. Return all
                SpendingEntry.COLUMN_NAME_CATEGORY + " = ?", // The columns for the WHERE clause
                whereArgs, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                SpendingEntry.COLUMN_NAME_DATE + " DESC" // The sort order
        );
        return cursor;
    }
}
