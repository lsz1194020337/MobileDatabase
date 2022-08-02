package com.example.mobiledatabase.service;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.mobiledatabase.bean.Database;
import com.example.mobiledatabase.bean.DatabaseInfoList;
import com.example.mobiledatabase.common.Constants;
import com.example.mobiledatabase.utils.MySQLiteHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * receive sql
 */
public class WifiServerService extends IntentService {

    private static final String TAG = "WifiServerService";

    private ServerSocket serverSocket;

    private InputStream inputStream;

    private MySQLiteHelper mySQLiteHelper;

    private SQLiteDatabase db;

    private ObjectInputStream ois;

    public WifiServerService() {
        super("WifiServerService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new WifiServerBinder();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        clean();
        try {
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(Constants.PORT));
            Socket client = serverSocket.accept();
            inputStream = client.getInputStream();
            ois = new ObjectInputStream(inputStream);
            //receive the data
            DatabaseInfoList databaseInfo = (DatabaseInfoList) ois.readObject();
            Toast.makeText(WifiServerService.this,"Receive Data Successfully", Toast.LENGTH_SHORT).show();
            List<Database> databaseList = databaseInfo.getDatabases();
            for (Database database : databaseList) {
                String databaseName = database.getDatabaseName();
                List<String> sqlList = database.getSqlList();
                mySQLiteHelper = new MySQLiteHelper(WifiServerService.this, databaseName, null, 1);
                db = mySQLiteHelper.getWritableDatabase();
                for (String sql : sqlList) {
                    db.execSQL(sql);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clean();
            startService(new Intent(this, WifiServerService.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clean();
    }

    private void clean() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                serverSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (inputStream != null) {
            try {
                inputStream.close();
                inputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class WifiServerBinder extends Binder {
        public WifiServerService getService() {
            return WifiServerService.this;
        }
    }

}
