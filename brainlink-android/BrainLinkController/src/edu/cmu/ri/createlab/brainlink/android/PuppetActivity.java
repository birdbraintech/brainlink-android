package edu.cmu.ri.createlab.brainlink.android;



import edu.cmu.ri.createlab.brainlink.BrainLink;


import android.app.Activity;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class PuppetActivity extends Activity implements SensorEventListener, OnTouchListener {

	private boolean bStartButtonPressed =false;

	private BrainLink mRobot;
	private Bundle bundle;
	private String mRobotName;
	int sx = 0;
	private SensorManager sensorManager = null;
	
	Button btnStart, btnRight;
	TextView topView;

	byte[] b = new byte[]{};

    public static final int CONDITION_STOP = 0;
    public static final int CONDITION_FORWARD = 1;
    public static final int CONDITION_BACKWARD = 2;
    public static final int CONDITION_LEFT = 3;
    public static final int CONDITION_RIGHT = 4;
    private int mRobotCondition;
	

    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//set full screen
		initializeWindow();
		
		initializeRobot();
		
		initializeSensor();

		initializeUI();
	
	}

	private void initializeUI() {
		btnStart = (Button)findViewById(R.id.btn_startpuppet);
		btnStart.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					bStartButtonPressed = true;
					btnStart.setText("Stop");
					break;
				case MotionEvent.ACTION_UP:
					bStartButtonPressed = false;
					mRobot.transmitIRSignal("stop");
					setRobotCondition(mRobotCondition);
					btnStart.setText("Start");
					break;
				}
				return false;
			}		
		});
		
		btnRight = (Button)findViewById(R.id.btn_right);
		btnRight.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shiftToRightAct();
			}
		});
		
		topView = (TextView) findViewById(R.id.topview);
		topView.setOnTouchListener(this);
		
		
	}

	private void setRobotCondition(int c) {
		mRobotCondition = c;				
	}
	
	private void initializeSensor() {
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		
		setContentView(R.layout.act_puppet);

		
	}

	private void initializeRobot() {
		//get the data from MainActivity
		bundle = getIntent().getExtras();

		mRobotName = (String) (bundle.getString(MainActivity.BUNDLE_ROBOT));
		
		mRobot = MainActivity.mBrainLink;
		mRobot.initializeDevice(mRobotName, true);

	}

	private void initializeWindow() {

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
				if(mRobotCondition!=CONDITION_FORWARD) {
					mRobot.transmitIRSignal("forward");
					setRobotCondition(CONDITION_FORWARD);
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(bStartButtonPressed == true && sensorEvent.values[1]<-30) {
				if(mRobotCondition!=CONDITION_BACKWARD) {
					mRobot.transmitIRSignal("backward");
					setRobotCondition(CONDITION_BACKWARD);
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(bStartButtonPressed == true && sensorEvent.values[2]> 30) {
				if(mRobotCondition!=CONDITION_LEFT) {
					mRobot.transmitIRSignal("left");
					setRobotCondition(CONDITION_LEFT);
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
			if(bStartButtonPressed == true && sensorEvent.values[2]<-30) {
				if(mRobotCondition!=CONDITION_RIGHT) {
					mRobot.transmitIRSignal("right");
					setRobotCondition(CONDITION_RIGHT);
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
