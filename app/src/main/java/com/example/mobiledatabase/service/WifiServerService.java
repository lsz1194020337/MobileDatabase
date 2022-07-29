package com.example.mobiledatabase.service;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.mobiledatabase.common.Constants;
import com.example.mobiledatabase.utils.MySQLiteHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * receive sql
 */
public class WifiServerService extends IntentService {

    private static final String TAG = "WifiServerService";

    private ServerSocket serverSocket;

    private InputStream inputStream;

    private MySQLiteHelper mySQLiteHelper;

    private SQLiteDatabase db;

    private String sql;

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
            mySQLiteHelper = new MySQLiteHelper(WifiServerService.this, "share.db", null, 1);
            db = mySQLiteHelper.getWritableDatabase();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((sql = reader.readLine()) != null) {
                System.out.println("sql receive: " +sql);
                db.execSQL(sql);
            }
            inputStream.close();
            serverSocket.close();
            serverSocket.close();
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
