package edu.cmu.ri.createlab.brainlink.android;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.content.BroadcastReceiver;

public class MainActivity extends ListActivity {
	
	private List<Map<String, Object>> mRobotList = new ArrayList<Map<String, Object>>();

	private String mSelectedRobot;

	public static BluetoothConnection mBluetooth;

	private volatile boolean mBluetoothConnected = false; // flag for bluetooth
															// connection
	private volatile boolean mBrainLinkDeviceFound = false; // flag for
															// brainlink found

	private ProgressDialog mBluetoothDialog;
	private AlertDialog mFunctionDialog;

	protected static final int DLG_BT_CONNECTING = 0;
	protected static final int DLG_DEVICE_FINDING = 1;
	protected static final int DLG_DEVICE_PAIRED_FOUND = 2;
	protected static final int DLG_DEVICE_UNPAIRED_FOUND = 3;
	protected static final int DLG_DEVICE_NOT_FOUND = 4;
	protected static final int DLG_DEVICE_CONNECTING = 5;
	protected static final int DLG_DEVICE_FOUND = 6;

//	public static final String BUNDLE_BLUETOOTH = "bundle_bluetooth";
	public static final String BUNDLE_ROBOT = "bundle_robotname";

	// Intent request codes
	private static final int REQUEST_ENABLE_BT = 2;

