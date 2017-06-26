package com.stefano_probst.gridtest;


import android.provider.BaseColumns;

public class CategoryContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private CategoryContract() {};

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
}

