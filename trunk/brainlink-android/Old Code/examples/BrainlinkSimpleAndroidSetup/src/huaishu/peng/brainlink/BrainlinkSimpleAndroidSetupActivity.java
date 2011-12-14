package huaishu.peng.brainlink;

import java.io.IOException;

import edu.cmu.ri.createlab.brainlink.BluetoothConnection;
import edu.cmu.ri.createlab.brainlink.BrainLink;
import android.app.Activity;
import android.os.Bundle;

public class BrainlinkSimpleAndroidSetupActivity extends Activity {
	/** Called when the activity is first created. */

	private BluetoothConnection mBluetooth;
	private BrainLink mBrainLink;

	
	// an app to demo the simple ways to connect brainlink in android
	// import the external jar file BrainLinkAndroidLib.jar
	// created by Huaishu Peng
	// www.huaishu.me
	// 11/6/2011
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// create the BluetoothConnection instance
		mBluetooth = new BluetoothConnection();

		// the simple way to setup bluetooth and connect to brainlink device.
		// notice: a. make sure you have turned on the brainlink device.
		// b. make sure the brainlink has already paired with the cellphone
		// c. no need to turn on Bluetooth in advance
		// d. modify the AndroidManifest.xml to add permission for bluetooth and
		// sd card

		int mConnectionState = mBluetooth.BluetoothConnectionAuto("RN42");	// give the Brainlink Bluetooth component's name
		if (mConnectionState == BluetoothConnection.STATE_BRAINLINK_CONNCCTION_SUCCESS) {
			try {
				mBrainLink = new BrainLink(mBluetooth.getInputStream(),
						mBluetooth.getOutputStream());
				Thread.sleep(500);

				// ture on the led on the brainlink to test the connection
				mBrainLink.setFullColorLED(255, 0, 0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
}