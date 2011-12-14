package brainlink.tiltLEDControl;

import brainlink.tiltLEDControl.R;

import java.io.IOException;

import edu.cmu.ri.createlab.brainlink.BluetoothConnection;
import edu.cmu.ri.createlab.brainlink.BrainLink;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

// Very simple app to demonstrate communication with Brainlink. Uses a phone's accelerometer to set Brainlink's LED
// Author: Tom Lauwers tlauwers@birdbraintechnologies.com

public class TiltLEDControlActivity extends Activity implements SensorEventListener, OnTouchListener {
	/** Called when the activity is first created. */

	private BluetoothConnection mBluetooth;
	private BrainLink mBrainLink;
	private boolean bStartButtonPressed=false;
	private SensorManager sensorManager = null;
	private float maxRange = 0;
	private TextView t=null;
	private TextView colField=null;	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);     //  Fixed Portrait orientation
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// create the BluetoothConnection instance
		mBluetooth = new BluetoothConnection();
	    t=new TextView(this); 
	    colField = new TextView(this);

	    t=(TextView)findViewById(R.id.statusText); 
	    colField = (TextView)findViewById(R.id.colorField); 

		int mConnectionState = mBluetooth.BluetoothConnectionAuto("RN42");	// give the Brainlink Bluetooth component's name
		if (mConnectionState == BluetoothConnection.STATE_BRAINLINK_CONNECTION_SUCCESS) {
			try {
				mBrainLink = new BrainLink(mBluetooth.getInputStream(),
						mBluetooth.getOutputStream());
				Thread.sleep(500);

				 //If the button is pressed, just set a flag. The rest is taken care of by the sensorChanged handler
				// If the button is up, turn off the LED
		        Button btnStart = (Button) findViewById(R.id.btnStart);
		        btnStart.setOnTouchListener(new View.OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						switch(event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							bStartButtonPressed = true;
							break;
						case MotionEvent.ACTION_UP:
							bStartButtonPressed = false;
							mBrainLink.setFullColorLED(0,0,0);
							break;
						}
						return false;
					}		});       
		        
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
	protected void onResume() {
		super.onResume();
		
		
		// ...and the accelerometer sensor
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		maxRange = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).getMaximumRange();
	}

	// Prints out the accelerometer values (x, y, z), and sets the LED, but only if the button is pressed
	public void onSensorChanged(SensorEvent sensorEvent) {
		synchronized (this) {
		
			if(bStartButtonPressed) {
				int greenLED = (int) (128+(sensorEvent.values[0]*127)/12);
				int blueLED =  (int) (128+(sensorEvent.values[1]*127)/12);
				t.setText("Green: " + greenLED + " Blue: " + blueLED + " Range: " + maxRange + ":" + sensorEvent.values[0] + ":"  + sensorEvent.values[1] + ":" + sensorEvent.values[2]);
				colField.setBackgroundColor(Color.rgb(0, greenLED, blueLED));
				mBrainLink.setFullColorLED(0,greenLED,blueLED);
			}
		}
	}	

	@Override
	protected void onStop() {
		// Unregister the listener
		sensorManager.unregisterListener(this);
		super.onStop();
	}	
	
	@Override
	public void onDestroy() {
		if (mBluetooth != null)
			mBluetooth.cancelSocket(); // don't forget to cancel the connection
										// so that brainlink can be connected
										// the next time you start the app

		super.onDestroy();
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}