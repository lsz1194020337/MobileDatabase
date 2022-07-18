package com.example.mobiledatabase.common;

import android.net.wifi.p2p.WifiP2pDevice;

public class Constants {

    public static final int PORT = 1995;

    public static String getDeviceStatus(int deviceStatus) {
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Enabled";
            case WifiP2pDevice.INVITED:
                return "Inviting";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Disabled";
            default:
                return "Unknown";
        }
    }
}
