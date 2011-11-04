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

	public boolean initialBluetoothAdapter() {
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
