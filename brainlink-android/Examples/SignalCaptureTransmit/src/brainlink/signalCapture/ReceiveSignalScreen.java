package brainlink.signalCapture;

import brainlink.signalCapture.R;

import java.io.IOException;

import edu.cmu.ri.createlab.brainlink.BluetoothConnection;
import edu.cmu.ri.createlab.brainlink.BrainLink;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

// This screen allows the user to capture signals and store them to 1 of 5 EEPROM locations.

public class ReceiveSignalScreen extends Activity {
	/** Called when the activity is first created. */
	private BrainLink mBrainLink;
	private int scrollBarValue = 0;
    private boolean validSignalCaptured = true;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.receive_signal);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);     //  Fixed Portrait orientation

		mBrainLink = MainActivity.mBrainLink;
		
		// Capture button handler. Checks if the data captured looks valid (tries to filter out noisy,
		// invalid captures). If it's good, sets the LED to green and prints "Capture Succeeded", otherwise
		// turns the LED red and prints "Capture Failed".
		final Button captureButton = (Button) findViewById(R.id.Capture);
		 captureButton.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
            	 final TextView captureText = (TextView)findViewById(R.id.CaptureText);
            	 mBrainLink.setFullColorLED(0,0,0);
            	int[] values = mBrainLink.recordIR();
            	
            	if(values != null) {
            		// If we've received more than 8 values, the signal is probably good
					if(values.length > 8) {
						validSignalCaptured = true;
						captureText.setText("   Capture succeeded");
						mBrainLink.setFullColorLED(0,255,0);
					}
					else {
						validSignalCaptured = false;
						captureText.setText("  Capture failed: Interference");
						mBrainLink.setFullColorLED(255,0,0);
					}
				}
				else {
					validSignalCaptured = false;
					captureText.setText("  Capture failed: No signal detected");
					mBrainLink.setFullColorLED(255,0,0);
				}
             }
         });				
	     final RadioGroup mRadioGroup = (RadioGroup) findViewById(R.id.signalGroup);
	   
	     // Stores the most recently captured signal to 1 of 5 EEPROM positions.
		 final Button storeButton = (Button) findViewById(R.id.storeButton);
		 storeButton.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
            	 final TextView storeText = (TextView)findViewById(R.id.storeText);
            	 if(validSignalCaptured) {
            		 // This converts the selection to a value of 0 to 4 (kinda nasty)
            		 int selection = mRadioGroup.getCheckedRadioButtonId()-R.id.radio1;
            		 // Stores the signal to an EEPROM location on Brainlink
            		 mBrainLink.storeIR(selection);
            		 // Prints out which location it stored to
            		 storeText.setText("   Stored to " + (selection+1));
 	 				 mBrainLink.setFullColorLED(0, 255, 0);
	 				 mBrainLink.sleep(500);
					 mBrainLink.setFullColorLED(0, 0, 0);
            	 }
            	 else {
            		 storeText.setText("   Invalid signal, try recapturing ");
 	 				 mBrainLink.setFullColorLED(255, 0, 0);
	 				 mBrainLink.sleep(500);
				 	 mBrainLink.setFullColorLED(0, 0, 0);
            	 }
             }
         });
		 // Button to navigate to the Send screen
         final Button toSend = (Button) findViewById(R.id.moveToSend);
         toSend.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
            	 Intent capture = new Intent(ReceiveSignalScreen.this,SendSignalScreen.class); 
            	 startActivity(capture); 
             }
         });

	}

}