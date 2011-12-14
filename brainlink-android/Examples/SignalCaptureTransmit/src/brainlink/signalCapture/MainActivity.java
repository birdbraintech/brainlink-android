package brainlink.signalCapture;

import brainlink.signalCapture.R;

import java.io.IOException;

import edu.cmu.ri.createlab.brainlink.BluetoothConnection;
import edu.cmu.ri.createlab.brainlink.BrainLink;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

// A simple app to capture, store, and transmit IR signals. The MainActivity connects to Brainlink and
// then just presents two buttons to the user - Capture or Send.
// Author: Tom Lauwers, tlauwers@birdbraintechnologies.com

public class MainActivity extends Activity{

	public static BluetoothConnection mBluetooth;
	public static BrainLink mBrainLink;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);     //  Fixed Portrait orientation


		// create the BluetoothConnection instance
		mBluetooth = new BluetoothConnection();

		
		int mConnectionState = mBluetooth.BluetoothConnectionAuto("RN42");	// give the Brainlink Bluetooth component's name
		if (mConnectionState == BluetoothConnection.STATE_BRAINLINK_CONNECTION_SUCCESS) {
			try {
				// Create a new instance of Brainlink
				mBrainLink = new BrainLink(mBluetooth.getInputStream(),
						mBluetooth.getOutputStream());
				Thread.sleep(500);

				// turn on the led on the brainlink for 0.5 seconds to show we're connected
				mBrainLink.setFullColorLED(255, 0, 0);
				Thread.sleep(500);
				mBrainLink.setFullColorLED(0, 0, 0);

			}
			 catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		// Handler if user presses send button
        final Button toSend = (Button) findViewById(R.id.sendButton);
        toSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
           	 Intent i = new Intent(MainActivity.this,SendSignalScreen.class); 
           	 startActivity(i); 
            }
        });
        
        // Handler if user presses capture button
        final Button toCapture = (Button) findViewById(R.id.captureButton);
        toCapture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
           	 Intent i = new Intent(MainActivity.this,ReceiveSignalScreen.class); 
           	 startActivity(i); 
            }
        });

	}
	
	@Override
	public void onDestroy() {
		if (mBluetooth != null)
			mBluetooth.cancelSocket(); // don't forget to cancel the connection
										// so that brainlink can be connected
										// the next time you start the app

		super.onDestroy();
	}
}
