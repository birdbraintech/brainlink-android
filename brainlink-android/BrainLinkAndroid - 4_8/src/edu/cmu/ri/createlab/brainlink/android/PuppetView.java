package edu.cmu.ri.createlab.brainlink.android;

import edu.cmu.ri.createlab.brainlink.robots.BrainLinkRobot;
import edu.cmu.ri.createlab.brainlink.robots.robosapien.RobotRobosapien;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

public class PuppetView extends View implements Runnable {

	Bitmap mBackground;
	
	private BrainLinkRobot mRobot;
	
	private SensorManager sensorManager = null;

	private String mRobotName;
	
	private boolean bStartButtonPressed =false;
	
	byte[] b = new byte[]{};
	
	public PuppetView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		Resources r = context.getResources();
		
		mBackground = BitmapFactory.decodeResource(getResources(), R.drawable.control_background);
		
		initialRobot();

	}

	@Override
	public void run() {
		while (true) {
			this.postInvalidate();

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	public void onDraw(Canvas canvas) {
		canvas.drawBitmap(mBackground, 0, 0,null);
	}
	private void initialRobot() {
		mRobot= (BrainLinkRobot)new RobotRobosapien();
	}

}
