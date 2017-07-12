package com.stefano_probst.gridtest;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class Statistics extends AppCompatActivity {
    private CategoryDbHelper mCategoryDB;
    private SQLiteDatabase dbCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Connect to the database
        mCategoryDB = new CategoryDbHelper(getApplicationContext());
        dbCategory = mCategoryDB.getReadableDatabase();

        // Bring items (Categories) in the spinner.
        Spinner spinner = (Spinner) findViewById(R.id.spinner_categories);
        String [] names = getCategoryNames();
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, names);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // Fakedata for the graph
        GraphView graph = (GraphView)findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series);
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
}
