package com.example.mobiledatabase.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mobiledatabase.R;
import com.example.mobiledatabase.broadcast.DirectBroadcastReceiver;
import com.example.mobiledatabase.callback.DirectActionListener;
import com.example.mobiledatabase.model.FileTransfer;
import com.example.mobiledatabase.service.ReceiveFileService;
import com.example.mobiledatabase.utils.Logger;

import java.io.File;
import java.util.Collection;

/**
 * Start the client that receives the file
 */
public class ReceiveFileActivity extends BaseActivity {

    private ProgressDialog progressDialog;
    private ImageView iv_image;
    private TextView tv_log;

    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private boolean connectionInfoAvailable = false;
    private DirectBroadcastReceiver broadcastReceiver;
    private ReceiveFileService receiveFileService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_file);
        initView();
        initData();
        createGroup();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiveFileService != null) {
            receiveFileService.setProgressChangListener(null);
            unbindService(serviceConnection);
        }
        stopService(new Intent(this, ReceiveFileService.class));
        unregisterReceiver(broadcastReceiver);
        if (connectionInfoAvailable) {
            removeGroup();
        }
    }

    private void initView() {
        setTitle("Receive File");
        iv_image = findViewById(R.id.iv_image);
        tv_log = findViewById(R.id.tv_log);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Receiving file");
        progressDialog.setMax(100);
    }

    private void initData() {
        wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        if (wifiP2pManager == null) {
            showToast("Failed to get WIFI P2P Manager, exit");
            finish();
            return;
        }

        // create channel
        channel = wifiP2pManager.initialize(this, this.getMainLooper(), directActionListener);
        broadcastReceiver = new DirectBroadcastReceiver(wifiP2pManager, channel, directActionListener);
        registerReceiver(broadcastReceiver, DirectBroadcastReceiver.getIntentFilter());
        bindService();
    }

    /**
     * Actively create groups
     * Here, in order to simplify the operation, directly designate a certain device as the server (group owner),
     * that is, directly designate a certain device to receive files
     * Therefore, the server must actively create a group and wait for the connection from the client
     */
    private void createGroup() {
        wifiP2pManager.createGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                printlog("createGroup successfully");
                dismissLoadingDialog();
                showToast("createGroup successfully");
            }

            @Override
            public void onFailure(int reason) {
                printlog("createGroup failed: " + reason);
                dismissLoadingDialog();
                showToast("createGroup failed");
            }
        });
    }

    /**
     * leave group
     */
    private void removeGroup() {
        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                printlog("removeGroup successfully");
                showToast("removeGroup successfully");
            }

            @Override
            public void onFailure(int reason) {
                printlog("removeGroup failed");
                showToast("removeGroup failed");
            }
        });
    }


    private DirectActionListener directActionListener = new DirectActionListener() {

        @Override
        public void wifiP2pEnabled(boolean enabled) {
            Logger.d("Receive rollback: wifiP2pEnabled enabled="+enabled);
            printlog("wifiP2pEnabled device enabled="+enabled);
        }

        @Override
        public void onChannelDisconnected() {
            Logger.d("Receive rollback: onChannelDisconnected");
            printlog("onChannelDisconnected Channel disconnected");
        }

        // 注意，如果createGroup时，也会回调到这个onConnectionInfoAvailable
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            Logger.d("Receive rollback: onConnectionInfoAvailable wifiP2pInfo isGroupOwner="+wifiP2pInfo.isGroupOwner+", groupFormed="+wifiP2pInfo.groupFormed);
            printlog("onConnectionInfoAvailable Get information about connected devices wifiP2pInfo isGroupOwner="+wifiP2pInfo.isGroupOwner+", groupFormed="+wifiP2pInfo.groupFormed);
            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                connectionInfoAvailable = true;
                if (receiveFileService != null) {
                    startService(ReceiveFileService.class);
                }
            }
        }

        @Override
        public void onDisconnection() {
            Logger.d("Receive rollback: onDisconnection");
            printlog("onDisconnection disconnected");
            connectionInfoAvailable = false;
        }

        @Override
        public void onSelfDeviceAvailable(WifiP2pDevice wifiP2pDevice) {
            Logger.d("Receive rollback: onSelfDeviceAvailable Get the local device information wifiP2pDevice="+wifiP2pDevice);
            printlog("onSelfDeviceAvailable Get the local device information wifiP2pDevice="+wifiP2pDevice);
        }

        @Override
        public void onPeersAvailable(Collection<WifiP2pDevice> wifiP2pDeviceList) {
            Logger.d("Receive rollback: onPeersAvailable wifiP2pDeviceList.size="+ (wifiP2pDeviceList == null ? 0 : wifiP2pDeviceList.size()));
            if (wifiP2pDeviceList != null) {
                for (WifiP2pDevice wifiP2pDevice : wifiP2pDeviceList) {
                    Logger.d("Receive rollback: onPeersAvailable Get remote device information "+ wifiP2pDevice.toString());
                    printlog("onPeersAvailable Get remote device information "+ wifiP2pDevice.toString());
                }
            }
        }
    };

    private void printlog(String log) {
        tv_log.append(log + "\n");
        tv_log.append("----------" + "\n");
    }


    private void bindService() {
        Intent intent = new Intent(this, ReceiveFileService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            printlog("onServiceConnected binding ReceiveFileService service successfully");
            ReceiveFileService.MyBinder binder = (ReceiveFileService.MyBinder) service;
            receiveFileService = binder.getService();
            receiveFileService.setProgressChangListener(progressChangListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            receiveFileService = null;
            bindService(); // rebind
        }
    };


    private ReceiveFileService.OnProgressChangListener progressChangListener = new ReceiveFileService.OnProgressChangListener() {
        @Override
        public void onProgressChanged(final FileTransfer fileTransfer, final int progress) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.setMessage("File Name： " + new File(fileTransfer.getFilePath()).getName());
                    progressDialog.setProgress(progress);
                    progressDialog.show();
                }
            });
        }

        @Override
        public void onTransferFinished(final File file) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.cancel();
                    if (file != null && file.exists()) {
                        Glide.with(ReceiveFileActivity.this).load(file.getPath()).into(iv_image);
                    }
                }
            });
        }
    };
}
