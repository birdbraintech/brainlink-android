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
import android.widget.SeekBar;
import android.widget.TextView;

// Activity allows the user to send signals stored in Brainlink's EEPROM. 
// Since EEPROM memory persists, it is not necessary to capture signals every time
// we run the app - only if we want different signals.
public class SendSignalScreen extends Activity {

	private BrainLink mBrainLink;
	private int scrollBarValue = 0;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_signal);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);     //  Fixed Portrait orientation
		mBrainLink = MainActivity.mBrainLink;
		
		// The next five buttons play the IR signals in positions 0-4 respectively. 
		// Each button maps to a different LED color as well. 
		// Finally, a signal can be set to auto-repeat with the scroll bar.
		 final Button button1 = (Button) findViewById(R.id.button1);
         button1.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
            	mBrainLink.playIR(0, scrollBarValue);
 				mBrainLink.setFullColorLED(255, 0, 0);
 				mBrainLink.sleep(500);
				mBrainLink.setFullColorLED(0, 0, 0);
             }
         });				

		 final Button button2 = (Button) findViewById(R.id.button2);
         button2.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
            	mBrainLink.playIR(1, scrollBarValue);
 				mBrainLink.setFullColorLED(0, 255, 255);
 				mBrainLink.sleep(500);
				mBrainLink.setFullColorLED(0, 0, 0);
             }
         });
         
         final Button button3 = (Button) findViewById(R.id.button3);
         button3.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
            	mBrainLink.playIR(2, scrollBarValue);
 				mBrainLink.setFullColorLED(0, 0, 255);
 				mBrainLink.sleep(500);
				mBrainLink.setFullColorLED(0, 0, 0);
             }
         });	
         
         final Button button4 = (Button) findViewById(R.id.button4);
         button4.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
            	mBrainLink.playIR(3, scrollBarValue);
 				mBrainLink.setFullColorLED(255, 0, 255);
 				mBrainLink.sleep(500);
				mBrainLink.setFullColorLED(0, 0, 0);
             }
         });
         
         final Button button5 = (Button) findViewById(R.id.button5);
         button5.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
            	mBrainLink.playIR(4, scrollBarValue);
 				mBrainLink.setFullColorLED(0, 255, 0);
 				mBrainLink.sleep(500);
				mBrainLink.setFullColorLED(0, 0, 0);
             }
         });		         
		 // Seekbar implementation for repeat time
         SeekBar repeatTimeSeeker = (SeekBar)findViewById(R.id.repeatTimeBar);
         final TextView repeatValue = (TextView)findViewById(R.id.repeatTimeText);

         // Sets the time that signals should auto repeat.
         repeatTimeSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

		     @Override
		     public void onProgressChanged(SeekBar repeatTimeSeeker, int progress,
		       boolean fromUser) {
		      // TODO Auto-generated method stub
		    	 if(progress > 19) {
		    		 repeatValue.setText(String.valueOf(progress) + " ms");
		    		 scrollBarValue = progress;
		    	 }
		    	 else {
		    		 repeatValue.setText("No Repeat");
		    		 scrollBarValue = 0;
		    	 }
		    	
		     }

		     @Override
		     public void onStartTrackingTouch(SeekBar repeatTimeSeeker) {
		      // TODO Auto-generated method stub
		     }

		     @Override
		     public void onStopTrackingTouch(SeekBar repeatTimeSeeker) {
		      // TODO Auto-generated method stub
			     }
	         });
	         

	         final Button toCapture = (Button) findViewById(R.id.moveToCapture);
	         toCapture.setOnClickListener(new View.OnClickListener() {
	             public void onClick(View v) {
	            	 Intent capture = new Intent(SendSignalScreen.this,ReceiveSignalScreen.class); 
	            	 startActivity(capture); 
	             }
	         });		    
			
		}

}
