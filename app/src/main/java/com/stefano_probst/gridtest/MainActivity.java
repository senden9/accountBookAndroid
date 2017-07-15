package com.stefano_probst.gridtest;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private CategoryDbHelper mCategoryDB;
    private SQLiteDatabase db;
    public static final String EXTRA_MESSAGE = "com.stefano_probst.gridtest.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mCategoryDB = new CategoryDbHelper(getApplicationContext());
        db = mCategoryDB.getReadableDatabase();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Snackbar.make(view, "New Categories can be added in a future release.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Start Grid
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        // Open the new entry activity on press.
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(MainActivity.this, AddEntry.class);
                String message = "" + (position+1); // ID. Not clean at the moment…
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivityForResult(intent, 1);
            }
        });

        // List entries on a long press.
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(MainActivity.this, ListEntries.class);
                String message = "" + (position+1); // ID. Not clean at the moment…
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivityForResult(intent, 2);
                return true;
            }
            });
        // End Grid


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(MainActivity.this, "Things like settings & export/import functions comes in a future release.",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Handle the actions fot the menu.
        // Open the statistic view with different arguments (time range).
        if (id == R.id.stat_week) {
            Intent intent = new Intent(MainActivity.this, Statistics.class);
            String message = "week" ;
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivityForResult(intent, 2);
        } else if (id == R.id.stat_month) {
            Intent intent = new Intent(MainActivity.this, Statistics.class);
            String message = "month" ;
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivityForResult(intent, 2);
        } else if (id == R.id.stat_year) {
            Intent intent = new Intent(MainActivity.this, Statistics.class);
            String message = "year" ;
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivityForResult(intent, 2);
        } else if (id == R.id.stat_all){
            Intent intent = new Intent(MainActivity.this, Statistics.class);
            String message = "all" ;
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivityForResult(intent, 2);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == MainActivity.RESULT_OK){
                Toast.makeText(MainActivity.this, "Entry successful saved.",
                        Toast.LENGTH_SHORT).show();
            }
            if (resultCode == MainActivity.RESULT_CANCELED) {
                Toast.makeText(MainActivity.this, "Something is wrong. Not saved.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void onDestroy() {
        mCategoryDB.close();
        db.close();
        super.onDestroy();
    }

    // Used during debugging. Copy the Database into the download folder.
    // Need the write to storage permission. Enable this permission manually.
    public void copyAppDbToDownloadFolder(String dbName) throws IOException {
        File backupDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), dbName);
        Log.i("Main", "Try to copy Database to " + backupDB.getPath() + ".");
        File currentDB = getApplicationContext().getDatabasePath(dbName);
        if (currentDB.exists()) {
            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
        }
    }
}