package com.stefano_probst.gridtest;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;

public class ListEntries extends AppCompatActivity {

    private int mID;
    private TableLayout mTable;
    private SpendingDbHelper mSpendingDB;
    private SQLiteDatabase dbSpending;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_entries);

        // Connect to the database
        mSpendingDB = new SpendingDbHelper(getApplicationContext());
        dbSpending = mSpendingDB.getReadableDatabase();

        // Get the Intent that started this activity and extract the string (ID)
        Intent intent = getIntent();
        String ID_msg = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        mID = Integer.parseInt(ID_msg);

        // Find Table
        mTable = (TableLayout)findViewById(R.id.entry_scroll_list);

        // Generate the list.
        // At the moment it is really just a simple list. Things like edit & delete come hopefully in the next release.
        updateList();
    }

    private void updateList(){
        // Delete content of the table & install a new head
        mTable.removeAllViews();
        TableRow rowHead = new TableRow(this);
        TextView labelDateHead = new TextView(this);
        labelDateHead.setText("Date");
        // Value
        TextView labelValueHead = new TextView(this);
        labelValueHead.setText("Spending");
        labelValueHead.setLayoutParams(new TableRow.LayoutParams(Gravity.RIGHT));
        // Subject
        TextView labelSubjectHead = new TextView(this);
        labelSubjectHead.setText("Subject");
        labelSubjectHead.setLayoutParams(new TableRow.LayoutParams(Gravity.RIGHT));
        // Merge
        rowHead.addView(labelDateHead);
        rowHead.addView(labelSubjectHead);
        rowHead.addView(labelValueHead);
        mTable.addView(rowHead);

        float density = getApplicationContext().getResources().getDisplayMetrics().density; // needed to calculate the margin.
        // Get data & iterate over it to fill the table.
        Cursor cursor = mSpendingDB.getCategoryCursor(dbSpending, mID);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // First get the values so that we can put them into the table.
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(cursor.getLong(cursor.getColumnIndexOrThrow(SpendingDbHelper.SpendingEntry.COLUMN_NAME_DATE))); // convert timestamp into a date object
            float value = (float)cursor.getLong(cursor.getColumnIndexOrThrow(SpendingDbHelper.SpendingEntry.COLUMN_NAME_VALUE)) / 100;
            String subject = cursor.getString(cursor.getColumnIndexOrThrow(SpendingDbHelper.SpendingEntry.COLUMN_NAME_SUBJECT));

            // Now fill one row of the table.
            TableRow row = new TableRow(this);
            // Date
            TextView labelDate = new TextView(this);
            String dateString = DateFormat.getDateInstance().format(cal.getTime()); // Print date in the local format
            labelDate.setText(dateString);
            // Value
            TextView labelValue = new TextView(this);
            DecimalFormat df = new DecimalFormat("#0.00");
            String valueString = df.format(value) + " €";
            labelValue.setText(valueString);
            TableRow.LayoutParams valueParams = new TableRow.LayoutParams(Gravity.RIGHT);
            valueParams.setMargins((int)(8*density), 0, (int)(8*density), 0);
            labelValue.setLayoutParams(valueParams);
            // Subject
            TextView labelSubject = new TextView(this);
            labelSubject.setText(subject);
            TableRow.LayoutParams subjectParams = new TableRow.LayoutParams(Gravity.RIGHT);
            subjectParams.setMargins((int)(8*density), 0, (int)(8*density), 0);
            labelSubject.setLayoutParams(subjectParams);
            // Merge
            row.addView(labelDate);
            row.addView(labelSubject);
            row.addView(labelValue);
            mTable.addView(row);
        }
    }

    protected void onDestroy() {
        mSpendingDB.close();
        dbSpending.close();
        super.onDestroy();
    }

}
