package com.example.mobiledatabase.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.mobiledatabase.R;
import com.example.mobiledatabase.common.Constants;
import com.example.mobiledatabase.utils.GetFile;
import com.example.mobiledatabase.utils.MoveFile;

import java.io.File;
import java.util.List;

public class P2PMainActivity extends BaseActivity {

    private static final int CODE_REQ_PERMISSIONS = 665;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p2p_main);
        //create target folder if not exist
        File targetPath = new File(Constants.SDCARD_DATA_FILE);
        if (!targetPath.exists()) {
            System.out.println("Move with create Folder");
            targetPath.mkdir();
            List<String> oldFileList = new GetFile().GetDBFileName(Constants.APP_DATA_FILE);
            for (String s : oldFileList) {
                File old = new File(Constants.APP_DATA_FILE + s);
                File move = new File(Constants.SDCARD_DATA_FILE + s);
                MoveFile.moveFile(old, move);
            }
        } else {
            System.out.println("Move without create Folder");
            List<String> oldFileList = new GetFile().GetDBFileName(Constants.APP_DATA_FILE);
            for (String s : oldFileList) {
                File old = new File(Constants.APP_DATA_FILE + s);
                File move = new File(Constants.SDCARD_DATA_FILE + s);
                MoveFile.moveFile(old, move);
            }
        }
        findViewById(R.id.btnCheckPermission).setOnClickListener(v ->
                ActivityCompat.requestPermissions(P2PMainActivity.this,
                        new String[]{Manifest.permission.CHANGE_NETWORK_STATE,
                                Manifest.permission.ACCESS_NETWORK_STATE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_WIFI_STATE,
                                Manifest.permission.CHANGE_WIFI_STATE,
                                Manifest.permission.ACCESS_FINE_LOCATION}, CODE_REQ_PERMISSIONS));
        findViewById(R.id.btnSender).setOnClickListener(v ->
                startActivity(new Intent(P2PMainActivity.this, SendFileActivity.class)));
        findViewById(R.id.btnReceiver).setOnClickListener(v ->
                startActivity(new Intent(P2PMainActivity.this, ReceiveFileActivity.class)));
        findViewById(R.id.btnToMain).setOnClickListener(v ->
                startActivity(new Intent(P2PMainActivity.this, MainActivity.class)));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_REQ_PERMISSIONS) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    showToast("Missing permission, please grant permission first: " + permissions[i]);
                    return;
                }
            }
            showToast("Permission granted");
        }
    }
}