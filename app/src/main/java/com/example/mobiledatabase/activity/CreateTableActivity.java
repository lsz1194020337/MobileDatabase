package com.example.mobiledatabase.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobiledatabase.R;
import com.example.mobiledatabase.utils.GetFile;
import com.example.mobiledatabase.utils.MySQLiteHelper;

import java.util.List;

public class CreateTableActivity extends AppCompatActivity {

    private EditText tableName, column1, column2, column3;
    private Toast toast;
    private String filesDir = "/data/data/com.example.mobiledatabase/databases";
    private List<String> DBFileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_table);
    }

    //jump back to main page after creating new database
    public void createDatabase(View view) {
        //get the input database name
        tableName = findViewById(R.id.et_table);
        column1 = findViewById(R.id.et_column1);
        column2 = findViewById(R.id.et_column2);
        column3 = findViewById(R.id.et_column3);
        String text = tableName.getText().toString();
        DBFileList = new GetFile().GetDBFileName(filesDir);
        if (text.isEmpty()) {
            toast = Toast.makeText(this, "Table name can not be null !", Toast.LENGTH_SHORT);
        }else if (DBFileList.contains(text)) {
            toast = Toast.makeText(this, "Table is existed !", Toast.LENGTH_SHORT);
        }else {
            //use the input name to create database
            SQLiteOpenHelper helper = new MySQLiteHelper(this, text + ".db", null, 1);
            // create database file
            helper.getWritableDatabase();
            toast = Toast.makeText(this, "Table create successfully !", Toast.LENGTH_SHORT);
            startActivity(new Intent(this, MainActivity.class));
        }
        toast.show();
    }


    //DBList button
    public void gotoMainPage(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}