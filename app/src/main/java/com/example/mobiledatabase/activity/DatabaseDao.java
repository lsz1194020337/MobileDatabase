package com.example.mobiledatabase.activity;

import android.content.Context;

import com.example.mobiledatabase.utils.MySQLiteHelper;

public class DatabaseDao {
    private MySQLiteHelper helper;

    public DatabaseDao(Context context, String tableName) {
        helper = new MySQLiteHelper(context, tableName, null, 1);
    }

    public void insert() {

    }

    public void delete() {

    }

    public void update() {

    }

    public void query() {

    }
}
