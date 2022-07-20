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

import com.example.mobiledatabase.R;
import com.example.mobiledatabase.adapter.TableAdapter;
import com.example.mobiledatabase.utils.GetFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
    private String filesDir = "/data/data/com.example.mobiledatabase/databases/";
    private String tempDir = "/storage/emulated/0/database/";
    private ListView listView;
    private List<String> DBFileList;
    private List<String> NewFileList;
    private TableAdapter adapter;

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        listView = this.findViewById(R.id.databaseList);
        File oldFile = new File(filesDir);
        File newFile = new File(tempDir);
        File old, move;
        //check the fileDir is exist or not
        if (oldFile.exists()) {
            //move db file from sdcard to app data file
            NewFileList = new GetFile().GetDBFileName(tempDir);
            for (String item : NewFileList) {
                old = new File(tempDir + item);
                move = new File(filesDir + item);
                if (old.exists()) {
                    FileChannel outF;
                    try {
                        outF = new FileOutputStream(move).getChannel();
                        new FileInputStream(old).getChannel().transferTo(0, old.length(), outF);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            //move db file from app data file to sdcard
            DBFileList = new GetFile().GetDBFileName(filesDir);
            newFile.mkdir();
            for (String item : DBFileList) {
                old = new File(filesDir + item);
                move = new File(tempDir + item);
                if (old.exists()) {
                    FileChannel outF;
                    try {
                        outF = new FileOutputStream(move).getChannel();
                        new FileInputStream(old).getChannel().transferTo(0, old.length(), outF);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            //if it is the first time using this app, the fileDir is not exist
            DBFileList = null;
        }
        adapter = new TableAdapter(this, DBFileList);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(this);
        //click the .db file to the database info page
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Welcome to File " + DBFileList.get(position).replace(".db", ""),
                        Toast.LENGTH_SHORT).show();
                //deliver the db file to the second page
                Intent intent = new Intent(MainActivity.this, DisplayDataActivity.class);
                intent.putExtra("databaseName", DBFileList.get(position));
                startActivity(intent);
            }
        });
    }

    // new button
    public void createDatabase(View view) {
        startActivity(new Intent(this, CreateTableActivity.class));
    }

    public void goToP2PPage(View view) {
        Toast.makeText(MainActivity.this, "Welcome to P2P Data transfer page ", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, P2PMainActivity.class));
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position,
                                   long id) {

        //delete dialog
        //create dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //create dialog
        AlertDialog dialog = builder.create();
        View dialogView = View.inflate(this, R.layout.delete_dialog, null);
        TextView tv_name = dialogView.findViewById(R.id.tv_dialogName);
        TextView tv_delete = dialogView.findViewById(R.id.tv_delete);
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