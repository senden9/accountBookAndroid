package com.stefano_probst.gridtest;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class AddEntry extends AppCompatActivity {

    private int mHour, mMinute, mYear, mMonth, mDay, mID;
    private SpendingDbHelper mSpendingDB;
    private CategoryDbHelper mCategoryDB;
    private SQLiteDatabase dbSpending;
    private SQLiteDatabase dbCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        // Open Databases
        mSpendingDB = new SpendingDbHelper(getApplicationContext());
        dbSpending = mSpendingDB.getWritableDatabase();
        mCategoryDB = new CategoryDbHelper(getApplicationContext());
        dbCategory = mCategoryDB.getReadableDatabase();

        // Set default values for the input fields
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        String hourStr = String.format("%2s",mHour).replace(" ", "0");
        String minuteStr = String.format("%2s",mMinute).replace(" ", "0");
        ((EditText)findViewById(R.id.entry_time)).setText(hourStr+":"+minuteStr);

        ((EditText)findViewById(R.id.entry_date)).setText(mDay + "." + (mMonth + 1) + "." + mYear);

        // Get the Intent that started this activity and extract the string (ID)
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        mID = Integer.parseInt(message);
        ((TextView)findViewById(R.id.header)).setText("Add a entry to " + mCategoryDB.getName(dbCategory, mID) + ".");
    }

    public void timeCallback(View v){
        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        mHour = hourOfDay;
                        mMinute = minute;
                        String hourStr = String.format("%2s",mHour).replace(" ", "0");
                        String minuteStr = String.format("%2s",mMinute).replace(" ", "0");
                        ((EditText)findViewById(R.id.entry_time)).setText(hourStr+":"+minuteStr);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public void dateCallback(View v){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                        ((EditText)findViewById(R.id.entry_date)).setText(mDay + "." + (mMonth + 1) + "." + mYear);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void saveCallback(View v){
        Date date = new Date(mYear, mMonth, mDay, mHour, mMinute);
        String subject = ((EditText)findViewById(R.id.entry_name)).getText().toString();
        float value = 0;
        try {
            value = Float.parseFloat(((EditText) findViewById(R.id.entry_value)).getText().toString());
        } catch (NumberFormatException e){
            Toast.makeText(AddEntry.this, "Please enter a valid value for '" + getString(R.string.label_value) + "'.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        long new_id = mSpendingDB.addEntry(dbSpending, subject, (int)(value*100), date, mID);

        // Transfer state back to the main application
        Intent returnIntent = new Intent();
        if (new_id<0){
            setResult(MainActivity.RESULT_CANCELED, returnIntent);
        } else {
            setResult(MainActivity.RESULT_OK,returnIntent);
        }
        finish();
    }

    protected void onDestroy() {
        mCategoryDB.close();
        mSpendingDB.close();
        dbSpending.close();
        dbCategory.close();
        super.onDestroy();
    }
}
