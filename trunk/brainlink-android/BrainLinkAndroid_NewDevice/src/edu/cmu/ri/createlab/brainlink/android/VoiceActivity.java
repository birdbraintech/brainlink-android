package edu.cmu.ri.createlab.brainlink.android;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import edu.cmu.ri.createlab.brainlink.robots.BrainLinkRobot;
import edu.cmu.ri.createlab.brainlink.robots.robosapien.BossaNova;
import edu.cmu.ri.createlab.brainlink.robots.robosapien.RobotRobosapien;
import edu.cmu.ri.createlab.brainlink.robots.robosapien.WallE;
import edu.cmu.ri.createlab.util.ByteUtils;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class VoiceActivity extends Activity implements OnTouchListener{
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 4321;
    
	private Bundle bundle;

	private BrainLinkRobot mRobot;

	private String mRobotName;
	
	Button start;
	TextView topView;
	ImageView left, right;
	int sx = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //Set full Screen
		initializeWindow();
		
		setContentView(R.layout.act_voice);
		
		
		initialRobot();

		
		
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
		
		start = (Button) findViewById(R.id.btn_startvoice);
		start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				StartVoiceIntent();				
			}
			
		});
	}
	
	private void initializeWindow() {

        //Set full Screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	private void initialRobot() {
		//get the data from MainActivity
		bundle = getIntent().getExtras();

		mRobotName = (String) (bundle.getString(MainActivity.BUNDLE_ROBOT));

		if(mRobotName.equals("walle")) {
			mRobot = (BrainLinkRobot)new WallE();
		}
		else if(mRobotName.equals("robosapien")) {
			mRobot= (BrainLinkRobot)new RobotRobosapien();
		}
		else if(mRobotName.equals("bossanova")) {
			mRobot= (BrainLinkRobot)new BossaNova();
		}	
	}
	void StartVoiceIntent() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Your instruction");
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK)
		{
			
			ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String resultsString = ""; 
			for (int i = 0; i < results.size(); i++)
			{
				resultsString += results.get(i);
				if(results.get(i).contains("stop")) {
					mRobot.moveStop();
					Toast.makeText(this, results.get(i), Toast.LENGTH_LONG).show();
					break;
				} else if(results.get(i).contains("forward") || results.get(i).contains("move")) {
					mRobot.moveForward();
					Toast.makeText(this, results.get(i), Toast.LENGTH_LONG).show();
					break;
				} else if(results.get(i).contains("backward") || results.get(i).contains("back")) {
					mRobot.moveBackward();
					Toast.makeText(this, results.get(i), Toast.LENGTH_LONG).show();
					break;
				} else if(results.get(i).contains("left")) {
					mRobot.moveLeft();
					Toast.makeText(this, results.get(i), Toast.LENGTH_LONG).show();
					break;
				} else if(results.get(i).contains("right")) {
					mRobot.moveRight();
					Toast.makeText(this, results.get(i), Toast.LENGTH_LONG).show();
					break;
				} else if(results.get(i).contains("quit")) {
					Toast.makeText(this, results.get(i), Toast.LENGTH_LONG).show();
					onStop();
					break;
				}
			} 
			
 
			super.onActivityResult(requestCode, resultCode, data);
			StartVoiceIntent();
		}
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
				}
				else  {
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
		Intent i;
		i = new Intent(getApplicationContext(), JoystickActivity.class);
		i.putExtras(bundle);
		startActivity(i);
		finish();			
	}
}
