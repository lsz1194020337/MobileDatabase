package com.example.mobiledatabase.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.example.mobiledatabase.R;
import com.example.mobiledatabase.broadcast.DirectBroadcastReceiver;
import com.example.mobiledatabase.callback.DirectActionListener;
import com.example.mobiledatabase.service.WifiServerService;

import java.util.Collection;

public class ReceiveFileActivity extends BaseActivity {

    private TextView tv_log;

    private WifiP2pManager wifiP2pManager;

    private WifiP2pManager.Channel channel;

    private boolean connectionInfoAvailable;

    private BroadcastReceiver broadcastReceiver;

    private WifiServerService wifiServerService;

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WifiServerService.WifiServerBinder binder = (WifiServerService.WifiServerBinder) service;
            wifiServerService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (wifiServerService != null) {
                wifiServerService = null;
            }
            bindService();
        }
    };

    private final DirectActionListener directActionListener = new DirectActionListener() {
        @Override
        public void wifiP2pEnabled(boolean enabled) {
            log("wifiP2pEnabled: " + enabled);
        }

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            log("onConnectionInfoAvailable");
            log("isGroupOwner：" + wifiP2pInfo.isGroupOwner);
            log("groupFormed：" + wifiP2pInfo.groupFormed);
            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                connectionInfoAvailable = true;
                if (wifiServerService != null) {
                    startService(WifiServerService.class);
                }
            }
        }

        @Override
        public void onDisconnection() {
            connectionInfoAvailable = false;
            log("onDisconnection");
        }

        @Override
        public void onSelfDeviceAvailable(WifiP2pDevice wifiP2pDevice) {
            log("onSelfDeviceAvailable");
            log(wifiP2pDevice.toString());
        }

        @Override
        public void onPeersAvailable(Collection<WifiP2pDevice> wifiP2pDeviceList) {
            log("onPeersAvailable,size:" + wifiP2pDeviceList.size());
            for (WifiP2pDevice wifiP2pDevice : wifiP2pDeviceList) {
                log(wifiP2pDevice.toString());
            }
        }

        @Override
        public void onChannelDisconnected() {
            log("onChannelDisconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_file);
        initView();
        wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        if (wifiP2pManager == null) {
            finish();
            return;
        }
        channel = wifiP2pManager.initialize(this, getMainLooper(), directActionListener);
        broadcastReceiver = new DirectBroadcastReceiver(wifiP2pManager, channel, directActionListener);
        registerReceiver(broadcastReceiver, DirectBroadcastReceiver.getIntentFilter());
        bindService();
    }

    private void initView() {
        setTitle("Receive Data");
        tv_log = findViewById(R.id.tv_log);
        findViewById(R.id.btnCreateGroup).setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(ReceiveFileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            wifiP2pManager.createGroup(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    log("createGroup onSuccess");
                    dismissLoadingDialog();
                    showToast("onSuccess");
                }

                @Override
                public void onFailure(int reason) {
                    log("createGroup onFailure: " + reason);
                    dismissLoadingDialog();
                    showToast("onFailure");
                }
            });
        });
        findViewById(R.id.btnRemoveGroup).setOnClickListener(v -> removeGroup());
        findViewById(R.id.btnToData).setOnClickListener(v -> jumpToDataPage());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wifiServerService != null) {
            unbindService(serviceConnection);
        }
        unregisterReceiver(broadcastReceiver);
        stopService(new Intent(this, WifiServerService.class));
        if (connectionInfoAvailable) {
            removeGroup();
        }
    }

    private void removeGroup() {
        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                log("removeGroup onSuccess");
                showToast("onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                log("removeGroup onFailure");
                showToast("onFailure");
            }
        });
    }

    private void log(String log) {
        tv_log.append(log + "\n");
        tv_log.append("----------" + "\n");
    }

    private void bindService() {
        Intent intent = new Intent(ReceiveFileActivity.this, WifiServerService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    private void jumpToDataPage() {
        startActivity(new Intent(ReceiveFileActivity.this, MainActivity.class));
    }

}