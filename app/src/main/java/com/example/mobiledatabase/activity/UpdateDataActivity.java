package com.example.mobiledatabase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobiledatabase.R;

public class UpdateDataActivity extends AppCompatActivity {
    private Intent intent;
    private String databaseName;
    private String tableName;
    private TextView table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_data);

        intent = getIntent();
        databaseName = intent.getStringExtra("databaseName");
        tableName = intent.getStringExtra("tableName");
        table = findViewById(R.id.textView4);
        table.setText("Table: " + tableName);
    }

    //create or delete table
    public void jumpToDBInfoPage(View view){
        startActivity(new Intent(this, DatabaseInfoActivity.class));
    }
}