package brainlink.tvController;

//Very simple app to demonstrate communication with Brainlink. Maps five EEPROM signals to five custom buttons on screen.
//Use the Signal Capturer app to store signals to EEPROM.
//Author: Tom Lauwers tlauwers@birdbraintechnologies.com

import java.io.IOException;

import brainlink.tvController.R;

import edu.cmu.ri.createlab.brainlink.BluetoothConnection;
import edu.cmu.ri.createlab.brainlink.BrainLink;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TVController extends Activity {
	/** Called when the activity is first created. */

	private BluetoothConnection mBluetooth;
	private BrainLink mBrainLink;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);     //  Fixed Portrait orientation
		
		// create the BluetoothConnection instance
		mBluetooth = new BluetoothConnection();

		int mConnectionState = mBluetooth.BluetoothConnectionAuto("RN42");	// give the Brainlink Bluetooth component's name
		if (mConnectionState == BluetoothConnection.STATE_BRAINLINK_CONNECTION_SUCCESS) {
			try {
				mBrainLink = new BrainLink(mBluetooth.getInputStream(),
						mBluetooth.getOutputStream());
				// turn on the led on the brainlink to test the connection
				mBrainLink.setFullColorLED(255, 0, 0);
				Thread.sleep(500);
				mBrainLink.setFullColorLED(0,0,0);
				 final TextView statusText = (TextView)findViewById(R.id.status_text);
			        
				 // Power signal stored to EEPROM position 1
				final Button power = (Button) findViewById(R.id.power_button);
		         power.setOnClickListener(new View.OnClickListener() {
		             public void onClick(View v) {
		            	statusText.setText("Power");
		            	mBrainLink.playIR(0, 0);
		 				mBrainLink.setFullColorLED(255, 0, 0);
		 				mBrainLink.sleep(500);
						mBrainLink.setFullColorLED(0, 0, 0);
		             }
		         });				

		         // Channel up stored to 2
				 final Button channelUp = (Button) findViewById(R.id.channel_up_button);
				 channelUp.setOnClickListener(new View.OnClickListener() {
		             public void onClick(View v) {
			            statusText.setText("Channel Up");
		            	mBrainLink.playIR(1, 0);
		 				mBrainLink.setFullColorLED(0, 255, 255);
		 				mBrainLink.sleep(500);
						mBrainLink.setFullColorLED(0, 0, 0);
		             }
		         });
		         
				 // Channel down stored to 3
		         final Button channelDown = (Button) findViewById(R.id.channel_down_button);
		         channelDown.setOnClickListener(new View.OnClickListener() {
		             public void onClick(View v) {
		            	statusText.setText("Channel Down");
		            	mBrainLink.playIR(2, 0);
		 				mBrainLink.setFullColorLED(0, 0, 255);
		 				mBrainLink.sleep(500);
						mBrainLink.setFullColorLED(0, 0, 0);
		             }
		         });	
		         
		         // Volume up stored to 4
		         final Button volumeUp = (Button) findViewById(R.id.volume_up_button);
		         volumeUp.setOnClickListener(new View.OnClickListener() {
		             public void onClick(View v) {
		            	statusText.setText("Volume Up");
		            	mBrainLink.playIR(3, 0);
		 				mBrainLink.setFullColorLED(255, 0, 255);
		 				mBrainLink.sleep(500);
						mBrainLink.setFullColorLED(0, 0, 0);
		             }
		         });
		         
		         // Volume down stored 5
		         final Button volumeDown = (Button) findViewById(R.id.volume_down_button);
		         volumeDown.setOnClickListener(new View.OnClickListener() {
		             public void onClick(View v) {
		            	statusText.setText("Volume Down");
		            	mBrainLink.playIR(4, 0);
		 				mBrainLink.setFullColorLED(0, 255, 0);
		 				mBrainLink.sleep(500);
						mBrainLink.setFullColorLED(0, 0, 0);
		             }
		         });		         

				
				

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
			mBluetooth.cancelSocket(); // don't forget to cancel the connection
										// so that brainlink can be connected
										// the next time you start the app

		super.onDestroy();
	}
	
}