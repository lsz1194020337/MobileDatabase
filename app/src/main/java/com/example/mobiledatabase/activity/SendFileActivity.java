package com.example.mobiledatabase.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledatabase.R;
import com.example.mobiledatabase.adapter.DeviceAdapter;
import com.example.mobiledatabase.bean.Database;
import com.example.mobiledatabase.bean.DatabaseInfoList;
import com.example.mobiledatabase.bean.Table;
import com.example.mobiledatabase.broadcast.DirectBroadcastReceiver;
import com.example.mobiledatabase.callback.DirectActionListener;
import com.example.mobiledatabase.common.Constants;
import com.example.mobiledatabase.utils.GetFile;
import com.example.mobiledatabase.utils.MySQLiteHelper;
import com.example.mobiledatabase.utils.WifiP2pUtils;
import com.example.mobiledatabase.widget.LoadingDialog;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class SendFileActivity extends BaseActivity {

    private static final String TAG = "SendFileActivity";

    private WifiP2pManager wifiP2pManager;

    private WifiP2pManager.Channel channel;

    private WifiP2pInfo wifiP2pInfo;

    private boolean wifiP2pEnabled = false;

    private List<WifiP2pDevice> wifiP2pDeviceList;

    private DeviceAdapter deviceAdapter;

    private TextView tv_myDeviceName;

    private TextView tv_myDeviceAddress;

    private TextView tv_myDeviceStatus;

    private TextView tv_status;

    private Button btn_disconnect;

    private Button btn_sendData;

    private LoadingDialog loadingDialog;

    private BroadcastReceiver broadcastReceiver;

    private WifiP2pDevice mWifiP2pDevice;

    private List<String> fileList;

    private SQLiteDatabase db;

    private MySQLiteHelper mySQLiteHelper;

    private List<Table> dataList;

    private List<String> sqlList;

    private String sql;

    private List<Database> databaseList;

    private DatabaseInfoList databaseInfoList;

    private final DirectActionListener directActionListener = new DirectActionListener() {

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
            btn_sendData.setEnabled(true);
            Log.e(TAG, "onConnectionInfoAvailable");
            Log.e(TAG, "onConnectionInfoAvailable groupFormed: " + wifiP2pInfo.groupFormed);
            Log.e(TAG, "onConnectionInfoAvailable isGroupOwner: " + wifiP2pInfo.isGroupOwner);
            Log.e(TAG, "onConnectionInfoAvailable getHostAddress: " + wifiP2pInfo.groupOwnerAddress.getHostAddress());
            StringBuilder stringBuilder = new StringBuilder();
            if (mWifiP2pDevice != null) {
                stringBuilder.append("Connected device name: ");
                stringBuilder.append(mWifiP2pDevice.deviceName);
                stringBuilder.append("\n");
                stringBuilder.append("The address of the connected device: ");
                stringBuilder.append(mWifiP2pDevice.deviceAddress);
            }
            stringBuilder.append("\n");
            stringBuilder.append("Is the group owner: ");
            stringBuilder.append(wifiP2pInfo.isGroupOwner ? "Yes" : "No");
            stringBuilder.append("\n");
            stringBuilder.append("Group owner IP address: ");
            stringBuilder.append(wifiP2pInfo.groupOwnerAddress.getHostAddress());
            tv_status.setText(stringBuilder);
            if (wifiP2pInfo.groupFormed && !wifiP2pInfo.isGroupOwner) {
                SendFileActivity.this.wifiP2pInfo = wifiP2pInfo;
            }
        }

        @Override
        public void onDisconnection() {
            Log.e(TAG, "onDisconnection");
            btn_disconnect.setEnabled(false);
            btn_sendData.setEnabled(false);
            showToast("Disconnected");
            wifiP2pDeviceList.clear();
            deviceAdapter.notifyDataSetChanged();
            tv_status.setText(null);
            SendFileActivity.this.wifiP2pInfo = null;
        }

        @Override
        public void onSelfDeviceAvailable(WifiP2pDevice wifiP2pDevice) {
            Log.e(TAG, "onSelfDeviceAvailable");
            Log.e(TAG, "DeviceName: " + wifiP2pDevice.deviceName);
            Log.e(TAG, "DeviceAddress: " + wifiP2pDevice.deviceAddress);
            Log.e(TAG, "Status: " + wifiP2pDevice.status);
            tv_myDeviceName.setText(wifiP2pDevice.deviceName);
            tv_myDeviceAddress.setText(wifiP2pDevice.deviceAddress);
            tv_myDeviceStatus.setText(WifiP2pUtils.getDeviceStatus(wifiP2pDevice.status));
        }

        @Override
        public void onPeersAvailable(Collection<WifiP2pDevice> wifiP2pDeviceList) {
            Log.e(TAG, "onPeersAvailable :" + wifiP2pDeviceList.size());
            SendFileActivity.this.wifiP2pDeviceList.clear();
            SendFileActivity.this.wifiP2pDeviceList.addAll(wifiP2pDeviceList);
            deviceAdapter.notifyDataSetChanged();
            loadingDialog.cancel();
        }

        @Override
        public void onChannelDisconnected() {
            Log.e(TAG, "onChannelDisconnected");
        }

    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);
        initView();
        initEvent();
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

    public void toDataPage(View view) {
        startActivity(new Intent(SendFileActivity.this, MainActivity.class));
    }

    private void initView() {
        View.OnClickListener clickListener = v -> {
            long id = v.getId();
            if (id == R.id.btn_disconnect) {
                disconnect();
            } else if (id == R.id.btn_sendSQL) {
                navToSendData();
            }
        };
        setTitle("Send Data");
        tv_myDeviceName = findViewById(R.id.tv_myDeviceName);
        tv_myDeviceAddress = findViewById(R.id.tv_myDeviceAddress);
        tv_myDeviceStatus = findViewById(R.id.tv_myDeviceStatus);
        tv_status = findViewById(R.id.tv_status);
        btn_disconnect = findViewById(R.id.btn_disconnect);
        btn_disconnect.setOnClickListener(clickListener);
        btn_sendData = findViewById(R.id.btn_sendSQL);
        btn_sendData.setOnClickListener(clickListener);
        loadingDialog = new LoadingDialog(this);
        RecyclerView rv_deviceList = findViewById(R.id.rv_deviceList);
        wifiP2pDeviceList = new ArrayList<>();
        deviceAdapter = new DeviceAdapter(wifiP2pDeviceList);
        deviceAdapter.setClickListener(position -> {
            mWifiP2pDevice = wifiP2pDeviceList.get(position);
            showToast(mWifiP2pDevice.deviceName);
            connect();
        });
        rv_deviceList.setAdapter(deviceAdapter);
        rv_deviceList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void connect() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showToast("Please grant location permission first");
            return;
        }
        WifiP2pConfig config = new WifiP2pConfig();
        if (config.deviceAddress != null && mWifiP2pDevice != null) {
            config.deviceAddress = mWifiP2pDevice.deviceAddress;
            config.wps.setup = WpsInfo.PBC;
            showLoadingDialog("Connecting " + mWifiP2pDevice.deviceName);
            wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.e(TAG, "connect onSuccess");
                }

                @Override
                public void onFailure(int reason) {
                    showToast("Connect failed " + reason);
                    dismissLoadingDialog();
                }
            });
        }
    }

    private void disconnect() {
        wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onFailure(int reasonCode) {
                Log.e(TAG, "disconnect onFailure:" + reasonCode);
            }

            @Override
            public void onSuccess() {
                Log.e(TAG, "disconnect onSuccess");
                tv_status.setText(null);
                btn_disconnect.setEnabled(false);
                btn_sendData.setEnabled(false);
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
        long id = item.getItemId();
        if (id == R.id.menuDirectEnable) {
            if (wifiP2pManager != null && channel != null) {
                startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
            } else {
                showToast("The current device does not support Wifi Direct");
            }
            return true;
        } else if (id == R.id.menuDirectDiscover) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                showToast("Please grant location permission first");
                return true;
            }
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
        return true;
    }

    private void jumpToDataPage() {
        startActivity(new Intent(SendFileActivity.this, MainActivity.class));
    }

    private void navToSendData() {
        Socket socketSend = null;
        OutputStream outputStream = null;
        ObjectOutputStream oos;
        File oldFile = new File(Constants.APP_DATA_FILE);
        if (oldFile.exists()) {
            fileList = new GetFile().GetDBFileName(Constants.APP_DATA_FILE);
            System.out.println("fileList: " + fileList);
            if (wifiP2pInfo != null) {
                try {
                    String hostAddress = wifiP2pInfo.groupOwnerAddress.getHostAddress();
                    socketSend = new Socket();
                    socketSend.bind(null);
                    socketSend.connect((new InetSocketAddress(hostAddress, Constants.PORT)), 10000);
                    outputStream = socketSend.getOutputStream();
                    oos = new ObjectOutputStream(outputStream);
                    databaseList = new ArrayList<>();
                    databaseInfoList = new DatabaseInfoList();
                    for (String databaseName : fileList) {
                        sqlList = new ArrayList<>();
                        mySQLiteHelper = new MySQLiteHelper(SendFileActivity.this, databaseName, null, 1);
                        db = mySQLiteHelper.getWritableDatabase();
                        dataList = mySQLiteHelper.queryData(db);
                        sqlList.add("delete from user;");
                        for (Table table : dataList) {
                            int id = table.get_id();
                            String c1 = table.getColumn1();
                            String c2 = table.getColumn2();
                            String c3 = table.getColumn3();
                            String c4 = table.getColumn4();
                            String c5 = table.getColumn5();
                            String c6 = table.getColumn6();
                            String c7 = table.getColumn7();
                            String c8 = table.getColumn8();
                            String c9 = table.getColumn9();
                            String c10 = table.getColumn10();
                            sql = "insert into user(_id,c1,c2,c3,c4,c5,c6,c7,c8,c9,c10) values("
                                    + "'" + id + "'," + "'" + c1 + "'," + "'" + c2 + "'," + "'" + c3 + "'," + "'" + c4 + "'," + "'" + c5 + "',"
                                    + "'" + c6 + "'," + "'" + c7 + "'," + "'" + c8 + "'," + "'" + c9 + "'," + "'" + c10 + "');";
                            sqlList.add(sql);
                        }
                        Database database = new Database();
                        database.setDatabaseName(databaseName);
                        System.out.println("databaseName send: " + databaseName);
                        database.setSqlList(sqlList);
                        System.out.println("sqlList: " + sqlList);
                        databaseList.add(database);
                        databaseInfoList.setDatabases(databaseList);
                    }
                    oos.writeObject(databaseInfoList);
                    oos.flush();
                    Toast.makeText(SendFileActivity.this, "Send Data Successfully", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    System.out.println("Send Data error: " + e.getMessage());
                } finally {
                    if (socketSend != null && !socketSend.isClosed()) {
                        try {
                            socketSend.close();
                        } catch (Exception e) {
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
                }
            }
        }
    }
}