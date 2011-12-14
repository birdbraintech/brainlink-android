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


/**
 * Simplifies dealing with Bluetooth for Brainlink on Android. You must use this to get input and output streams
 * for the Brainlink object.
 * @author Huaishu Peng 
 * @author Tom Lauwers (tlauwers@birdbraintechnologies.com)
 */
public class BluetoothConnection {

	private BluetoothSocket mSocket;
	private List<BluetoothDevice> mDevice = new ArrayList<BluetoothDevice>();
	private BluetoothAdapter mAdapter;

	private InputStream mInput;
	private OutputStream mOutput;

	/* The standard UUID for an SPP (serial) bluetooth device like Brainlink */
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB"); 

	public static final int STATE_NO_BLUETOOTH = 0;
	public static final int STATE_BLUETOOTH_OFF = 1;
	public static final int STATE_BLUETOOTH_ON = 2;
	public static final int STATE_BRAINLINK_CONNECTION_FAIL = 3;
	public static final int STATE_BRAINLINK_CONNECTION_SUCCESS = 4;

	/**
	 * This function is designed as a quick connection function to
	 * setup a blutooth connection to the brainlink device. Brainlink must be turned on
	 * and paired for the function to return successfully. 
	 * 
	 *  @param name The name of the device to connect to. For Brainlink, use "RN42"
	 *  @return The status of the connection attempt
	 */
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
					return STATE_BRAINLINK_CONNECTION_SUCCESS; // connected
																// successfully
				}
			} 
			else 
			{
				return STATE_BRAINLINK_CONNECTION_FAIL; // the brainlink device
														// is not paired with the
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

	/**
	 * Gets the handle to the bluetooth adapter and stores it within the BluetoothConnection object (for use with other methods)
	 * @return true if an adapter was found, false otherwise
	 */
	public boolean initializeBluetoothAdapter() {
		mAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mAdapter == null)
			return false;
		else
			return true;
	}

	/**
	 * Turns on the bluetooth adapter. Call initializeBluetoothAdapter first.
	 */
	public void enableBluetoothAdapter() {
		mAdapter.enable();
	}

	/**
	 * Cancels further discovery of devices (used when app needs to connect with an unpaired brainlink)
	 */
	public void cancelDiscovery() {
		if (mAdapter.isDiscovering())
			mAdapter.cancelDiscovery();
	}

	/**
	 * Looks for devices that contain the string specified by str within their device name. All Brainlinks contain the 
	 * string "RN42" in their device name. Stores found devices within the class.
	 * @param str The substring to look for in a device name
	 * @return true if one or more devices were found, false if none were found.
	 */
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

	/**
	 * Adds a bluetooth device to the object's list of devices
	 * @param d The device handle to add
	 */
	public void addDevice(BluetoothDevice d) {
		mDevice.add(d);
	}

	/**
	 * Tries to create a socket connection to the devices found or added to the list. Will try to
	 * connect for the number of times specified by trytimes.
	 * @param trytimes 
	 * @return true if a socket with input and output streams was created, false otherwise
	 */
	public boolean socketConnect(int trytimes) {
		boolean connected = false;
		// If we have a socket already, close it first
		if (mSocket != null)
			try {
				mSocket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		cancelDiscovery();

		// try "trytimes" of connection to the Brainlink, return false if
		// failed, initialize input and output if connection success
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

	/**
	 * Tries to create a socket connection to the devices found or added to the list. Will try to
	 * connect twice.
	 * @param trytimes 
	 * @return true if a socket with input and output streams was created, false otherwise
	 */
	public boolean socketConnect() {
		return socketConnect(2);
	}

	/**
	 * Gets the state of the bluetooth adapter
	 * @return true if bluetooth is on, false otherwise
	 */
	public boolean isEnabled() {
		return mAdapter.isEnabled();
	}

	/**
	 * Returns the output stream associated with the connection to the Brainlink (or other device)
	 * 
	 * @return the Output stream for use in Brainlink instantiation
	 * @throws IOException
	 */
	public OutputStream getOutputStream() throws IOException {
		return mOutput;
	}

	/**
	 * Returns the input stream associated with the connection to the Brainlink (or other device)
	 * 
	 * @return the Input stream for use in Brainlink instantiation
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException {
		return mInput;
	}

	/**
	 * Returns the state of the bluetooth adapter
	 * 
	 * @return adapter state (On, turning on, off, turning off)
	 * @throws IOException
	 */
	public int getAdapterState() {
		return mAdapter.getState();
	}

	/**
	 * Starts the adapter scanning for bluetooth devices
	 * @return true if the adapter is on and scanning, false otherwise
	 */
	public boolean startDiscovery() {
		return mAdapter.startDiscovery();
	}

	/**
	 * Closes the socket connection. Should be called in an app's onDestroy method.
	 */
	public void cancelSocket() {
		try {
			mSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
