package edu.cmu.ri.createlab.brainlink.android;

import java.io.InputStream;
import java.io.OutputStream;

import edu.cmu.ri.createlab.brainlink.robots.BrainLinkRobot;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class JoystickActivity extends Activity implements OnTouchListener{

	private BrainLinkRobot mRobot;	
	private Bundle bundle;
	private String mRobotName;
    
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
        
        final JoystickView joystickControl = (JoystickView) findViewById(R.id.joystickview);
        //joyStickControl.setOnTouchListener(this);
        joystickControl.setFocusableInTouchMode(true);     
        
        TextView topView = (TextView) findViewById(R.id.topview);
        topView.setOnTouchListener(this);
        
        joystickControl.initialRobot(mRobotName);
    }
    
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}

}
