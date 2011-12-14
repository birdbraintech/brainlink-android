package brainlink.commandPanel;

import java.io.IOException;

import brainlink.commandPanel.R;
import edu.cmu.ri.createlab.brainlink.BluetoothConnection;
import edu.cmu.ri.createlab.brainlink.BrainLink;
import edu.cmu.ri.createlab.brainlink.BrainLinkFileManipulator;

import android.R.string;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class PanelActivity extends Activity{
	
	private BrainLink mBrainLink;
	
	private TextView mDisplay;
	
	private Button mLightButton;
	private Button mAccelerometerButton;
	private Button mBatteryButton;
	private Button mAnalogButton;
	private ToggleButton mSoundButton;
	private SeekBar soundBar;
	private SeekBar[] rgbBar = new SeekBar[3];
	
	private int[] rgbValue = new int[3];
	private int soundValue=100;
	
	private int MAX_RETRY = 4;
	
	OnClickListener lightListener = new OnClickListener() {

		// Prints out the light sensor value
		// Note that we try up to 4 times to retrieve a non-zero value. 
		// Sometimes a value is lost even when in close range.
		@Override
		public void onClick(View v) {
			int retry = 0;
			Integer data;
			do {
				retry++;
				data = mBrainLink.getLightSensor();
				if(data == null) {
					data = 0;
				}
			}
			while((data == 0) && (retry <= MAX_RETRY));
			// If we still have no data, declare that we're out of range.
			if(retry > MAX_RETRY) {
				mDisplay.setText("Receive error, out of range?");
			}
			else {
				mDisplay.setText("Light sensor: " + Integer.toString(data));
			}
		}
		
	};
	
	// Prints the accelerometer data. Retries up to four times.
	OnClickListener accelerometerListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int retry = 0;
			boolean repeatLoop;
			double[] data;
			do {
				repeatLoop = false;
				retry++;
				data = mBrainLink.getAccelerometerValuesInGs();
				if(data == null) 
					repeatLoop = true;
				else if(data[0] == 0 && data[1] == 0 && data[2] == 0) 
					repeatLoop = true;
			} while(repeatLoop && retry<=MAX_RETRY);
			
			if(retry > MAX_RETRY) {
				mDisplay.setText("Receive error, out of range?");
			}
			else {
				mDisplay.setText("X: " + 
						Double.toString(data[0]) +
						" \nY: " +
						Double.toString(data[1]) +
						" \nZ: " +
						Double.toString(data[2]));
			}
		}		
	};
	
	// Displays battery voltage in mV, retries up to 4 times.
	OnClickListener batteryListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int retry = 0;
			Integer data;
			do {
				retry++;
				data = mBrainLink.getBatteryVoltage();
				if(data == null) {
					data = 0;
				}
			}
			while(((data == 0)) && (retry <= MAX_RETRY));
			if(retry > MAX_RETRY) {
				mDisplay.setText("Receive error, out of range?");
			}
			else {
				mDisplay.setText("Battery: " + Integer.toString(data) + " mV");
			}
		}
	};

	// Displays the 0-255 values of the Analog inputs. Retries up to 4 times.
	OnClickListener analogListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
				int[] data;
				boolean repeatLoop;
				int retry = 0;
				do {
					repeatLoop = false;
					retry++;
					data = mBrainLink.getAnalogInputs();
					
					if(data==null) {
						repeatLoop = true;
					}
					else if(data[0] == 0 && data[1] == 0 && data[2] == 0 && data[3] == 0 && data[4] == 0 && data[5] == 0) {
						repeatLoop = true;
					}
					
				} while(repeatLoop && retry<=MAX_RETRY);
				
				if(retry > MAX_RETRY) {
					mDisplay.setText("Receive error, out of range?");
				}
				else {
					mDisplay.setText("Analog: \n" + 
						Integer.toString(data[0])+ " " +
						Integer.toString(data[1])+ " " +
						Integer.toString(data[2])+ "\n" +
						Integer.toString(data[3])+ " " +
						Integer.toString(data[4])+ " " +
						Integer.toString(data[5]));
				}
			}
	};
	
	// Toggles the buzzer
	OnClickListener soundListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(!mSoundButton.isChecked()) {
				mBrainLink.turnOffSpeaker();
			}
			else {
				mBrainLink.playTone(soundValue);
			}
		}
		
	};
	
	// Sets the frequency of the buzzer
	OnSeekBarChangeListener soundBarListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// converting linear bar to an exponential scale
			soundValue = 40000/(257-progress);
			if(mSoundButton.isChecked()) {
				mBrainLink.playTone(soundValue);
			}
		}


		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	// These three listeners control the LED color, r for red, g for green, b for blue
	OnSeekBarChangeListener rListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			rgbValue[0] = progress;		
			mBrainLink.setFullColorLED(rgbValue[0], rgbValue[1], rgbValue[2]);
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {}

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {}
		
	};
	OnSeekBarChangeListener gListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			rgbValue[1] = progress;
			mBrainLink.setFullColorLED(rgbValue[0], rgbValue[1], rgbValue[2]);
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {}

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {}
		
	};
	OnSeekBarChangeListener bListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			rgbValue[2] = progress;
			mBrainLink.setFullColorLED(rgbValue[0], rgbValue[1], rgbValue[2]);			
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {}

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {}
		
	};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Set full Screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
        setContentView(R.layout.panel);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  
        
        // Set up all buttons and seekbars
        mLightButton = (Button)findViewById(R.id.btn_lightsensor);
        mLightButton.setOnClickListener(lightListener);
        
        mBatteryButton = (Button)findViewById(R.id.btn_battery);
        mBatteryButton.setOnClickListener(batteryListener);
        
        mAccelerometerButton = (Button)findViewById(R.id.btn_accelerometersensor);
        mAccelerometerButton.setOnClickListener(accelerometerListener);
        
        mAnalogButton = (Button)findViewById(R.id.btn_analog);
        mAnalogButton.setOnClickListener(analogListener);
        
        mSoundButton = (ToggleButton)findViewById(R.id.sound_tgl);
        mSoundButton.setOnClickListener(soundListener);
        
        soundBar = (SeekBar)findViewById(R.id.soundbar);
        soundBar.setOnSeekBarChangeListener(soundBarListener);
        
        rgbBar[0] = (SeekBar)findViewById(R.id.ledr);
        rgbBar[0].setOnSeekBarChangeListener(rListener);
        
        rgbBar[1] = (SeekBar)findViewById(R.id.ledg);
        rgbBar[1].setOnSeekBarChangeListener(gListener);  
        
        rgbBar[2] = (SeekBar)findViewById(R.id.ledb);
        rgbBar[2].setOnSeekBarChangeListener(bListener); 
        
        mDisplay = (TextView)findViewById(R.id.display);
        try {
			mBrainLink = new BrainLink(MainActivity.mBluetooth.getInputStream(), MainActivity.mBluetooth.getOutputStream());
		} catch (IOException e) {}
    }
    
}
