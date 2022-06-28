package com.example.mobiledatabase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobiledatabase.R;

public class DatabaseInfoActivity extends AppCompatActivity {
    private Intent intent;
    private String databaseName;
    private TextView fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_info);
        intent = getIntent();
        databaseName = intent.getStringExtra("databaseName");
        fileName = findViewById(R.id.textView4);
        fileName.setText("Database: " + databaseName);
    }

    //DBList button
    public void jumpToMainPage(View view){
        startActivity(new Intent(this, MainActivity.class));
    }

    //New button
    public void jumpToCreateDBPage(View view){
        startActivity(new Intent(this, CreateDatabaseActivity.class));
    }

    //NewTable button
    public void jumpToCreateTablePage(View view){
        startActivity(new Intent(this, CreateTableActivity.class));
    }
}