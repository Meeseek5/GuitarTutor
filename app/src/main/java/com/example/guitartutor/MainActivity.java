package com.example.guitartutor;


import android.app.Activity;
//import android.app.ActionBar;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private Button btnFgp;
    private Button btnTest;
    private Button btnSong;

    //連線的裝置名稱
    private String mConnectedDeviceName = null;

    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    private BluetoothService mService = null;

    private  MyBroadcastReceiver myBroadcastReceiver = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //final Handler handler = new Handler();

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        /*
        //在配對表中找到RPi，並設定到變數mmDevice
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                if(device.getName().equals("raspberrypi")) { //使用自己的RPi裝置名稱
                    Log.e("Raspberry pi",device.getName());
                    mmDevice = device;
                    break;
                }
            }
        }
         */


        // 測試訊息是否傳到遠端裝置的按紐
        //final Button testButton = (Button) findViewById(R.id.bt1);
        //testButton.setOnClickListener(this);


        //按鍵觸發跳轉到另一頁面
        btnFgp = findViewById(R.id.btFingerPlacement);
        btnFgp.setOnClickListener(this);

        btnTest = findViewById(R.id.btTest);
        btnTest.setOnClickListener(this);

        btnSong = findViewById(R.id.btSong);
        btnSong.setOnClickListener(this);

        //註冊廣播接收器，用來接收和弦指法
        IntentFilter itFilter = new IntentFilter();
        itFilter.addAction("CHORD_A_ACTION");
        itFilter.addAction("CHORD_AM_ACTION");
        itFilter.addAction("CHORD_AM7_ACTION");
        itFilter.addAction("CHORD_AM7G_ACTION");
        itFilter.addAction("CHORD_C_ACTION");
        itFilter.addAction("CHORD_C7_ACTION");
        itFilter.addAction("CHORD_CADD9_ACTION");
        itFilter.addAction("CHORD_CM7_ACTION");
        itFilter.addAction("CHORD_D_ACTION");
        itFilter.addAction("CHORD_D7_ACTION");
        itFilter.addAction("CHORD_DM_ACTION");
        itFilter.addAction("CHORD_DM7_ACTION");
        itFilter.addAction("CHORD_DM7G_ACTION");
        itFilter.addAction("CHORD_E_ACTION");
        itFilter.addAction("CHORD_E7_ACTION");
        itFilter.addAction("CHORD_EM_ACTION");
        itFilter.addAction("CHORD_EM7_ACTION");
        itFilter.addAction("CHORD_EM7B_ACTION");
        itFilter.addAction("CHORD_F_ACTION");
        itFilter.addAction("CHORD_FM7_ACTION");
        itFilter.addAction("CHORD_G_ACTION");
        itFilter.addAction("CHORD_G7_ACTION");
        myBroadcastReceiver = new MyBroadcastReceiver();
        registerReceiver(myBroadcastReceiver, itFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mBluetoothAdapter == null) {
            Toast.makeText(MainActivity.this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            return;
        }
        //若裝置沒有啟動藍芽，則要求使用者開啟藍芽
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else if (mService == null) {
            setupConnection();
        }
    }

    /* 測試Activity畫面不見時，是否有執行onPause(), onStop()
    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(getApplicationContext(), "onPause called", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(getApplicationContext(), "onStop called", Toast.LENGTH_LONG).show();
    }
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            mService.stop();
        }

        //撤銷廣播接收器
        unregisterReceiver(myBroadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
                mService.start();
            }
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btFingerPlacement:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, FingerPlacementActivity.class);
                startActivity(intent);
                break;
            case R.id.btTest:
                Intent intent_2 = new Intent();
                intent_2.setClass(MainActivity.this, TestActivity.class);
                startActivity(intent_2);
                break;
            case R.id.btSong:
                Intent intent_3 = new Intent();
                intent_3.setClass(MainActivity.this, SongActivity.class);
                startActivity(intent_3);
                break;
            //case R.id.bt1:
                //sendMessage("Message from Android phone!");

        }
    }


    private void setupConnection() {
        Log.d(TAG, "setupConnection()");

        // Initialize the BluetoothCService to perform bluetooth connections
        mService = new BluetoothService(MainActivity.this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer();
    }

    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(getApplicationContext(), "Not connected to a device", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);

        }
    }

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        if (MainActivity.this == null) {
            Toast.makeText(MainActivity.this, "MainActivity is null", Toast.LENGTH_SHORT).show();
            return;
        }
        final ActionBar actionBar = MainActivity.this.getSupportActionBar();
        if (actionBar == null) {
            Toast.makeText(MainActivity.this, "ActionBar is null", Toast.LENGTH_SHORT).show();
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        if (MainActivity.this == null) {
            Toast.makeText(MainActivity.this, "MainActivity is null", Toast.LENGTH_SHORT).show();
            return;
        }
        final ActionBar actionBar = MainActivity.this.getSupportActionBar();
        if (actionBar == null) {
            Toast.makeText(MainActivity.this, "ActionBar is null", Toast.LENGTH_SHORT).show();
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    /**
     * The Handler that gets information back from the BluetoothService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Toast.makeText(MainActivity.this, readMessage, Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != MainActivity.this) {
                        Toast.makeText( MainActivity.this, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null !=  MainActivity.this) {
                        Toast.makeText( MainActivity.this, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupConnection();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");

                    if (MainActivity.this != null) {
                        Toast.makeText(MainActivity.this, "bt_not_enabled_leaving",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    /**
     * Establish connection with other device
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        // 取得MAC位址
        Bundle extras = data.getExtras();
        if (extras == null) {
            return;
        }
        String address = extras.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        // 使用MAC address取的BluetoothDevice實體
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        // 連線
        mService.connect(device, secure);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 設置要用哪個menu檔做為選單
        getMenuInflater().inflate(R.menu.bluetooth_connection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.insecure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case R.id.discoverable: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }
        }
        return false;
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case "CHORD_A_ACTION":
                    sendMessage("A");
                    break;
                case "CHORD_AM_ACTION":
                    sendMessage("Am");
                    break;
                case "CHORD_AM7_ACTION":
                    sendMessage("Am7");
                    break;
                case "CHORD_AM7G_ACTION":
                    sendMessage("Am7G");
                    break;
                case "CHORD_C_ACTION":
                    sendMessage("C");
                    break;
                case "CHORD_C7_ACTION":
                    sendMessage("C7");
                    break;
                case "CHORD_CADD9_ACTION":
                    sendMessage("Cadd9");
                    break;
                case "CHORD_CM7_ACTION":
                    sendMessage("CM7");
                    break;
                case "CHORD_D_ACTION":
                    sendMessage("D");
                    break;
                case "CHORD_D7_ACTION":
                    sendMessage("D7");
                    break;
                case "CHORD_DM_ACTION":
                    sendMessage("Dm");
                    break;
                case "CHORD_DM7_ACTION":
                    sendMessage("Dm7");
                    break;
                case "CHORD_DM7G_ACTION":
                    sendMessage("Dm7G");
                    break;
                case "CHORD_E_ACTION":
                    sendMessage("E");
                    break;
                case "CHORD_E7_ACTION":
                    sendMessage("E7");
                    break;
                case "CHORD_EM_ACTION":
                    sendMessage("Em");
                    break;
                case "CHORD_EM7_ACTION":
                    sendMessage("Em7");
                    break;
                case "CHORD_EM7B_ACTION":
                    sendMessage("Em7B");
                    break;
                case "CHORD_F_ACTION":
                    sendMessage("F");
                    break;
                case "CHORD_FM7_ACTION":
                    sendMessage("FM7");
                    break;
                case "CHORD_G_ACTION":
                    sendMessage("G");
                    break;
                case "CHORD_G7_ACTION":
                    sendMessage("G7");
                    break;
            }
        }
    }

}