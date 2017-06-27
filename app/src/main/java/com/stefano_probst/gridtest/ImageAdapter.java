package com.stefano_probst.gridtest;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private CategoryDbHelper mCategoryDB;
    private SQLiteDatabase db;
    private Cursor mCursorIter = null;

    public ImageAdapter(Context c) {
        mContext = c;
        mCategoryDB = new CategoryDbHelper(mContext);
        db = mCategoryDB.getReadableDatabase();
    }

    public int getCount() {
        Cursor cursor = mCategoryDB.getCategoryList(db);
        int count = cursor.getCount();
        cursor.close();
        Log.i("ImageAdapter", "We have " + count + " elements for our category view.");
        return count;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    // here we can define our buttons
    // maybe just via inserting a fragment
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout linearLayout;
        ImageView imageView;
        TextView textView;
        if (convertView == null) {
            /*if (mCursorIter == null){
                mCursorIter = mCategoryDB.getCategoryList(db);
            }
            if (mCursorIter.moveToNext() == false){
                Log.e("ImageAdapter", "We are already behind the last element.");
            }*/
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(220, 220));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
            int resID = mContext.getResources().getIdentifier(mCategoryDB.getIcon(db,position+1), "mipmap", mContext.getPackageName());
            imageView.setImageResource(resID);
            textView = new TextView(mContext);
            //String catName = mCursorIter.getString(mCursorIter.getColumnIndexOrThrow(CategoryDbHelper.CategoryEntry.COLUMN_NAME_NAME));
            String catName = mCategoryDB.getName(db, position+1);
            textView.setText(catName);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            linearLayout = new LinearLayout(mContext);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            linearLayout.addView(imageView);
            linearLayout.addView(textView);
        } else {
            linearLayout = (LinearLayout) convertView;
        }
        return linearLayout;
    }

    public void finalize() throws Throwable {
        db.close();
        mCategoryDB.close();
        super.finalize();
    }
}