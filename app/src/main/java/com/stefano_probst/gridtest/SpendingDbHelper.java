package com.stefano_probst.gridtest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public class SpendingDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SQLite.db";

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
                    SpendingEntry.COLUMN_NAME_VALUE + " INT NOT NULL COMMENT 'in cent'," +
                    SpendingEntry.COLUMN_NAME_DATE + " DATE NOT NULL COMMENT 'Datetime. Not just date.'," +
                    SpendingEntry.COLUMN_NAME_CATEGORY + " INT NOT NULL COMMENT 'foreign key for the category'," +
                    "INDEX `" + SpendingEntry.FOREIGN_NAME_CATEGORY + "_idx` (`" + SpendingEntry.COLUMN_NAME_CATEGORY + "` ASC)," +
                        "CONSTRAINT `" + SpendingEntry.FOREIGN_NAME_CATEGORY + "`" +
                        "FOREIGN KEY (`" + SpendingEntry.COLUMN_NAME_CATEGORY + "`)" +
                        "REFERENCES `" + CategoryDbHelper.CategoryEntry.TABLE_NAME + "` (`" + CategoryDbHelper.CategoryEntry._ID + "`)" +
                        "ON DELETE NO ACTION" +
                        "ON UPDATE NO ACTION" +
                    " )";


    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SpendingEntry.TABLE_NAME;
}
