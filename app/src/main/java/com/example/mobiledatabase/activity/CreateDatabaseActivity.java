package com.example.mobiledatabase.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobiledatabase.R;
import com.example.mobiledatabase.utils.MySQLiteHelper;

public class CreateDatabaseActivity extends AppCompatActivity {

    private EditText editText;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_database);
    }

    //jump back to main page after creating new database
    public void createDatabase(View view) {
        //get the input database name
        editText = findViewById(R.id.edit_TextDB);
        String text = editText.getText().toString();

        if (text.isEmpty()){
            toast = Toast.makeText(this, "database name can not be null", Toast.LENGTH_SHORT);
        }else {
            text = text + ".db";
            //use the input name to create database
            SQLiteOpenHelper helper = new MySQLiteHelper(this, text, null, 1);
            // create database file
            helper.getWritableDatabase();
            toast = Toast.makeText(this, "database create successfully", Toast.LENGTH_SHORT);
            startActivity(new Intent(this, MainActivity.class));
        }
        toast.show();
    }


    //DBList button
    public void gotoMainPage(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}