/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.mybluetooth;

import edu.cmu.ri.createlab.util.ByteUtils;
import android.app.Activity;
import android.os.Bundle;
import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bill
 */
public class MainActivity extends Activity {

    private int REQUEST_ENABLE;
    public final static ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
    ArrayList<String> myArrayList = null;
    ListView myListView;
    ArrayAdapter<String> myArrayAdapter;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // Get local Bluetooth adapter
    final BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ConnectedThread mConnectedThread;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    static boolean initial = false;
    static boolean firstConnect = false;
//    private static final byte COMMAND_PREFIX = 'i';
    static final byte[] INITIALIZATION_COMMAND = new byte[]{0x49, 0x03, 0x49, 0x02, 0x0B, 0x6D, 0x03, ByteUtils.intToUnsignedByte(0xB6), 0x03, 0x15, 0x02, ByteUtils.intToUnsignedByte(0xD5), 0x00, ByteUtils.intToUnsignedByte(0xB6), 0x03, 0x15, 0x02, ByteUtils.intToUnsignedByte(0xE1), 0x00, 0x00};
    static final byte[] FORWARD = new byte[]{0x69, ByteUtils.intToUnsignedByte(0xAA), ByteUtils.intToUnsignedByte(0xEA), ByteUtils.intToUnsignedByte(0xE8), 0x00, 0x00};

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.main);

        myArrayList = new ArrayList<String>();
        myArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myArrayList);
        myListView = (ListView) findViewById(R.id.myListView);
        myListView.setAdapter(myArrayAdapter);

        // If the adapter is null, then Bluetooth is not supported
        if (myBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!myBluetoothAdapter.isEnabled()) {
            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enabler, REQUEST_ENABLE);
        }

        Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                myArrayList.add(device.getName() + "\n" + device.getAddress());
                myArrayAdapter.notifyDataSetChanged();
            }
        }

        //Start discover
        myBluetoothAdapter.startDiscovery();

        // Register the BroadcastReceiver to receive the device discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        myBluetoothAdapter.cancelDiscovery();
    }

    @Override
    public void onResume() {
        super.onResume();
        myListView.setOnItemClickListener(myListViewListener);
    }
    private ListView.OnItemClickListener myListViewListener =
            new ListView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    String address = myArrayList.get(arg2).split("\n")[1];

                    firstConnect = false;
                    BluetoothDevice device = myBluetoothAdapter.getRemoteDevice(address);

                    try {
                        btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                    } catch (IOException ex) {
                        Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    try {
                        if (!initial) {
                            btSocket.connect();
                            outStream = btSocket.getOutputStream();
                            initial = true;
                            firstConnect = true;
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
                        initial = false;
                        firstConnect = false;
                    }

                    try {
                        if (initial) {
                            if (firstConnect) {
                                outStream.write(INITIALIZATION_COMMAND);
                                outStream.flush();
                                firstConnect = false;
                            }
                            outStream.write(FORWARD);
                            outStream.flush();
                        }
                    } catch (IOException e) {
                        int t;
                    }
                }
            };

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onRestart() {
        super.onRestart();
        Toast.makeText(this, "OnRestart", RESULT_OK);
    }

    @Override
    public void onPause() {
        super.onPause();
        Toast.makeText(this, "OnPause", RESULT_OK);
    }

    @Override
    public void onStop() {
        super.onStop();
        Toast.makeText(this, "OnStop", RESULT_OK);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "OnDestory", RESULT_OK);
    }
// Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                myArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    };

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            //Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                //Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            //Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;
            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    //  mHandler.obtainMessage(BluetoothChat.MESSAGE_READ, bytes, -1, buffer)
                    //         .sendToTarget();
                } catch (IOException e) {
                    //  Log.e(TAG, "disconnected", e);
                    //    connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                //  mHandler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1, buffer)
                //          .sendToTarget();
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "Bad News", RESULT_OK);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                //   Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
