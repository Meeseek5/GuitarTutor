package com.example.guitartutor;

/**
 * Defines several constants used between {@link BluetoothService} and the UI.
 */

public class Constants {
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static String DEVICE_NAME = "device_name";
    public static String TOAST = "toast";
}
