package com.example.mobiledatabase.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.mobiledatabase.bean.Table;

import java.util.ArrayList;

public class MySQLiteHelper extends SQLiteOpenHelper {
    private String sql;
    private String c1, c2, c3, c4, c5, c6, c7, c8, c9, c10;
    private Context context;

    public MySQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    //initialize database
    @Override
    public void onCreate(SQLiteDatabase db) {
        sql = "create table user(_id integer primary key autoincrement, c1 text, c2 text, c3 text, c4 text, c5 text, c6 text, c7 text, c8 text, c9 text, c10 text)";
        db.execSQL(sql);
        insertData(db);
    }

    //update database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //query all data
    public ArrayList<Table> queryData(SQLiteDatabase db) {
        ArrayList<Table> dataList = new ArrayList<>();
        sql = "select * from user";
        Cursor c = null;
        if (db != null) {
            c = db.rawQuery(sql, null);
            while (c.moveToNext()) {
                int id = c.getInt(0);
                String c1 = c.getString(1);
                String c2 = c.getString(2);
                String c3 = c.getString(3);
                String c4 = c.getString(4);
                String c5 = c.getString(5);
                String c6 = c.getString(6);
                String c7 = c.getString(7);
                String c8 = c.getString(8);
                String c9 = c.getString(9);
                String c10 = c.getString(10);
                Table table = new Table(id, c1, c2, c3, c4, c5, c6, c7, c8, c9, c10);
                dataList.add(table);
            }
        }
        return dataList;
    }

    public void insertData(SQLiteDatabase db){
        sql = "insert into user(c1,c2,c3,c4,c5,c6,c7,c8,c9,c10) values(0,0,0,0,0,0,0,0,0,0)";
        db.execSQL(sql);
    }

}
