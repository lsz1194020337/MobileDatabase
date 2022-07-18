package com.example.mobiledatabase.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.mobiledatabase.common.Constants;
import com.example.mobiledatabase.model.FileTransfer;
import com.example.mobiledatabase.utils.Logger;
import com.example.mobiledatabase.utils.Md5Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SendFileTask extends AsyncTask<String, Integer, Boolean> {

    private ProgressDialog progressDialog;

    private FileTransfer fileTransfer;

    public SendFileTask(Context context, FileTransfer fileTransfer) {
        this.fileTransfer = fileTransfer;
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Sending file");
        progressDialog.setMax(100);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        fileTransfer.setMd5(Md5Util.getMd5(new File(fileTransfer.getFilePath())));
        Logger.d( "The MD5 code value of the file is: " + fileTransfer.getMd5());
        Socket socket = null;
        OutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;
        InputStream inputStream = null;
        try {
            socket = new Socket();
            socket.bind(null);
            socket.connect((new InetSocketAddress(strings[0], Constants.PORT)), 10000);
            outputStream = socket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(fileTransfer);
            inputStream = new FileInputStream(new File(fileTransfer.getFilePath()));
            long fileSize = fileTransfer.getFileLength();
            long total = 0;
            byte[] buf = new byte[512];
            int len;
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
                total += len;
                int progress = (int) ((total * 100) / fileSize);
                publishProgress(progress);
                Logger.d("File sending progress:" + progress);
            }
            socket.close();
            inputStream.close();
            outputStream.close();
            objectOutputStream.close();
            socket = null;
            inputStream = null;
            outputStream = null;
            objectOutputStream = null;
            Logger.e("File sent successfully");
            return true;
        } catch (Exception e) {
            Logger.e("File sending exception Exception: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressDialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        progressDialog.cancel();
        Logger.e("onPostExecute: " + aBoolean);
    }
}
