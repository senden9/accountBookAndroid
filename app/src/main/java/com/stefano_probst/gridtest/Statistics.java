package com.stefano_probst.gridtest;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
    private TableLayout mTable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Connect to the database
        mCategoryDB = new CategoryDbHelper(getApplicationContext());
        dbCategory = mCategoryDB.getReadableDatabase();
        mSpendingDB = new SpendingDbHelper(getApplicationContext());
        dbSpending = mSpendingDB.getReadableDatabase();

        // Find the table
        mTable = (TableLayout) findViewById(R.id.overviewTable);

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
        // enable scaling and scrolling
        mGraph.getViewport().setScalable(true);

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
        String categoryName = (String)parent.getItemAtPosition(position);
        long categoryID;
        if (position == 0) { // „over all categories“
            categoryID = -1; // We do not a categoryID in this case.
        } else { // specific category
            categoryID = mCategoryDB.getIDforCategory(dbCategory, categoryName);
        }
        // Search the right time interval. Depending on how our activity was called.
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
        int xMin = -daysBack; // minimum x value for the graph
        float yMin = Float.MAX_VALUE;
        float yMax = -Float.MAX_VALUE;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -daysBack);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        // Delete content of the table & install a new head
        mTable.removeAllViews();
        TableRow rowHead = new TableRow(this);
        TextView labelDateHead = new TextView(this);
        labelDateHead.setText("Date");
        // Value
        TextView labelValueHead = new TextView(this);
        labelValueHead.setText("Spending");
        labelValueHead.setLayoutParams(new TableRow.LayoutParams(Gravity.RIGHT));
        // Merge
        rowHead.addView(labelDateHead);
        rowHead.addView(labelValueHead);
        mTable.addView(rowHead);
        for(;daysBack > 0; daysBack--){
            float spended;
            if (position == 0){ // We want to show the statistics for all categories.
                spended = mSpendingDB.getSpendings(dbSpending, cal);
            } else { // We want to show a category.
                spended = mSpendingDB.getSpendings(dbSpending, cal, categoryID);
            }
            Log.i("Statistics", "Spendings: " + String.valueOf(spended/100) + " on day: " + String.valueOf(daysBack) + " (" + cal + ")");
            DataPoint point = new DataPoint(-daysBack, spended/100);
            series.appendData(point, true, 356*10);
            // find min/max for the graph scaling
            if (spended > yMax){
                yMax = spended;
            }
            if (spended < yMin){
                yMin = spended;
            }
            // create fill the statistics table.
            // Date
            TableRow row = new TableRow(this);
            TextView labelDate = new TextView(this);
            String dateString = DateFormat.getDateInstance().format(cal.getTime()); // Print date in the local format
            labelDate.setText(dateString);
            // Value
            TextView labelValue = new TextView(this);
            DecimalFormat df = new DecimalFormat("#0.00");
            String valueString = df.format(spended/100) + " €";
            labelValue.setText(valueString);
            labelValue.setLayoutParams(new TableRow.LayoutParams(Gravity.RIGHT));
            // Merge
            row.addView(labelDate);
            row.addView(labelValue);
            mTable.addView(row);
            cal.add(Calendar.DATE, 1); // Step forward
        }
        mGraph.getSeries().clear();
        mGraph.getViewport().setMinY(yMin);
        mGraph.getViewport().setMaxY(yMax);
        mGraph.getViewport().setMinX(xMin);
        mGraph.getViewport().setMaxX(0);
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
