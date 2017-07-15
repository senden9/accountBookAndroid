package com.stefano_probst.gridtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ListEntries extends AppCompatActivity {

    private int mID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_entries);

        // Get the Intent that started this activity and extract the string (ID)
        Intent intent = getIntent();
        String ID_msg = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        mID = Integer.parseInt(ID_msg);
    }
}
