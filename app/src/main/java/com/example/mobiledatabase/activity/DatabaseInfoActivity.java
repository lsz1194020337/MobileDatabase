package com.example.mobiledatabase.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobiledatabase.R;
import com.example.mobiledatabase.adapter.DatabaseAdapter;
import com.example.mobiledatabase.utils.MySQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseInfoActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
    private Intent intent;
    private String databaseName;
    private TextView fileName;
    private MySQLiteHelper mySQLiteHelper;
    private SQLiteDatabase db;
    private ListView listView;
    private List<String> tableList;
    private DatabaseAdapter adapter;
    private String sql;


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
        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(DatabaseInfoActivity.this, "Welcome to Table " + tableList.get(position), Toast.LENGTH_SHORT).show();
                //deliver the db file to the second page
                Intent intent = new Intent(DatabaseInfoActivity.this, UpdateDataActivity.class);
                intent.putExtra("databaseName", tableList.get(position));
                intent.putExtra("tableName", tableList.get(position));
                startActivity(intent);
            }
        });
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
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: {
                //flash page
                if (resultCode == 1) {
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

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        //delete dialog
        //create dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //create dialog
        AlertDialog dialog = builder.create();
        View dialogView = View.inflate(this, R.layout.delete_dialog, null);
        TextView tv_name = dialogView.findViewById(R.id.tv_dialogName);
        TextView tv_delete = dialogView.findViewById(R.id.tv_delete);

        //delete
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(position);
                dialog.dismiss();
                //delete the table
                mySQLiteHelper = new MySQLiteHelper(DatabaseInfoActivity.this, databaseName, null, 1);
                db = mySQLiteHelper.getWritableDatabase();
                sql = "drop table " + tableList.get(position) + ";";
                db.execSQL(sql);
            }
        });
        tv_name.setText(tableList.get(position));
        dialog.setView(dialogView);
        dialog.show();

        //change the size of dialog must put behind the dialog.show()
        //get params of the dialog of the hole page
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        WindowManager windowManager = getWindowManager();
        Point p = new Point();
        windowManager.getDefaultDisplay().getSize(p);
        params.width = (p.x) / 3;
        dialog.getWindow().setAttributes(params);
        return true;
    }

    private void deleteItem(int position) {
        //create dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure to delete this table ? ");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //delete item need to delete the file
                tableList.remove(position);
                listView.setAdapter(adapter);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }


}