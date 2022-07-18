package com.example.mobiledatabase.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.mobiledatabase.common.Constants;
import com.example.mobiledatabase.model.FileTransfer;
import com.example.mobiledatabase.utils.Logger;
import com.example.mobiledatabase.utils.Md5Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiveFileService extends IntentService {

    public ReceiveFileService() {
        super("ReceiveFileService");
    }

    private ServerSocket serverSocket;

    private InputStream inputStream;

    private ObjectInputStream objectInputStream;

    private FileOutputStream fileOutputStream;

    private OnProgressChangListener progressChangListener;

    /**
     * File transfer callback
     */
    public interface OnProgressChangListener {
        //当传输进度发生变化时
        void onProgressChanged(FileTransfer fileTransfer, int progress);
        //当传输结束时
        void onTransferFinished(File file);

    }

    public class MyBinder extends Binder {
        public ReceiveFileService getService() {
            return ReceiveFileService.this;
        }
    }



    @Override
    public void onCreate() {
        Logger.d("ReceiveFileService onCreate");
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.d("ReceiveFileService onBind");
        return new MyBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logger.d("ReceiveFileService onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d("ReceiveFileService onDestroy");
        clean();
    }

    public void setProgressChangListener(OnProgressChangListener progressChangListener) {
        this.progressChangListener = progressChangListener;
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
        if (objectInputStream != null) {
            try {
                objectInputStream.close();
                objectInputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fileOutputStream != null) {
            try {
                fileOutputStream.close();
                fileOutputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // Use IntentService to listen to the client's Socket connection request in the background, and transfer files through input and output streams.
    // Note that there is a little trick here, the service is only bound once, but startService can be called multiple times,
    // and the onHandleIntent here will also be called multiple times
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Logger.d("ReceiveFileService onHandleIntent+++++");
        clean();
        File file = null;
        try {
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(Constants.PORT));
            Logger.w("The server is listening to socket monitoring...");
            Socket client = serverSocket.accept();
            Logger.w("A client is connected, the client IP address : " + client.getInetAddress().getHostAddress());

            inputStream = client.getInputStream();
            objectInputStream = new ObjectInputStream(inputStream);
            FileTransfer fileTransfer = (FileTransfer) objectInputStream.readObject(); // attention here readObject directly
            Logger.w("documents to be received: " + fileTransfer);

            String name = new File(fileTransfer.getFilePath()).getName();
            file = new File(Environment.getExternalStorageDirectory() + "/" + name);
            fileOutputStream = new FileOutputStream(file);
            byte[] buf = new byte[512];
            int len;
            long total = 0;
            int progress;
            while ((len = inputStream.read(buf)) != -1) {
                fileOutputStream.write(buf, 0, len);
                total += len;
                progress = (int) ((total * 100) / fileTransfer.getFileLength());
                Logger.w( "File receiving progress: " + progress);
                if (progressChangListener != null) {
                    progressChangListener.onProgressChanged(fileTransfer, progress);
                }
            }
            serverSocket.close();
            inputStream.close();
            objectInputStream.close();
            fileOutputStream.close();
            serverSocket = null;
            inputStream = null;
            objectInputStream = null;
            fileOutputStream = null;
            Logger.w( "The file is received successfully, and the MD5 code of the file is:" + Md5Util.getMd5(file));
        }
        catch (Exception e) {
            Logger.e( "File received Exception: " + e.getMessage());
        }
        finally {
            clean();
            if (progressChangListener != null) {
                progressChangListener.onTransferFinished(file);
            }
            startService(new Intent(this, ReceiveFileService.class));
        }
    }


}
