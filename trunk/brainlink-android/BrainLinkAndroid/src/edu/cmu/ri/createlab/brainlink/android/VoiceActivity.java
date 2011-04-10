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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class VoiceActivity extends Activity{
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 4321;
    
	private Bundle bundle;

	private BrainLinkRobot mRobot;

	private String mRobotName;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //Set full Screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		
		initialRobot();
		
		StartVoiceIntent();
		

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

}
