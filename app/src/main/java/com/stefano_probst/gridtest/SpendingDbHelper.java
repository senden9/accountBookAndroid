package com.stefano_probst.gridtest;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    private static String getDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
    }

    public static long addEntry(SQLiteDatabase db, String subject, int value, Date datetime, int category_id){
        ContentValues values = new ContentValues();
        values.put(SpendingEntry.COLUMN_NAME_SUBJECT, subject);
        values.put(SpendingEntry.COLUMN_NAME_VALUE, value);
        values.put(SpendingEntry.COLUMN_NAME_DATE, getDateTime(datetime));
        values.put(SpendingEntry.COLUMN_NAME_CATEGORY, category_id);
        long new_id = db.insert(SpendingEntry.TABLE_NAME, null, values);
        Log.d("SpendingDB", "New spending entry with ID " + new_id + ".");
        return new_id;
    }
}
