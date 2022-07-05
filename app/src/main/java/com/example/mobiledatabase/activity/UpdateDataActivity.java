package com.example.mobiledatabase.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobiledatabase.R;
import com.example.mobiledatabase.utils.MySQLiteHelper;

import java.util.ArrayList;

public class UpdateDataActivity extends AppCompatActivity {
    private Intent intent;
    private String databaseName;
    private String tableName;
    private TextView table;
    private MySQLiteHelper mySQLiteHelper;
    private SQLiteDatabase db;
    private String sql;
    private ArrayList dataList;
    private LinearLayout dataTable;
    private TableRow row;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_data);

        intent = getIntent();
        databaseName = intent.getStringExtra("databaseName").replace(".db","");
        table = findViewById(R.id.textView4);
        table.setText("Table: " + databaseName);

        //get the name
        ArrayList titleList = new ArrayList();
        mySQLiteHelper = new MySQLiteHelper(UpdateDataActivity.this, databaseName, null, 1);
        db = mySQLiteHelper.getWritableDatabase();
        sql = "PRAGMA TABLE_INFO ( " + databaseName + " );";
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {
            String[] list = c.getColumnNames();
            for (String o : list) {
                titleList.add(c.getString(c.getColumnIndex("name")));
            }
        }
    }


    //create or delete table
    public void jumpToDBInfoPage(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}