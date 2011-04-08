package edu.cmu.ri.createlab.brainlink;

import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public static BluetoothConnection mBluetooth = null;
	
	protected static final int DLG_BT_CONNECTING = 0;
	protected static final int DLG_DEVICE_FINDING = 1;
	protected static final int DLG_DEVICE_PAIRED_FOUND = 2;
	protected static final int DLG_DEVICE_UNPAIRED_FOUND = 3;
	protected static final int DLG_DEVICE_NOT_FOUND = 4;
	protected static final int DLG_DEVICE_CONNECTING = 5;
	protected static final int DLG_DEVICE_FOUND = 6;
	
	private Button mQuitButton;
	private Button mConnectButton;
	private ProgressDialog mBluetoothDialog;
	
	private volatile boolean mBluetoothConnected = false;
	private volatile boolean mBrainLinkDeviceFound = false;
	
	OnClickListener quitListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			System.exit(0);				
		}
    };
    
	OnClickListener connectListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			
			//connect Bluetooth
			if(!mBluetoothConnected) {
				//start the bluetooth dialog
				mBluetoothDialog.show();
				
				boolean bHasBluetooth = mBluetooth.initialBluetoothAdapter();
				
				if(bHasBluetooth) {
					if(!mBluetooth.isEnabled()) {
						bluetoothConnectThread.start();
					} else {
						brainLinkFindThread.start();
					}				
				}
			}
			
		//	startActivity(new Intent(MainActivity.this,PanelActivity.class));	
		}
		
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set full Screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
        
        setContentView(R.layout.main);



		
        //initial buttons
        mQuitButton = (Button) findViewById(R.id.quit);
        mConnectButton = (Button) findViewById(R.id.connect);

        mQuitButton.setOnClickListener(quitListener);
        mConnectButton.setOnClickListener(connectListener);   
//        
        //initial bluetooth dialog
        mBluetoothDialog = new ProgressDialog(MainActivity.this);
		mBluetoothDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mBluetoothDialog.setTitle("Connecting");
		mBluetoothDialog.setMessage("Initializing");
		mBluetoothDialog.setIndeterminate(false);
		mBluetoothDialog.setCancelable(false);
		mBluetooth = new BluetoothConnection();

    }
    


    
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

			
			// Scan paired devices, Must paired first
			if (!mBrainLinkDeviceFound) {
				mBrainLinkDeviceFound = mBluetooth.findPairedBrainlinkDevice("FireFly-99DE");//65E7
				boolean connect = mBluetooth.socketConnect();			
				m = new Message();
				m.what = MainActivity.DLG_DEVICE_PAIRED_FOUND;
				bluetoothMessageHandler.sendMessage(m);
				if(mBrainLinkDeviceFound)
					brainLinkFindThread.stop();
				}

//			// if BrainLinkDevice is not paired, then search for the device
//			if (!mBrainLinkDeviceFound) {
//				mBluetooth.startDiscovery();
//				IntentFilter filter = new IntentFilter(
//						BluetoothDevice.ACTION_FOUND);
//				registerReceiver(mReceiver, filter); // Don't forget to
//														// unregister during
//														// onDestroy
//
//				// Register for broadcasts when discovery has finished
//				filter = new IntentFilter(
//						BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//				MainActivity.this.registerReceiver(mReceiver, filter);
//			}
//
		}
	};
	
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
				startActivity(new Intent(MainActivity.this,PanelActivity.class));			
				break;
			case MainActivity.DLG_DEVICE_UNPAIRED_FOUND:
				mBluetoothDialog
						.setMessage("Please Pair Your Android with BrainLink First");
				mBluetoothDialog.cancel();
			}
		}
	};
}