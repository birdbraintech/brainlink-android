package edu.cmu.ri.createlab.brainlink;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.R.string;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothConnection {

	private BluetoothSocket mSocket;
	private BluetoothDevice mDevice;
	private BluetoothAdapter mAdapter;
	
	private InputStream mInput;
	private OutputStream mOutput;
	
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	public boolean initialBluetoothAdapter() {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mAdapter == null)
			return false;
		else 
			return true;
	}
	
	public void enableBluetoothAdapter() {
		mAdapter.enable();
	}
	
	public boolean getDeviceState() {
		if (mDevice == null)
			return false;
		else
			return true;
	}
	
	public void setDevice(BluetoothDevice d) {
		mDevice = d;
		cancelDiscovery();
	}
	
	public void cancelDiscovery() {
		if(mAdapter.isDiscovering())
			mAdapter.cancelDiscovery();		
	}
	
	public boolean findPairedBrainlinkDevice(String str) {
		Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();
		
		if(pairedDevices.size() > 0)
		{
			for(BluetoothDevice device : pairedDevices) {
				if (device.getName().toString().equals(str)) {
					mAdapter.cancelDiscovery();
					mDevice = device;
					return true;
				}
			}
			return false;
		}
		else 	
			return false;
	}

	public boolean socketConnect(int trytimes) {
		boolean connected = false;
		for(int i=0; i<trytimes && !connected; i++) {
			try {
				connected = true;
				mSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID);	
				mSocket.connect();
			} catch (IOException e) {
				connected = false;
			}
		}
		if(connected) {
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

	public boolean socketConnect(){
		int trytimes = 4;
		boolean connected = false;
		for(int i=0; i<trytimes && !connected; i++) {
			try {
				connected = true;
				mSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID);	
				mSocket.connect();
			} catch (IOException e) {
				connected = false;
			}
		}
		if(connected) {
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
}
