package huaishu.peng.brainlink;

import java.io.IOException;

import edu.cmu.ri.createlab.brainlink.BluetoothConnection;
import edu.cmu.ri.createlab.brainlink.BrainLink;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class BrainlinkManualAndroidSetupActivity extends Activity {
	/** Called when the activity is first created. */

	// an app to demo how to connect the brainlink manually
	// import the external jar file BrainLinkAndroidLib.jar
	// key feature: automatically paired with new BrainLink device
	// created by Huaishu Peng
	// www.huaishu.me
	// 11/6/2011

	private BluetoothConnection mBluetooth;
	private BrainLink mBrainLink;

	boolean connection;
	boolean findPaired;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// First, new an instance of BluetoothConnection
		mBluetooth = new BluetoothConnection();

		// initialize the Bluetooth Adapter. An essential step to manipulate
		// Bluetooth in Android
		mBluetooth.initializeBluetoothAdapter();

		if (!mBluetooth.isEnabled()) {
			mBluetooth.enableBluetoothAdapter(); // enable the adapter, turn on
													// the Bluetooth if not.
													// This might take sometime,
													// so here we simply sleep
													// for 3s. It would be great
													// to put this in an
													// seperate thread
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (mBluetooth.isEnabled())// check if Bluetooth is on or not
		{

			// scan all paired devices to find one live Brainlink device
			findPaired = mBluetooth.findPairedBrainlinkDevice("RN42");

			if (findPaired) {
				connection = mBluetooth.socketConnect(); // if find the paired
															// device, make the
															// connection. This
															// should be put
															// into an seperate
															// thread for it
															// will occupy all
															// resources and
															// block the main
															// thread

				if (connection) {
					initializeBrainLink(); // setup the BrainLink
				}
			} else // no paired device found. Search for new BrainLink device
			{
				mBluetooth.startDiscovery();
				IntentFilter intent = new IntentFilter();
				intent.addAction(BluetoothDevice.ACTION_FOUND);
				intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

				registerReceiver(mReceiver, intent); // Don't forget to
														// unregister during
														// onDestroy

			}
		}
	}

	@Override
	public void onDestroy() {
		if (mBluetooth != null)
			mBluetooth.cencelSocket(); // don't forget to cancel the connection
										// so that brainlink can be connected
										// the next time you start the app

		super.onDestroy();
	}

	private void initializeBrainLink() {
		try {
			mBrainLink = new BrainLink(mBluetooth.getInputStream(),
					mBluetooth.getOutputStream()); // new BrainLink()
			Thread.sleep(500);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mBrainLink.setFullColorLED(255, 0, 0); // test the connection
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() // setup
																		// an
																		// broadcast
																		// receiver
																		// to
																		// find
																		// an
																		// new
																		// BrianLink
																		// device
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getName().toString().contains("RN42")) {
					mBluetooth.addDevice(device);

					findPaired = true;
					connection = mBluetooth.socketConnect();

					initializeBrainLink();

				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				mBluetooth.cancelDiscovery();
			}

			mBluetooth.cancelDiscovery();
		}

	};
}