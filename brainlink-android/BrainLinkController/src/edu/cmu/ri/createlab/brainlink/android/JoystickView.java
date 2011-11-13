package edu.cmu.ri.createlab.brainlink.android;

import edu.cmu.ri.createlab.brainlink.BrainLink;
import edu.cmu.ri.createlab.brainlink.robots.BrainLinkRobot;

import edu.cmu.ri.createlab.brainlink.robots.robosapien.RobotRobosapien;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;

import android.graphics.Paint;
import android.graphics.Paint.Style;

import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;


public class JoystickView extends View implements Runnable {

	Bitmap mStickBackground;
	Bitmap mDot;

	Paint mTextPaint = new Paint();

	int mDotx, mDoty, mDotDiameter;
	int mBgCoordx, mBgCoordy;
	int vWidth, vHeight;
	boolean mTouchCondition = false;

	boolean mBackgroundPosition = false;
	
	
	
	Bitmap mBackground;
	
	Rect mLeft, mRight, mTop, mBottom, mCenter;

	Context joystickActivity;
	Resources r;
	private String mRobotName;
	
	public JoystickView(Context context, AttributeSet attrs) {
		super(context, attrs);

		joystickActivity = (JoystickActivity)context;
		r = context.getResources();

		mBackground = BitmapFactory.decodeResource(getResources(),
				R.drawable.control_bg);
		mStickBackground = BitmapFactory.decodeStream(r
				.openRawResource(R.drawable.joystick_back));

		mDot = BitmapFactory.decodeStream(r.openRawResource(R.drawable.joystic_dot));

		mDotDiameter = mDot.getWidth();
		
		
		
		mDotx = vWidth/2;
		mDoty = vHeight/2;

		mTextPaint.setARGB(255, 255, 255, 255);
		mTextPaint.setStyle(Style.FILL);
		mTextPaint.setTextSize(30);

		Thread thread = new Thread(this);
		thread.start();
		
	}


	@Override
	protected void onDraw(Canvas canvas) {
		if(!mBackgroundPosition)
		{
			vWidth = getWidth();
			vHeight = getHeight();
			mBgCoordx = (vWidth - mStickBackground.getWidth()) / 2;
			mBgCoordy = (int) ((vHeight - mStickBackground.getHeight()) / 2 + vHeight*.1);	
			mBackgroundPosition = true;
			mDotx = (vWidth - mDot.getWidth()) / 2;
			mDoty = (int) ((vHeight - mDot.getHeight()) / 2 + vHeight*.1);	
			
			int stickBackWidth = mStickBackground.getWidth();
			int stickBackHeight = mStickBackground.getHeight();
			mLeft = new Rect(mBgCoordx,mBgCoordy+(int)(0.362*stickBackHeight),mBgCoordx+(int)(0.357*stickBackWidth),mBgCoordy+(int)(0.652*stickBackHeight));
			mRight = new Rect(mBgCoordx+(int)(0.643*stickBackWidth),mBgCoordy+(int)(0.362*stickBackHeight),mBgCoordx+stickBackWidth,mBgCoordy+(int)(0.652*stickBackHeight));
			mTop = new Rect(mBgCoordx+(int)(0.357*stickBackWidth), mBgCoordy, mBgCoordx+(int)(0.643*stickBackWidth),mBgCoordy+(int)(0.362*stickBackHeight));
			mBottom = new Rect(mBgCoordx+(int)(0.357*stickBackWidth),mBgCoordy+(int)(0.652*stickBackHeight), mBgCoordx+(int)(0.643*stickBackWidth),mBgCoordy+ stickBackHeight);
			mCenter = new Rect(mBgCoordx+(int)(0.357*stickBackWidth), mBgCoordy+(int)(0.362*stickBackHeight),mBgCoordx+(int)(0.643*stickBackWidth),mBgCoordy+(int)(0.652*stickBackHeight));

		}

		
		canvas.drawBitmap(mBackground, 0, 0, null);
		canvas.drawBitmap(mStickBackground, mBgCoordx,mBgCoordy, null);
		canvas.drawBitmap(mDot, mDotx, mDoty, null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int tx = (int) event.getX();
		int ty = (int) event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (tx >= mDotx && tx <= mDotx + mDotDiameter && ty >= mDoty
					&& ty <= mDoty + mDotDiameter) {
				mTouchCondition = true;
				mDot = BitmapFactory.decodeStream(r.openRawResource(R.drawable.joystic_dot_2));
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mTouchCondition) {

				if(mCenter.contains(tx, ty)) {
					mDotx = tx - mDotDiameter / 2;
					mDoty = ty - mDotDiameter / 2;					
				}
				if(mTop.contains(tx, ty)) {
					if(ty-mDotDiameter/2<mTop.top) {
						mDotx = tx - mDotDiameter / 2;
					}
					else {
						mDotx = tx - mDotDiameter / 2;
						mDoty = ty - mDotDiameter / 2;
					}
					((JoystickActivity) joystickActivity).forward();
				}
				if(mBottom.contains(tx,ty)) {
					if(ty+mDotDiameter/2>mBottom.bottom) {
						mDotx = tx - mDotDiameter /2;
					}
					else {
						mDotx = tx - mDotDiameter / 2;
						mDoty = ty - mDotDiameter / 2;
					}
					((JoystickActivity) joystickActivity).backward();
				}
				if(mLeft.contains(tx, ty)) {
					if(tx-mDotDiameter/2<mLeft.left) {
						mDoty = ty - mDotDiameter / 2;
					}
					else {
						mDoty = ty - mDotDiameter / 2;
						mDotx = tx - mDotDiameter / 2;
					}
					((JoystickActivity) joystickActivity).left();
				}
				if(mRight.contains(tx, ty)) {
					if(tx+mDotDiameter/2>mRight.right) {
						mDoty = ty - mDotDiameter /2;
					}
					else {
						mDotx = tx - mDotDiameter / 2;
						mDoty = ty - mDotDiameter / 2;
					}
					((JoystickActivity) joystickActivity).right();
				}
				
				

			}
			break;
		case MotionEvent.ACTION_UP:
			mTouchCondition = false;
			mDot = BitmapFactory.decodeStream(r.openRawResource(R.drawable.joystic_dot));
			mDotx = (vWidth - mDot.getWidth()) / 2;
			mDoty = (int) ((vHeight - mDot.getHeight()) / 2 + vHeight*.1);	
			((JoystickActivity) joystickActivity).stop();
			break;
		}

		return true;
	}


	private int touchZone(int tx, int ty) {
		if (tx > 50 && tx < 120 && ty > 70 && ty < 160)
			return 0;

		return 0;
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

}