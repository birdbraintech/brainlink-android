package edu.cmu.ri.createlab.brainlink.android;

import edu.cmu.ri.createlab.brainlink.robots.BrainLinkRobot;

import edu.cmu.ri.createlab.brainlink.robots.robosapien.RobotRobosapien;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import android.graphics.Paint;
import android.graphics.Paint.Style;

import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;


public class JoystickView extends View implements Runnable {

	Bitmap mStickBackground;
	Bitmap mDot;
	Bitmap mDotPressed;

	Paint mTextPaint = new Paint();
	String mText = " ";

	int mDotx, mDoty, mDotDiameter;
	int mBgCoordx, mBgCoordy;
	int vWidth, vHeight;
	boolean mTouchCondition = false;

	boolean mBackgroundPosition = false;
	
	private BrainLinkRobot mRobot;
	
	Bitmap mBackground;

	
	public JoystickView(Context context, AttributeSet attrs) {
		super(context, attrs);

		Resources r = context.getResources();

		mBackground = BitmapFactory.decodeResource(getResources(),
				R.drawable.control_bg);
		mStickBackground = BitmapFactory.decodeStream(r
				.openRawResource(R.drawable.joystick_back));

		mDot = BitmapFactory.decodeStream(r
				.openRawResource(R.drawable.joystic_dot));
		
		mDotPressed = BitmapFactory.decodeStream(r.openRawResource(R.drawable.joystic_dot_2));

		mDotDiameter = mDot.getWidth();
		
		vWidth = 0;
		vHeight = 0;

		
		
		mDotx = vWidth/2;
		mDoty = vHeight/2;

		mTextPaint.setARGB(255, 255, 255, 255);
		mTextPaint.setStyle(Style.FILL);
		mTextPaint.setTextSize(30);

		Thread thread = new Thread(this);
		thread.start();
		
	}

	public void initialRobot(String s) {
//		if(s.equals("walle")) {
//			mRobot = (BrainLinkRobot)new WallE();
//		}
		if(s.equals("Robosapien")) {
			mRobot= (BrainLinkRobot)new RobotRobosapien();
		}
//		else if(s.equals("bossanova")) {
//			mRobot= (BrainLinkRobot)new BossaNova();
//		}
//		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(!mBackgroundPosition)
		{
			mBgCoordx = getWidth();
			mBgCoordy = getHeight();
			mBgCoordx = (vWidth - mStickBackground.getWidth()) / 2;
			mBgCoordy = (vHeight - mStickBackground.getHeight()) / 2;	
			mBackgroundPosition = true;
			mDotx = mBgCoordx;
			mDoty = mBgCoordy;
		}

		
		canvas.drawBitmap(mBackground, 0, 0, null);
		canvas.drawBitmap(mStickBackground, mBgCoordx,mBgCoordy, null);
		canvas.drawBitmap(mDot, mDotx, mDoty, null);
		canvas.drawText(mText, 10, 30, mTextPaint);
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
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mTouchCondition) {
				if (ty > 53 && ty <320 && mDotx > 114 && mDotx<134) {
					mDoty = ty - mDotDiameter / 2;
					mDotx = 124;
					if (mDoty < 53) {
						mDoty = 53;
						mRobot.moveForward();
					}
					if (mDoty > 270) {
						mDoty = 270;
						mRobot.moveBackward();
					}

				}
				if (tx > 16 && tx < 300 && mDoty > 150 && mDoty<170) {
					mDotx = tx - mDotDiameter / 2;
					mDoty = 160;
					if (mDotx < 16) {
						mDotx = 16;
						mRobot.moveLeft();
					}
					if (mDotx > 230) {
						mDotx = 230;
						mRobot.moveRight();
					}
				}


			}
			break;
		case MotionEvent.ACTION_UP:
			mTouchCondition = false;
			mDotx = 124;
			mDoty = 160;
			mText = " ";
			mRobot.moveStop();
			break;
		}
//		mText = "X:" + Integer.toString(mDotx) + " Y:"
//				+ Integer.toString(mDoty);

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