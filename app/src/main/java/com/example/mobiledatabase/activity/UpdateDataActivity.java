package com.example.mobiledatabase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobiledatabase.R;

public class UpdateDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_table);
    }

    //create or delete table
    public void jumpToDBInfoPage(View view){
        startActivity(new Intent(this, DatabaseInfoActivity.class));
    }
}