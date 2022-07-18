package com.example.mobiledatabase.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledatabase.BuildConfig;
import com.example.mobiledatabase.R;
import com.example.mobiledatabase.adapter.DeviceAdapter;
import com.example.mobiledatabase.broadcast.DirectBroadcastReceiver;
import com.example.mobiledatabase.callback.DirectActionListener;
import com.example.mobiledatabase.common.Constants;
import com.example.mobiledatabase.model.FileTransfer;
import com.example.mobiledatabase.task.SendFileTask;
import com.example.mobiledatabase.utils.Glide4Engine;
import com.example.mobiledatabase.utils.Logger;
import com.example.mobiledatabase.widget.LoadingDialog;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The client (used to send files) actively searches for nearby devices, joins the group created by the server,
 * obtains the IP address of the server, and initiates a file transfer request to it
 */
public class SendFileActivity extends BaseActivity {

    private static final int CODE_CHOOSE_FILE = 100;

    private TextView tv_myDeviceName;
    private TextView tv_myDeviceAddress;
    private TextView tv_myDeviceStatus;
    private TextView tv_status;
    private List<WifiP2pDevice> wifiP2pDeviceList;
    private DeviceAdapter deviceAdapter;
    private Button btn_disconnect;
    private Button btn_chooseFile;

    private LoadingDialog loadingDialog;

    private WifiP2pManager wifiP2pManager;

    private WifiP2pManager.Channel channel;

    private WifiP2pInfo wifiP2pInfo;

    private WifiP2pDevice mWifiP2pDevice;

    private boolean wifiP2pEnabled = false;

    private DirectBroadcastReceiver broadcastReceiver;


    private DirectActionListener directActionListener = new DirectActionListener() {

        @Override
        public void wifiP2pEnabled(boolean enabled) {
            wifiP2pEnabled = enabled;
        }

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            dismissLoadingDialog();
            wifiP2pDeviceList.clear();
            deviceAdapter.notifyDataSetChanged();
            btn_disconnect.setEnabled(true);
            btn_chooseFile.setEnabled(true);
            Logger.d("onConnectionInfoAvailable");
            Logger.d("onConnectionInfoAvailable groupFormed: " + wifiP2pInfo.groupFormed);
            Logger.d("onConnectionInfoAvailable isGroupOwner: " + wifiP2pInfo.isGroupOwner);
            Logger.d("onConnectionInfoAvailable getHostAddress: " + wifiP2pInfo.groupOwnerAddress.getHostAddress());
            StringBuilder stringBuilder = new StringBuilder();
            if (mWifiP2pDevice != null) {
                stringBuilder.append("connected device name：");
                stringBuilder.append(mWifiP2pDevice.deviceName);
                stringBuilder.append("\n");
                stringBuilder.append("The address of the connected device：");
                stringBuilder.append(mWifiP2pDevice.deviceAddress);
            }
            stringBuilder.append("\n");
            stringBuilder.append("is Group owner：");
            stringBuilder.append(wifiP2pInfo.isGroupOwner ? "Yes" : "No");
            stringBuilder.append("\n");
            stringBuilder.append("Group owner IP address：");
            stringBuilder.append(wifiP2pInfo.groupOwnerAddress.getHostAddress());
            tv_status.setText(stringBuilder);
            if (wifiP2pInfo.groupFormed && !wifiP2pInfo.isGroupOwner) {
                SendFileActivity.this.wifiP2pInfo = wifiP2pInfo;
            }
        }

        @Override
        public void onDisconnection() {
            Logger.d("onDisconnection");
            btn_disconnect.setEnabled(false);
            btn_chooseFile.setEnabled(false);
            showToast("in a disconnected state");
            wifiP2pDeviceList.clear();
            deviceAdapter.notifyDataSetChanged();
            tv_status.setText(null);
            SendFileActivity.this.wifiP2pInfo = null;
        }

        @Override
        public void onSelfDeviceAvailable(WifiP2pDevice wifiP2pDevice) {
            Logger.d("onSelfDeviceAvailable");
            Logger.d("DeviceName: " + wifiP2pDevice.deviceName);
            Logger.d("DeviceAddress: " + wifiP2pDevice.deviceAddress);
            Logger.d("Status: " + wifiP2pDevice.status);
            tv_myDeviceName.setText(wifiP2pDevice.deviceName);
            tv_myDeviceAddress.setText(wifiP2pDevice.deviceAddress);
            tv_myDeviceStatus.setText(Constants.getDeviceStatus(wifiP2pDevice.status));
        }

        @Override
        public void onPeersAvailable(Collection<WifiP2pDevice> wifiP2pDeviceList) {
            Logger.d("onPeersAvailable :" + wifiP2pDeviceList.size());
            SendFileActivity.this.wifiP2pDeviceList.clear();
            SendFileActivity.this.wifiP2pDeviceList.addAll(wifiP2pDeviceList);
            deviceAdapter.notifyDataSetChanged();
            loadingDialog.cancel();
        }

