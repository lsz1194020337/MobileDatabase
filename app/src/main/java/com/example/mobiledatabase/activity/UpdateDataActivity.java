package com.example.mobiledatabase.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobiledatabase.R;
import com.example.mobiledatabase.utils.MySQLiteHelper;

public class UpdateDataActivity extends AppCompatActivity {
    private Intent intent;
    private int id;
    private String value;
    private String columnName;
    private String databaseName;
    private MySQLiteHelper mySQLiteHelper;
    private SQLiteDatabase db;
    private String sql;
    private EditText etValue;
    private TextView oldValue;
    private Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_data);

        intent = getIntent();
        id = intent.getIntExtra("id", 0);
        value = intent.getStringExtra("value");
        columnName = intent.getStringExtra("colName");
        databaseName = intent.getStringExtra("databaseName");
        btnUpdate = findViewById(R.id.btn_update);
        oldValue = findViewById(R.id.textView2);
        oldValue.setText(value);
        etValue = findViewById(R.id.et_value);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySQLiteHelper = new MySQLiteHelper(UpdateDataActivity.this, databaseName, null, 1);
                db = mySQLiteHelper.getWritableDatabase();
                String newValue = etValue.getText().toString();
                if (newValue.isEmpty()) {
                    Toast.makeText(UpdateDataActivity.this, "Please input the data you want to modify !", Toast.LENGTH_SHORT).show();
                } else {
                    sql = "update user set " + columnName + " = " + "'" + newValue + "' where _id = " + id;
                    db.execSQL(sql);
                    Intent intent = new Intent(UpdateDataActivity.this, DisplayDataActivity.class);
                    intent.putExtra("databaseName", databaseName);
                    startActivity(intent);
                }
            }
        });
    }
}