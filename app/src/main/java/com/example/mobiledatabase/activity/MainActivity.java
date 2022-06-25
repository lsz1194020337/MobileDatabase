package com.example.mobiledatabase.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.example.mobiledatabase.R;
import com.example.mobiledatabase.adapter.DatabaseAdapter;
import com.example.mobiledatabase.utils.GetFile;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
    private String filesDir = "/data/data/com.example.mobiledatabase/databases";
    private ListView listView;
    private List<String> DBFileList;
    private DatabaseAdapter adapter;

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.main_page);
        listView = this.findViewById(R.id.databaseList);
        File file = new File(filesDir);
        //check the fileDir is exist or not
        if (file.exists()) {
            DBFileList = new GetFile().GetDBFileName(filesDir);
        } else {
            //if it is the first time using this app, the fileDir is not exist
            DBFileList = null;
        }
        listView.setOnItemLongClickListener(this);
        //click the .db file to the database info page
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Welcome to " + DBFileList.get(position), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, DatabaseInfoActivity.class));
            }
        });
        adapter = new DatabaseAdapter(this, DBFileList);
        listView.setAdapter(adapter);
    }
    // new button
    public void createDatabase(View view) {
        startActivity(new Intent(this, CreateDatabaseActivity.class));
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

        //create dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //create dialog
        AlertDialog dialog = builder.create();
        View dialogView = View.inflate(this, R.layout.dialog, null);
        TextView tv_name = dialogView.findViewById(R.id.tv_dialogName);
        TextView tv_update = dialogView.findViewById(R.id.tv_update);
        TextView tv_delete = dialogView.findViewById(R.id.tv_delete);
        //TODO
        //update
        tv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        //delete
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(position);
                dialog.dismiss();
                //delete the database file
                MainActivity.this.deleteDatabase(DBFileList.get(position));
            }
        });
        tv_name.setText(DBFileList.get(position));
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
        builder.setMessage("Are you sure to delete this Database ? ");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //delete item need to delete the file
                DBFileList.remove(position);
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