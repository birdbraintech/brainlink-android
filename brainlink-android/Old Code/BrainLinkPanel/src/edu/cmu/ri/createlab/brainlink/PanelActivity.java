package edu.cmu.ri.createlab.brainlink;

import java.io.IOException;

import android.R.string;
import android.app.Activity;
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

public class PanelActivity extends Activity{
	
	private BrainLink mBrainLink;
	
	private TextView mDisplay;
	private ImageView mImgTitle;
	
	private Button mLightButton;
	private Button mAccelerometerButton;
	private Button mBatteryButton;
	private Button mSwitchButton;
	private Button mTemperatureButton;
	private Button mAnalogButton;
	private Button mSoundButton;
	private SeekBar soundBar;
	private SeekBar[] rgbBar = new SeekBar[3];
	
	private int[] rgbValue = new int[3];
	private int soundValue=0;
	
	private boolean mSwitchStatus = false;
	
	OnClickListener switchListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if(!mSwitchStatus) {
				mSwitchStatus = mBrainLink.connectionOn();
				if(mSwitchStatus)
						mSwitchButton.setText("ON");
				
			}
			else {
				mSwitchStatus = mBrainLink.connectionOff();
				if(!mSwitchStatus)
					mSwitchButton.setText("OFF");				
			}
			
		}
		
	};
	OnClickListener lightListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(mSwitchStatus) {
				int[] data = mBrainLink.getPhotoresistors();
				mDisplay.setText("L: " + 
						Integer.toString(data[0])+
						" R: " +
						Integer.toString(data[1]));
			}
		}
		
	};
	
	OnClickListener accelerometerListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(mSwitchStatus) {
				int[] data = mBrainLink.getAccelerometer();
				mDisplay.setText("X: " + 
						Integer.toString(data[0]) +
						" Y: " +
						Integer.toString(data[1]) +
						" Z: " +
						Integer.toString(data[2]));
			}
		}		
	};
	
	OnClickListener batteryListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(mSwitchStatus) {
				int data = mBrainLink.getBatteryVoltage();
				mDisplay.setText("Battery: " + 
						Integer.toString(data));
			}			
		}
	};
	
	OnClickListener temperatureListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(mSwitchStatus) {
				int data = mBrainLink.getThermistor();
				mDisplay.setText("Temperature: " + 
						Integer.toString(data));
			}			
		}
	};

	OnClickListener analogListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(mSwitchStatus) {
				int[] data = mBrainLink.getAnalogInputs();
				mDisplay.setText("Analog: " + 
						Integer.toString(data[0])+
						Integer.toString(data[1])+
						Integer.toString(data[2])+
						Integer.toString(data[3])+
						Integer.toString(data[4]));
			}	
		}
		
	};
	
	OnClickListener soundListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(mSwitchStatus && mBrainLink.getSpeakerStatus()) {
				mBrainLink.turnOffSpeaker();
			}	
			else if(mSwitchStatus && !mBrainLink.getSpeakerStatus()) {
				mBrainLink.turnOnSpeaker();
			}
		}
		
	};
	
	OnSeekBarChangeListener soundBarListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			soundValue = progress;		
			if(mBrainLink.getSpeakerStatus())
				mBrainLink.playTone(soundValue);
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
        
        mSwitchButton = (Button)findViewById(R.id.btn_switch);
        mSwitchButton.setOnClickListener(switchListener);
        
        mLightButton = (Button)findViewById(R.id.btn_lightsensor);
        mLightButton.setOnClickListener(lightListener);
        
        mBatteryButton = (Button)findViewById(R.id.btn_battery);
        mBatteryButton.setOnClickListener(batteryListener);
        
        mAccelerometerButton = (Button)findViewById(R.id.btn_accelerometersensor);
        mAccelerometerButton.setOnClickListener(accelerometerListener);
        
        mTemperatureButton = (Button)findViewById(R.id.btn_temperaturesensor);
        mTemperatureButton.setOnClickListener(temperatureListener);
        
        mAnalogButton = (Button)findViewById(R.id.btn_analog);
        mAnalogButton.setOnClickListener(analogListener);
        
        mSoundButton = (Button)findViewById(R.id.btn_sound);
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
