package edu.cmu.ri.createlab.brainlink.android;


import edu.cmu.ri.createlab.brainlink.BrainLink;
import edu.cmu.ri.createlab.brainlink.BrainLinkConstants;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.view.View.OnTouchListener;
import android.widget.Button;

import android.widget.TextView;

public class JoystickActivity extends Activity implements OnTouchListener{

	private Bundle bundle;
	private String mRobotName;
	JoystickView joystickView;
	
	TextView topView;
	Button btnLeft, btnRight;
	int sx = 0;
	
	private BrainLink mRobot;
	private int mRobotCondition;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = getIntent().getExtras();
        mRobotName = (String) (bundle.getString(MainActivity.BUNDLE_ROBOT));

        initializeWindow();
        
		initializeUI();
		
		initializeRobot();


		
    }
    
	void left()
	{
		if(mRobotCondition!=BrainLinkConstants.CONDITION_LEFT) {
			mRobot.transmitIRSignal("left");
			setRobotCondition(BrainLinkConstants.CONDITION_LEFT);
		}
	}
	
	void right()
	{
		if(mRobotCondition!=BrainLinkConstants.CONDITION_RIGHT) {
			mRobot.transmitIRSignal("right");
			setRobotCondition(BrainLinkConstants.CONDITION_RIGHT);
		}
	}
	
	void stop()
	{
		mRobot.transmitIRSignal("stop");
		setRobotCondition(BrainLinkConstants.CONDITION_STOP);
	}
	
	void forward()
	{
		if(mRobotCondition!=BrainLinkConstants.CONDITION_FORWARD) {
			mRobot.transmitIRSignal("forward");
			setRobotCondition(BrainLinkConstants.CONDITION_FORWARD);
		}
	}
	
	void backward()
	{
		if(mRobotCondition!=BrainLinkConstants.CONDITION_BACKWARD) {
			mRobot.transmitIRSignal("backward");
			setRobotCondition(BrainLinkConstants.CONDITION_BACKWARD);
		}
	}
	
	private void setRobotCondition(int c) {
		mRobotCondition = c;				
	}
	
	private void initializeRobot() {
		bundle = getIntent().getExtras();
		mRobotName =(String) (bundle.getString(MainActivity.BUNDLE_ROBOT));
		mRobot= MainActivity.mBrainLink;
		mRobot.initializeDevice(mRobotName, true);
	}
	
	private void initializeUI() {
        setContentView(R.layout.act_joystick);
        
        joystickView = (JoystickView) findViewById(R.id.joystickview);
        //joyStickControl.setOnTouchListener(this);
        joystickView.setFocusableInTouchMode(true);     
        
        topView = (TextView) findViewById(R.id.topview);
        topView.setOnTouchListener(this);
		
		
		btnLeft = (Button)findViewById(R.id.btn_left);
		btnLeft.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shiftToLeftAct();
			}
		});
		
//		btnRight = (Button)findViewById(R.id.btn_right);
//		btnRight.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				shiftToRightAct();
//			}
//		});
		
	}


	private void initializeWindow() {

        //Set full Screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		requestWindowFeature(Window.FEATURE_NO_TITLE);
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
				if (event.getX() - sx > 0) {
					shiftToLeftAct();
				} else {
					shiftToRightAct();
				}
				break;
			}
		}
		return true;
	}
	


	
	private void shiftToLeftAct() {
		Intent i;
		i = new Intent(getApplicationContext(), PuppetActivity.class);
		i.putExtras(bundle);
		startActivity(i);
		overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
		finish();
	}
	
	private void shiftToRightAct() {
//		Intent i;
//		i = new Intent(getApplicationContext(), NativeActivity.class);
//		i.putExtras(bundle);
//		startActivity(i);
//		finish();			
	}
}
