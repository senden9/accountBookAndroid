package com.stefano_probst.gridtest;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;
import java.util.Date;

public class Statistics extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private CategoryDbHelper mCategoryDB;
    private SQLiteDatabase dbCategory;
    private SpendingDbHelper mSpendingDB;
    private SQLiteDatabase dbSpending;
    private String [] mCategoryNames;
    private String mMode; // Timeframe. all, year, month or week.
    private GraphView mGraph;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Connect to the database
        mCategoryDB = new CategoryDbHelper(getApplicationContext());
        dbCategory = mCategoryDB.getReadableDatabase();
        mSpendingDB = new SpendingDbHelper(getApplicationContext());
        dbSpending = mSpendingDB.getReadableDatabase();


        // Bring items (Categories) in the spinner.
        Spinner spinner = (Spinner) findViewById(R.id.spinner_categories);
        mCategoryNames = getCategoryNames();
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, mCategoryNames);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // Fakedata for the graph
        mGraph = (GraphView)findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        mGraph.addSeries(series);

        // Get the Intent that started this activity and extract the string (ID)
        Intent intent = getIntent();
        mMode = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
    }

    // return all Category names
    private String [] getCategoryNames(){
        Cursor cursor = mCategoryDB.getCategoryList(dbCategory);
        String [] names = new String[cursor.getCount()+1];
        // Iterate over the cursor
        int i = cursor.getCount(); // array counter
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            names[i--] = cursor.getString(cursor.getColumnIndex(CategoryDbHelper.CategoryEntry.COLUMN_NAME_NAME));
        }
        names[i] = "All";
        return names;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(position)
        if(position == 0){
            // Todo: Handle „all“ case.
            return;
        }
        String categoryName = (String)parent.getItemAtPosition(position);
        long categoryID = mCategoryDB.getIDforCategory(dbCategory, categoryName);
        int daysBack = 0;
        switch (mMode){
            case "year":
                daysBack = 365;
                break;
            case "month":
                daysBack = 30;
                break;
            case "week":
                daysBack = 7;
                break;
            case "all":
                daysBack = 365*10;
                break;
            default:
                throw new IllegalArgumentException("Invalid mode selected.");
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -daysBack);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        for(;daysBack > 0; daysBack--){
            Date dateBefore = cal.getTime();
            float spended = mSpendingDB.getSpendings(dbSpending, dateBefore, categoryID);
            Log.i("Statistics", "Spendings: " + String.valueOf(spended/100) + " on day: " + String.valueOf(daysBack) + " (" + dateBefore + ")");
            DataPoint point = new DataPoint(-daysBack, spended/100);
            series.appendData(point, true, 356*10);
            cal.add(Calendar.DATE, 1); // Step forward
        }
        mGraph.getSeries().clear();
        mGraph.addSeries(series);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback for the spinner (OnItemSelectedListener)
    }

    protected void onDestroy() {
        mCategoryDB.close();
        mSpendingDB.close();
        dbSpending.close();
        dbCategory.close();
        super.onDestroy();
    }
}
