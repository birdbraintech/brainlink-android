package edu.cmu.ri.createlab.brainlink.android;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

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
	
	private BluetoothDevice brainLinkBluetooth;
	
	private String mRobotName;
	private OutputStream outStream = null;
	private InputStream inStream = null;
	byte[] b = new byte[]{};

	static final byte[] INITIALIZATION_COMMAND = new byte[]{0x49, 0x03, 0x49, 0x02, 0x0B, 0x6D, 0x03, ByteUtils.intToUnsignedByte(0xB6), 0x03, 0x15, 0x02, ByteUtils.intToUnsignedByte(0xD5), 0x00, ByteUtils.intToUnsignedByte(0xB6), 0x03, 0x15, 0x02, ByteUtils.intToUnsignedByte(0xE1), 0x00, 0x00};
    static final byte[] FORWARD = new byte[]{0x69, ByteUtils.intToUnsignedByte(0xAF), ByteUtils.intToUnsignedByte(0xEF), ByteUtils.intToUnsignedByte(0xE8), 0x00, 0x00};

    static final byte[] ROBOT_SAPIENS = new byte[] {0x49, 0x03, 0x49, 0x01, ByteUtils.intToUnsignedByte(0x0D), 0x05, 0x02, 0x08, 0x06, ByteUtils.intToUnsignedByte(0x83), 0x01, ByteUtils.intToUnsignedByte(0xA0), 0x01, ByteUtils.intToUnsignedByte(0xA0)};
    static final byte[] MOVE_FORWARD = new byte[] {0x69, ByteUtils.intToUnsignedByte(0x86),  0x00, 0x00};
    static final byte[] MOVE_BACKWARD = new byte[] {0x69, ByteUtils.intToUnsignedByte(0x87),  0x00, 0x00};
    static final byte[] MOVE_LEFT = new byte[] {0x69, ByteUtils.intToUnsignedByte(0x88),  0x00, 0x00};
    static final byte[] MOVE_RIGHT = new byte[] {0x69, ByteUtils.intToUnsignedByte(0x80),  0x00, 0x00};

    static final byte[] MOVE_STOP = new byte[] {0x69, ByteUtils.intToUnsignedByte(0x8E),  0x00, 0x00};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //Set full Screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		
		//get the data from MainActivity
		bundle = getIntent().getExtras();
		brainLinkBluetooth = (BluetoothDevice) (bundle
				.getParcelable(MainActivity.BUNDLE_BLUETOOTH));
		mRobotName = (String) (bundle.getString(MainActivity.BUNDLE_ROBOT));

		try {
			outStream = MainActivity.btSocket.getOutputStream();
			inStream = MainActivity.btSocket.getInputStream();
			
			outStream.write(ROBOT_SAPIENS);

			inStream.read(b);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(VoiceActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
		
		StartVoiceIntent();
		

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
				if(results.get(i).equals("stop")) {
					MoveStop();
					Toast.makeText(this, results.get(i), Toast.LENGTH_LONG).show();
					break;
				} else if(results.get(i).equals("forward") || results.get(i).equals("forwards")) {
					MoveForward();
					Toast.makeText(this, results.get(i), Toast.LENGTH_LONG).show();
					break;
				} else if(results.get(i).equals("backward") || results.get(i).equals("backwards")) {
					MoveBackward();
					Toast.makeText(this, results.get(i), Toast.LENGTH_LONG).show();
					break;
				} else if(results.get(i).equals("left")) {
					MoveLeft();
					Toast.makeText(this, results.get(i), Toast.LENGTH_LONG).show();
					break;
				} else if(results.get(i).equals("right")) {
					MoveRight();
					Toast.makeText(this, results.get(i), Toast.LENGTH_LONG).show();
					break;
				} else if(results.get(i).equals("quit")) {
					Toast.makeText(this, results.get(i), Toast.LENGTH_LONG).show();
					onStop();
					break;
				}
			} 
			
 
			super.onActivityResult(requestCode, resultCode, data);
			StartVoiceIntent();
		}
	}

	
	private void MoveBackward() {
		try {
			outStream.write(MOVE_BACKWARD);
			inStream.read(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	private void MoveForward() {
		try {
			outStream.write(MOVE_FORWARD);
			inStream.read(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	private void MoveLeft() {
		try {
			outStream.write(MOVE_LEFT);
			inStream.read(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private void MoveRight() {
		try {
			outStream.write(MOVE_RIGHT);
			inStream.read(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void MoveStop() {
		try {
			outStream.write(MOVE_STOP);
			inStream.read(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
}
