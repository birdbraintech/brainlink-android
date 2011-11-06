package edu.cmu.ri.createlab.brainlink;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothConnection {

	private BluetoothSocket mSocket;
	private List<BluetoothDevice> mDevice = new ArrayList<BluetoothDevice>();
	private BluetoothAdapter mAdapter;

	private InputStream mInput;
	private OutputStream mOutput;

	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB"); // the typical
																	// UUID for
																	// extra
																	// bluetooth
																	// device
																	// like
																	// Brainlink

	public static final int STATE_NO_BLUETOOTH = 0;
	public static final int STATE_BLUETOOTH_OFF = 1;
	public static final int STATE_BLUETOOTH_ON = 2;
	public static final int STATE_BRAINLINK_CONNECTION_FAIL = 3;
	public static final int STATE_BRAINLINK_CONNCCTION_SUCCESS = 4;

	// this constructed function is designed as a quick connection function to
	// setup a blutooth connection to the brainlink device
	// if one hope to use this function, must make sure the brainlink device is
	// turned on and already paired
	public int BluetoothConnectionAuto(String name) {
		boolean mBrainLinkDeviceFound = false;

		// it is necessary to initialize the Bluetooth adapter, if the android
		// device has a Bluetooth, return true, if not, return false
		if (this.initializeBluetoothAdapter()) {
			// after initialize the adapter, check the status. STATE_OFF means
			// the bluetooth is not on
			switch (this.getAdapterState()) {
			case BluetoothAdapter.STATE_OFF:
				// turn on the Bluetooth device
				this.enableBluetoothAdapter();	
				
				for(int i=0; i<2; i++)
				{
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (isEnabled())	// check if the bluetooth is turned on, if not, try again. no more than 2 times
						break;
				}
				break;
			case BluetoothAdapter.STATE_ON:
				break;
			}
		} 
		else 
		{
			return STATE_NO_BLUETOOTH; // no bluetooth device
		}

		if (!isEnabled())
			return STATE_BLUETOOTH_OFF;

		// find the paired brainlink device
		if (!mBrainLinkDeviceFound) {
			// find the paired devices first
			mBrainLinkDeviceFound = this.findPairedBrainlinkDevice(name); // the
																			// keyword
																			// of
																			// the
																			// Bluetooth
																			// component
																			// of
																			// a
																			// brainlink
																			// device

			if (mBrainLinkDeviceFound) // if find one, then set up the
										// connection
			{
				if (!this.socketConnect()) // if successfully connected the
											// brainlink
				{
					mBrainLinkDeviceFound = false; // connection failed
					return STATE_BRAINLINK_CONNECTION_FAIL;
				} else {
					return STATE_BRAINLINK_CONNCCTION_SUCCESS; // connected
																// successfully
				}
			} 
			else 
			{
				return STATE_BRAINLINK_CONNECTION_FAIL; // the brainlink device
														// is not paired in the
														// android phone
			}
		}
		else
		{
			return STATE_BRAINLINK_CONNECTION_FAIL;
		}
	}

	public BluetoothConnection() {

	}

	public boolean initializeBluetoothAdapter() {
		mAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mAdapter == null)
			return false;
		else
			return true;
	}

	public void enableBluetoothAdapter() {
		mAdapter.enable();
	}

	public void cancelDiscovery() {
		if (mAdapter.isDiscovering())
			mAdapter.cancelDiscovery();
	}

	public boolean findPairedBrainlinkDevice(String str) {
		Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				if (device.getName().toString().contains(str)) {
					mDevice.add(device);
				}
			}
			mAdapter.cancelDiscovery();
			if (!mDevice.isEmpty())
				return true;

			return false;
		} else
			return false;
	}

	public void addDevice(BluetoothDevice d) {
		mDevice.add(d);
	}

	public boolean socketConnect(int trytimes) {
		boolean connected = false;
		if (mSocket != null)
			try {
				mSocket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		cancelDiscovery();

		// try "trytimes" of connection to the Brainlink, return false if
		// failed, initialize input and out put if connection success
		for (int i = 0; i < trytimes && !connected; i++) {
			for (int j = 0; j < mDevice.size(); j++) {
				try {
					connected = true;
					mSocket = mDevice.get(j).createRfcommSocketToServiceRecord(
							MY_UUID);
					mSocket.connect();
					break;
				} catch (IOException e) {
					connected = false;
				}
			}
		}
		if (connected) {
			try {
				mInput = mSocket.getInputStream();
				mOutput = mSocket.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return connected;
	}

	// default connection time is 2
	public boolean socketConnect() {
		int trytimes = 2;
		boolean connected = false;
		if (mSocket != null)
			try {
				mSocket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		cancelDiscovery();
		for (int i = 0; i < trytimes && !connected; i++) {
			for (int j = 0; j < mDevice.size(); j++) {
				try {
					connected = true;
					mSocket = mDevice.get(j).createRfcommSocketToServiceRecord(
							MY_UUID);
					mSocket.connect();
					break;
				} catch (IOException e) {
					connected = false;
				}
			}
		}
		if (connected) {
			try {
				mInput = mSocket.getInputStream();
				mOutput = mSocket.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return connected;
	}

	public boolean isEnabled() {
		return mAdapter.isEnabled();
	}

	public OutputStream getOutputStream() throws IOException {
		return mOutput;
	}

	public InputStream getInputStream() throws IOException {
		return mInput;
	}

	public int getAdapterState() {
		return mAdapter.getState();
	}

	public boolean startDiscovery() {
		return mAdapter.startDiscovery();
	}

	public void cencelSocket() {
		try {
			mSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