	Handler bluetoothMessageHandler = new Handler() {
		// @Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MainActivity.DLG_BT_CONNECTING:
				mBluetoothDialog.setMessage("Connecting Bluetooth");
				break;
			case MainActivity.DLG_DEVICE_FINDING:
				mBluetoothDialog.setMessage("Finding BrainLink Device");
				break;
			case MainActivity.DLG_DEVICE_PAIRED_FOUND:
				mBluetoothDialog.setMessage("Paired BrainLink Device Found");
				mBluetoothDialog.cancel();
				mFunctionDialog.show();
				break;
			case MainActivity.DLG_DEVICE_UNPAIRED_FOUND:
				mBluetoothDialog
						.setMessage("Please Pair Your Android with BrainLink First");
				mBluetoothDialog.cancel();
			}
		}
	};



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //Set full Screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		initialList();
		initialFunctionDialog();

		mBluetoothDialog = new ProgressDialog(MainActivity.this);
		mBluetoothDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mBluetoothDialog.setTitle("Connecting");
		mBluetoothDialog.setMessage("Initializing");
		mBluetoothDialog.setIndeterminate(false);
		mBluetoothDialog.setCancelable(false);
		
		mBluetooth = new BluetoothConnection();

	}

	private void initialList() {
		SimpleAdapter adapter = new SimpleAdapter(this, getData(),
				R.layout.list_robot, new String[] { "title", "info", "img" },
				new int[] { R.id.title, R.id.info, R.id.img });
		setListAdapter(adapter);

	}

	private List<Map<String, Object>> getData() {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", "walle");
		map.put("info", "WALL-E U Command");
		map.put("img", R.drawable.i1);
		mRobotList.add(map);

		map = new HashMap<String, Object>();
		map.put("title", "robosapien");
		map.put("info", "WowWee");
		map.put("img", R.drawable.i2);
		mRobotList.add(map);

		map = new HashMap<String, Object>();
		map.put("title", "bossanova");
		map.put("info", "Bossa Nova Prime-8");
		map.put("img", R.drawable.i3);
		mRobotList.add(map);

		return mRobotList;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.layout.menu_main, menu);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		mSelectedRobot = ((HashMap<String, Object>) mRobotList.get(position))
				.get("title").toString();

		if (!mBrainLinkDeviceFound) {
			mBluetoothDialog.show();
		} else {
			mFunctionDialog.show();
		}

		// Connect Bluetooth and Search for Brainlink Device
		if (!mBluetoothConnected) { // if not setup for Bluetooth connection
			ConnectionSetup(); // Start bluetooth
		} else if (!mBrainLinkDeviceFound) {
			brainLinkFindThread.start(); // find Brainlink Device
		}

	}

	private void ConnectionSetup() {
		// Get local Bluetooth adapter
		boolean bHasBluetooth = mBluetooth.initialBluetoothAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (bHasBluetooth == false) {
			Toast.makeText(this, "Your Device does not support Bluetooth",
					Toast.LENGTH_LONG).show();
		}

		if (bHasBluetooth) {
			if (!mBluetooth.isEnabled()) {
				// Intent enableIntent = new
				// Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				// startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
				bluetoothConnectThread.start();
			} else {
				mBluetoothConnected = true;
				brainLinkFindThread.start();
			}
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.addnewrobots:
			return true;
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == 0) {
				mBluetoothConnected = true;
				brainLinkFindThread.start();

			} else {
				// User did not enable Bluetooth or an error occured
				Toast.makeText(this, "Please connect your Bluetooth first",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onDestroy() {
		if (mReceiver != null)
			unregisterReceiver(mReceiver);
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (!mBluetooth.getDeviceState()
						&& device.getName().toString().equals("FireFly-65E7")) {//65E7 99DE
					mBluetooth.setDevice(device);
					mBrainLinkDeviceFound = true;
					
				
					Message m = new Message();
					m.what = MainActivity.DLG_DEVICE_UNPAIRED_FOUND;
					bluetoothMessageHandler.sendMessage(m);

				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {

				if (!mBluetooth.getDeviceState()) {
					Message m = new Message();
					m.what = MainActivity.DLG_DEVICE_NOT_FOUND;
					bluetoothMessageHandler.sendMessage(m);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					mBluetoothConnected = false;
					mBluetoothDialog.cancel();
				}

				mBluetooth.cancelDiscovery();
			}
		}

	};



	private Thread bluetoothConnectThread = new Thread() {

		@Override
		public void run() {

			Message m = new Message();
			boolean quitThread = false;
			for (;;) {
				switch (mBluetooth.getAdapterState()) {
				case BluetoothAdapter.STATE_OFF:
					m.what = MainActivity.DLG_BT_CONNECTING;
					bluetoothMessageHandler.sendMessage(m);
					mBluetooth.enableBluetoothAdapter();
					break;
				case BluetoothAdapter.STATE_ON:
					mBluetoothConnected = true;
					brainLinkFindThread.start();
					quitThread = true;
					break;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				if (quitThread) {
					bluetoothConnectThread.stop();
					break;
				}

			}

		};
	};

	private Thread brainLinkFindThread = new Thread() {

		@Override
		public void run() {

			Message m = new Message();
			m.what = MainActivity.DLG_DEVICE_FINDING;
			bluetoothMessageHandler.sendMessage(m);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}

			
			// Scan paired devices
			if (!mBrainLinkDeviceFound) {
				mBrainLinkDeviceFound = mBluetooth.findPairedBrainlinkDevice("FireFly-65E7");//65E7 
				boolean connect = mBluetooth.socketConnect();			
				m = new Message();
				m.what = MainActivity.DLG_DEVICE_PAIRED_FOUND;
				bluetoothMessageHandler.sendMessage(m);
				if(mBrainLinkDeviceFound)
					brainLinkFindThread.stop();
				}

			// if BrainLinkDevice is not paired, then search for the device
			if (!mBrainLinkDeviceFound) {
				mBluetooth.startDiscovery();
				IntentFilter filter = new IntentFilter(
						BluetoothDevice.ACTION_FOUND);
				registerReceiver(mReceiver, filter); // Don't forget to
														// unregister during
														// onDestroy

				// Register for broadcasts when discovery has finished
				filter = new IntentFilter(
						BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
				MainActivity.this.registerReceiver(mReceiver, filter);
			}

		}
	};

	private void initialFunctionDialog() {
		CharSequence[] items = { "Puppet Control", "Voice Control", "Joystick Control",
				"Mimic Control", "Programmable Control" };
		AlertDialog.Builder mybuilder;
		mybuilder = new AlertDialog.Builder(this);
		mybuilder.setTitle("Choose One");
		mybuilder.setItems(items, functionOnClickListener);

		mFunctionDialog = mybuilder.create();
	}

	DialogInterface.OnClickListener functionOnClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			Bundle bundle = new Bundle();
			Intent i;
			
			switch (which) {
			case 0:
				// Start PuppetActivity
				bundle.putString(BUNDLE_ROBOT, mSelectedRobot);
				
				i = new Intent(MainActivity.this, PuppetActivity.class);
				i.putExtras(bundle);
				
				startActivity(i);
				
				break;
			case 1:
				// Start VoiceActivity
				bundle.putString(BUNDLE_ROBOT, mSelectedRobot);
				
				i = new Intent(MainActivity.this, VoiceActivity.class);
				i.putExtras(bundle);
				
				startActivity(i);
				break;
			case 2:
				// Start VoiceActivity
				bundle.putString(BUNDLE_ROBOT, mSelectedRobot);
				
				i = new Intent(MainActivity.this, JoystickActivity.class);
				i.putExtras(bundle);
				
				startActivity(i);
				break;				
				
			case 3:
				// Start VoiceActivity
				bundle.putString(BUNDLE_ROBOT, mSelectedRobot);
				
				i = new Intent(MainActivity.this, MimicActivity.class);
				i.putExtras(bundle);
				
				startActivity(i);			
				break;
			case 4:
				// Start RouteActivity
				bundle.putString(BUNDLE_ROBOT, mSelectedRobot);
				
				i = new Intent(MainActivity.this, RouteActivity.class);
				i.putExtras(bundle);
				
				startActivity(i);
				break;
			}

		}

	};

}
