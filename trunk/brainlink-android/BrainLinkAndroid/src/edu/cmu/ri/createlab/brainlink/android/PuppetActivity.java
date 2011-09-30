package edu.cmu.ri.createlab.brainlink.android;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import edu.cmu.ri.createlab.brainlink.robots.BrainLinkRobot;
//import edu.cmu.ri.createlab.brainlink.robots.robosapien.BossaNova;
import edu.cmu.ri.createlab.brainlink.robots.robosapien.RobotRobosapien;
//import edu.cmu.ri.createlab.brainlink.robots.robosapien.WallE;
import edu.cmu.ri.createlab.util.ByteUtils;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class PuppetActivity extends Activity implements SensorEventListener, OnTouchListener {

	private boolean bStartButtonPressed =false;

	private BrainLinkRobot mRobot;	
	private Bundle bundle;
	private String mRobotName;
	int sx = 0;
	private SensorManager sensorManager = null;
	
	Button start;
	TextView topView;
	ImageView right;


	byte[] b = new byte[]{};

    

    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//set full screen
		initialWindow();
		
		initialRobot();
		
		initialSensor();
		
		start = (Button)findViewById(R.id.btn_startpuppet);
		start.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					bStartButtonPressed = true;
					start.setText("Stop");
					break;
				case MotionEvent.ACTION_UP:
					bStartButtonPressed = false;
					mRobot.moveStop();
					start.setText("Start");
					break;
				}
				return false;
			}
			
		});
		
		topView = (TextView) findViewById(R.id.topview);
		topView.setOnTouchListener(this);
		

		right = (ImageView) findViewById(R.id.img_right);
		right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				shiftToRightAct();
			}
		});

	}

	private void initialSensor() {
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		
		setContentView(R.layout.act_puppet);

		
	}

	private void initialRobot() {
		//get the data from MainActivity
		bundle = getIntent().getExtras();

		mRobotName = (String) (bundle.getString(MainActivity.BUNDLE_ROBOT));

//		if(mRobotName.equals("walle")) {
//			mRobot = (BrainLinkRobot)new WallE();
//		}
		if(mRobotName.equals("robosapien")) {
			mRobot= (BrainLinkRobot)new RobotRobosapien();
		}
//		else if(mRobotName.equals("bossanova")) {
//			mRobot= (BrainLinkRobot)new BossaNova();
//		}
	}

	private void initialWindow() {

        //Set full Screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	
	public void onSensorChanged(SensorEvent sensorEvent) {
		synchronized (this) {
			if (bStartButtonPressed == true && sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {

			}
			
			if(bStartButtonPressed==true && sensorEvent.values[1]>30) {
				mRobot.moveForward();
			}
			if(bStartButtonPressed == true && sensorEvent.values[1]<-30) {
				mRobot.moveBackward();
			}
			if(bStartButtonPressed == true && sensorEvent.values[2]> 30) {
				mRobot.moveLeft();
			}			
			if(bStartButtonPressed == true && sensorEvent.values[2]<-30) {
				mRobot.moveRight();
			}
		}
	}



	@Override
	protected void onResume() {
		super.onResume();
		
		
		// ...and the orientation sensor
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onStop() {
		// Unregister the listener
		sensorManager.unregisterListener(this);
		super.onStop();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	
	private void shiftToRightAct() {
//		Intent i;
//		i = new Intent(getApplicationContext(), VoiceActivity.class);
//		i.putExtras(bundle);
//		startActivity(i);
//		finish();			
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		final int action = event.getAction();
		Intent i;
		switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN: {
				sx = (int) event.getX();
				break;
			}
			case MotionEvent.ACTION_UP: {
				if (event.getX() - sx > 0) {}
				else  {
					shiftToRightAct();					
				}
				break;
			}
		}
		return true;
	}
}
