package com.stefano_probst.gridtest;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.DatePicker;

import java.util.Calendar;

public class AddEntry extends AppCompatActivity {

    private int mHour, mMinute, mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
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
}
