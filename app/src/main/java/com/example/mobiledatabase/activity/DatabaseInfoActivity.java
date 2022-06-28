package com.example.mobiledatabase.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobiledatabase.R;
import com.example.mobiledatabase.adapter.DatabaseAdapter;
import com.example.mobiledatabase.utils.MySQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseInfoActivity extends AppCompatActivity {
    private Intent intent;
    private String databaseName;
    private TextView fileName;
    private MySQLiteHelper mySQLiteHelper;
    private SQLiteDatabase db;
    private ListView listView;
    private List<String> tableList;
    private DatabaseAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_info);

        listView = findViewById(R.id.tableList);

        //get the database
        intent = getIntent();
        databaseName = intent.getStringExtra("databaseName");
        fileName = findViewById(R.id.textView4);
        fileName.setText("Database: " + databaseName);
        //get tableList
        tableList = getTableList(databaseName);
        adapter = new DatabaseAdapter(this, tableList);
        listView.setAdapter(adapter);

    }

    @SuppressLint("Range")
    private List<String> getTableList(String databaseName) {
        //connect to the database
        mySQLiteHelper = new MySQLiteHelper(this, databaseName, null, 1);
        db = mySQLiteHelper.getWritableDatabase();
        //get the tableList
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' order by name;", null);
        tableList = new ArrayList<>();
        while (cursor.moveToNext()) {
            tableList.add(cursor.getString(cursor.getColumnIndex("name")));
        }
        //remove the default table
        tableList.remove(0);
        return tableList;
    }

    //DBList button
    public void jumpToMainPage(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    //New button
    public void jumpToCreateDBPage(View view) {
        startActivity(new Intent(this, CreateDatabaseActivity.class));
    }

    //NewTable button
    public void jumpToCreateTablePage(View view) {
        Intent intent = new Intent(DatabaseInfoActivity.this, CreateTableActivity.class);
        intent.putExtra("databaseName", databaseName);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:{
                //flash page
                if(resultCode==1){
                    databaseName = data.getStringExtra("databaseName");
                    tableList = getTableList(databaseName);
                    fileName.setText(databaseName);
                    adapter = new DatabaseAdapter(this, this.tableList);
                    listView.setAdapter(adapter);
                }
                break;
            }
            default:
                break;
        }
    }
}