package edu.cmu.ri.createlab.brainlink.android;

import java.io.InputStream;
import java.io.OutputStream;
import edu.cmu.ri.createlab.brainlink.robots.BrainLinkRobot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

public class JoystickActivity extends Activity implements OnTouchListener{

	private BrainLinkRobot mRobot;	
	private Bundle bundle;
	private String mRobotName;
	JoystickView joystickControl;
	
	TextView topView;
	ImageView left, right;
	int sx = 0;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = getIntent().getExtras();
        mRobotName = (String) (bundle.getString(MainActivity.BUNDLE_ROBOT));
        
        initializeWindow();
		
		initializeView();
		
		

    }
    
	
	private void initializeWindow() {

        //Set full Screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
    private void initializeView() {
        setContentView(R.layout.act_joystick);
        
        joystickControl = (JoystickView) findViewById(R.id.joystickview);
        //joyStickControl.setOnTouchListener(this);
        joystickControl.setFocusableInTouchMode(true);     
        
        topView = (TextView) findViewById(R.id.topview);
        topView.setOnTouchListener(this);
        
		left = (ImageView) findViewById(R.id.img_left);
		right = (ImageView) findViewById(R.id.img_right);
		
		left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				shiftToLeftAct();
			}
		});
		
		right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				shiftToRightAct();
			}
		});
		
        joystickControl.initialRobot(mRobotName);
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
		i = new Intent(getApplicationContext(), VoiceActivity.class);
		i.putExtras(bundle);
		startActivity(i);
		overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
		finish();
	}
	
	private void shiftToRightAct() {
		Intent i;
		i = new Intent(getApplicationContext(), MimicActivity.class);
		i.putExtras(bundle);
		startActivity(i);
		finish();			
	}
}
