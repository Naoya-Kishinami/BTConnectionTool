package com.android.btconnection;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by 9004046846 on 2018/03/15.
 */

public class BTConnect {
    static private UUID SSP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    static private BluetoothDevice[] devices;
    static private BluetoothSocket socket;
    static private Handler handler;

    static private final Object lock = new Object();
    static private String received;

    static public String GetReceived() {
        synchronized(lock){
            return received;
        }
    }

    static public void ClearReceived() {
        setReceived("");
    }

    static public boolean InitConnection() {
        boolean ret = false;

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if (adapter != null && adapter.isEnabled()) {
            devices = adapter.getBondedDevices().toArray(new BluetoothDevice[0]);
            ret = (devices != null) && (0 < devices.length);
        }

        return ret;
    }

    static public void DeviceConnect(final Context context) {
        ClearReceived();

        new AlertDialog.Builder(context)
            .setTitle("Select device")
            .setItems(getDeviceNames(devices), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectDevice(which);
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    static private void setReceived(String s) {
        synchronized(lock){
            received = s;
        }
    }

    static private String[] getDeviceNames(BluetoothDevice[] devices) {
        String[] items = new String[devices.length];

        for (int i = 0; i < devices.length; i++) {
            items[i] = devices[i].getName();
        }

        return items;
    }

    static private void selectDevice(int which) {
        try {
            socket = devices[which].createRfcommSocketToServiceRecord(SSP_UUID);
            socket.connect();

            handler = new Handler();

            new Thread() {
                public void run() {
                    try {
                        while (true) {
                            byte[] buf = new byte[1024];
                            int len = socket.getInputStream().read(buf);
                            final String s = new String(buf, 0, len, "UTF-8");

                            Log.d("Received : ",s);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    setReceived(s);
                                }
                            });
                        }
                    } catch(IOException e) {
                    }
                }
            }.start();
        } catch (IOException e) {
        }
    }
}
