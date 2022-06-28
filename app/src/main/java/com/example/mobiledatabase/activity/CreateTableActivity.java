package com.example.mobiledatabase.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobiledatabase.R;
import com.example.mobiledatabase.utils.MySQLiteHelper;

public class CreateTableActivity extends AppCompatActivity {

    private Intent intent;
    private EditText editText;
    private Toast toast;
    private MySQLiteHelper mySQLiteHelper;
    private SQLiteDatabase db;
    private String sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_table);
    }

    //create or delete table
    public void tableList(View view){
        intent = getIntent();
        String databaseName = intent.getStringExtra("databaseName");
        //get the input table name
        editText = findViewById(R.id.edit_TextTable);
        String text = editText.getText().toString();
        if (text.isEmpty()){
            toast = Toast.makeText(this, "table name can not be null !", Toast.LENGTH_SHORT);
        } else {
            //connect to the database
            mySQLiteHelper = new MySQLiteHelper(this, databaseName, null, 1);
            db = mySQLiteHelper.getWritableDatabase();
            //create sql
            sql = "create table if not exists " + text + "( id_ INTEGER primary key autoincrement);";
            db.execSQL(sql);
            toast = Toast.makeText(this, "table create successfully !", Toast.LENGTH_SHORT);
            Intent intent = new Intent(this, DatabaseInfoActivity.class);
            intent.putExtra("databaseName", databaseName);
            setResult(1, intent);
            finish();
        }
        toast.show();
    }

}