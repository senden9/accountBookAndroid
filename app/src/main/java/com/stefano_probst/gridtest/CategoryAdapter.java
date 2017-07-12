package com.stefano_probst.gridtest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;


public class CategoryAdapter extends ArrayAdapter {
    private Context mContext;
    private CategoryDbHelper mCategoryDB;
    private SQLiteDatabase db;

    public CategoryAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
        mContext = context;
        mCategoryDB = new CategoryDbHelper(mContext);
        db = mCategoryDB.getReadableDatabase();
    }

    @Override
    public int getCount() {
        Cursor cursor = mCategoryDB.getCategoryList(db);
        int count = cursor.getCount();
        cursor.close();
        Log.i("CategoryAdapter", "We have " + count + " elements for our category view.");
        return count;
    }

    public void finalize() throws Throwable {
        db.close();
        mCategoryDB.close();
        super.finalize();
    }
}