        @Override
        public void onChannelDisconnected() {
            Logger.d("onChannelDisconnected");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);
        initView();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void initEvent() {
        wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        if (wifiP2pManager == null) {
            finish();
            return;
        }
        channel = wifiP2pManager.initialize(this, getMainLooper(), directActionListener);
        broadcastReceiver = new DirectBroadcastReceiver(wifiP2pManager, channel, directActionListener);
        registerReceiver(broadcastReceiver, DirectBroadcastReceiver.getIntentFilter());
    }

    private void initView() {
        setTitle("Sending File");
        tv_myDeviceName = findViewById(R.id.tv_myDeviceName);
        tv_myDeviceAddress = findViewById(R.id.tv_myDeviceAddress);
        tv_myDeviceStatus = findViewById(R.id.tv_myDeviceStatus);
        tv_status = findViewById(R.id.tv_status);
        btn_disconnect = findViewById(R.id.btn_disconnect);
        btn_chooseFile = findViewById(R.id.btn_chooseFile);
        btn_disconnect.setOnClickListener(clickListener);
        btn_chooseFile.setOnClickListener(clickListener);
        loadingDialog = new LoadingDialog(this);
        RecyclerView rv_deviceList = findViewById(R.id.rv_deviceList);
        wifiP2pDeviceList = new ArrayList<>();
        deviceAdapter = new DeviceAdapter(wifiP2pDeviceList);
        deviceAdapter.setClickListener(new DeviceAdapter.OnClickListener() {
            @Override
            public void onItemClick(int position) {
                mWifiP2pDevice = wifiP2pDeviceList.get(position);
                showToast(mWifiP2pDevice.deviceName);
                connect();
            }
        });
        rv_deviceList.setAdapter(deviceAdapter);
        rv_deviceList.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_CHOOSE_FILE && resultCode == RESULT_OK) {
            List<String> strings = Matisse.obtainPathResult(data);
            if (strings != null && !strings.isEmpty()) {
                String path = strings.get(0);
                Logger.d("File Path：" + path);
                File file = new File(path);
                if (file.exists() && wifiP2pInfo != null) {
                    FileTransfer fileTransfer = new FileTransfer(file.getPath(), file.length());
                    new SendFileTask(this, fileTransfer).execute(wifiP2pInfo.groupOwnerAddress.getHostAddress());
                }
            }
        }
    }

    // After that, select the group owner (server-side) device through the click event,
    // and request to connect to it through the connect method
    // There is still no way to judge the connection result through the function function, you need to rely on the WifiP2pManager.
    // WIFI_P2P_CONNECTION_CHANGED_ACTION method issued by the system to obtain the connection result，
    private void connect() {
        WifiP2pConfig config = new WifiP2pConfig();
        if (config.deviceAddress != null && mWifiP2pDevice != null) {
            config.deviceAddress = mWifiP2pDevice.deviceAddress;
            config.wps.setup = WpsInfo.PBC;
            showLoadingDialog("Connecting " + mWifiP2pDevice.deviceName);
            wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Logger.d("connect onSuccess");
                }

                @Override
                public void onFailure(int reason) {
                    showToast("Connecting failed " + reason);
                    dismissLoadingDialog();
                }
            });
        }
    }

    private void disconnect() {
        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onFailure(int reasonCode) {
                Logger.d("disconnect onFailure:" + reasonCode);
            }

            @Override
            public void onSuccess() {
                Logger.d("disconnect onSuccess");
                tv_status.setText(null);
                btn_disconnect.setEnabled(false);
                btn_chooseFile.setEnabled(false);
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuDirectDiscover: {
                if (!wifiP2pEnabled) {
                    showToast("Wifi needs to be turned on first");
                    return true;
                }
                loadingDialog.show("Searching for nearby devices", true, false);
                wifiP2pDeviceList.clear();
                deviceAdapter.notifyDataSetChanged();
                wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        showToast("Success");
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        showToast("Failure");
                        loadingDialog.cancel();
                    }
                });
                return true;
            }
            default:
                return true;
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_disconnect: {
                    disconnect();
                    break;
                }
                case R.id.btn_chooseFile: {
                    navToChose();
                    break;
                }
            }
        }
    };


    private void navToChose() {
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .countable(true)
                .showSingleMediaType(true)
                .maxSelectable(1)
                .capture(false)
                .captureStrategy(new CaptureStrategy(true, BuildConfig.APPLICATION_ID + ".fileprovider"))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.70f)
                .imageEngine(new Glide4Engine())
                .forResult(CODE_CHOOSE_FILE);
    }

}
