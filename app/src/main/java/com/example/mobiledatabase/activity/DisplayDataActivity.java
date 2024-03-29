package com.example.mobiledatabase.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.example.mobiledatabase.R;
import com.example.mobiledatabase.bean.Table;
import com.example.mobiledatabase.utils.MySQLiteHelper;

import java.util.List;

public class DisplayDataActivity extends Activity {
    private Intent intent;
    private String databaseName;
    private TextView tableName;
    private MySQLiteHelper mySQLiteHelper;
    private SQLiteDatabase db;
    private List<Table> dataList;
    private com.bin.david.form.core.SmartTable table;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_data);

        intent = getIntent();
        databaseName = intent.getStringExtra("databaseName");
        tableName = findViewById(R.id.tv_tableName);
        tableName.setText("Table: " + databaseName.replace(".db", ""));

        //display the data in a table
        mySQLiteHelper = new MySQLiteHelper(DisplayDataActivity.this, databaseName, null, 1);
        db = mySQLiteHelper.getWritableDatabase();
        dataList = mySQLiteHelper.queryData(db);
        table = findViewById(R.id.table);
        table.setData(dataList);
        table.getConfig().setContentStyle(new FontStyle(50, Color.BLUE));
        table.getTableData().setOnItemClickListener(new TableData.OnItemClickListener() {
            @Override
            public void onClick(Column column, String value, Object o, int col, int row) {
                Intent intent = new Intent(DisplayDataActivity.this, UpdateDataActivity.class);
                intent.putExtra("colName", column.getColumnName());
                intent.putExtra("id", (row + 1));
                intent.putExtra("value", value);
                intent.putExtra("databaseName", databaseName);
                startActivity(intent);
            }
        });
    }

    //create or delete table
    public void jumpToDBInfoPage(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void jumpToSendDataPage(View view) {
        intent = new Intent(this, SendFileActivity.class);
        startActivity(intent);
    }
    //add example data
    public void addExampleData(View view) {
        addData();
        onCreate(null);
    }

    public void deleteTheLastRow(View view){
        mySQLiteHelper = new MySQLiteHelper(DisplayDataActivity.this, databaseName, null, 1);
        db = mySQLiteHelper.getWritableDatabase();
        String sql = "delete from user where _id = (select max(_id) from user);";
        db.execSQL(sql);
        onCreate(null);
    }

    public void addData() {
        mySQLiteHelper = new MySQLiteHelper(DisplayDataActivity.this, databaseName, null, 1);
        db = mySQLiteHelper.getWritableDatabase();
        mySQLiteHelper.insertData(db);
        Toast.makeText(DisplayDataActivity.this, "Add New Row successfully !", Toast.LENGTH_SHORT).show();
    }
}