package com.example.mobiledatabase.utils;

import android.net.wifi.p2p.WifiP2pDevice;

public class WifiP2pUtils {

    public static String getDeviceStatus(int deviceStatus) {
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
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
