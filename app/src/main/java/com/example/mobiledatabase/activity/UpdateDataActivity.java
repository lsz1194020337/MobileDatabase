package com.example.mobiledatabase.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bin.david.form.data.style.FontStyle;
import com.example.mobiledatabase.R;
import com.example.mobiledatabase.adapter.DataAdapter;
import com.example.mobiledatabase.bean.Table;
import com.example.mobiledatabase.utils.MySQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class UpdateDataActivity extends AppCompatActivity {
    private Intent intent;
    private String databaseName;
    private TextView tableName;
    private MySQLiteHelper mySQLiteHelper;
    private SQLiteDatabase db;
    private String sql;
    private List<Table> dataList;
    private ListView lv;
    private DataAdapter dataAdapter;
    private com.bin.david.form.core.SmartTable table;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_data);

        intent = getIntent();
        databaseName = intent.getStringExtra("databaseName");
        tableName = findViewById(R.id.textView4);
        tableName.setText("Table: " + databaseName.replace(".db", ""));

        //display the data in a table
        mySQLiteHelper = new MySQLiteHelper(UpdateDataActivity.this, databaseName, null, 1);
        db = mySQLiteHelper.getWritableDatabase();
        dataList = mySQLiteHelper.queryData(db);
        table = findViewById(R.id.table);
        table.setData(dataList);
        table.getConfig().setContentStyle(new FontStyle(50, Color.BLUE));

    }


    //create or delete table
    public void jumpToDBInfoPage(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    // get the title
    @SuppressLint("Range")
    public ArrayList getColumnName(String databaseName) {
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
        return titleList;
    }
}