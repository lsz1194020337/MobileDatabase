package com.example.mobiledatabase.activity;

import android.os.Bundle;
import android.view.View;

import com.example.mobiledatabase.R;

public class P2PMainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p2p_main);
        checkPermission();
    }

    /**
     * Start the client that sends the file
     * @param view
     */
    public void startFileSenderActivity(View view) {
        startActivity(SendFileActivity.class);
    }

    /**
     * Start the client that receives the file
     * @param view
     */
    public void startFileReceiverActivity(View view) {
        startActivity(ReceiveFileActivity.class);
    }
}