package edu.cmu.ri.createlab.brainlink.android;

import java.util.ArrayList;
import java.util.Timer;

import edu.cmu.ri.createlab.brainlink.robots.BrainLinkRobot;
import edu.cmu.ri.createlab.brainlink.robots.robosapien.BossaNova;
import edu.cmu.ri.createlab.brainlink.robots.robosapien.RobotRobosapien;
import edu.cmu.ri.createlab.brainlink.robots.robosapien.WallE;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MimicView extends View{

	static final int TOUCH_UNKNOWN = 0;
	static final int TOUCH_DOWN = 1;
	static final int TOUCH_UP = 2;
	int mTouchStatus = 0;
	
	private BrainLinkRobot mRobot;

	Bitmap mBackground;
	GraphicObject mTouchpad;
	Point center, lefttop, centertop, righttop, leftcenter, rightcenter,
			bottomleft, bottomcenter, bottomright;
	Point[] mPointArray;
	Paint dotPaint;
	int dotX, dotY;

	String tip = " ";
	Paint textPaint;

	ArrayList<Point> mTrace;

	int px, py, cx, cy;

	public MimicView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Resources r = context.getResources();

		mBackground = BitmapFactory.decodeResource(getResources(),
				R.drawable.control_background);
		mTouchpad = new GraphicObject(BitmapFactory.decodeResource(
				getResources(), R.drawable.touchpad_back), 320 / 2, 480 / 2);

		center = new Point(mTouchpad.getCenterX(), mTouchpad.getCenterY());
		lefttop = new Point(mTouchpad.getLeftBoundary(),
				mTouchpad.getTopBoundary());
		centertop = new Point(center.x, lefttop.y);
		righttop = new Point(mTouchpad.getRightBoundary(), centertop.y);
		leftcenter = new Point(lefttop.x, center.y);
		rightcenter = new Point(righttop.x, center.y);
		bottomleft = new Point(lefttop.x, mTouchpad.getBottomBoundary());
		bottomcenter = new Point(center.x, bottomleft.y);
		bottomright = new Point(righttop.x, bottomleft.y);

		mPointArray = new Point[] { center, lefttop, leftcenter, bottomleft,
				bottomcenter, bottomright, rightcenter, righttop, centertop, };

		dotPaint = new Paint();
		dotPaint.setColor(Color.YELLOW);

		textPaint = new Paint();
		textPaint.setTextSize(14);
		textPaint.setColor(Color.RED);

		mTrace = new ArrayList<Point>();


		controlThread.start();
		drawingThread.start();

	}

	public void initialRobot(String s) {
		if(s.equals("walle")) {
			mRobot = (BrainLinkRobot)new WallE();
		}
		else if(s.equals("robosapien")) {
			mRobot= (BrainLinkRobot)new RobotRobosapien();
		}
		else if(s.equals("bossanova")) {
			mRobot= (BrainLinkRobot)new BossaNova();
		}

	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawBitmap(mBackground, 0, 0, null);
		mTouchpad.drawObject(canvas);

		canvas.drawCircle(dotX, dotY, 30, dotPaint);

		canvas.drawText(tip, 100, 100, textPaint);
	}

	boolean t = false;
	private static final int INVALID_POINTER_ID = -1;

	// The ‘active pointer’ is the one currently moving our object.
	private int mActivePointerId = INVALID_POINTER_ID;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			if (!mTrace.isEmpty()) {
				mTrace.clear();
			}
			px = (int) event.getX();
			py = (int) event.getY();
			dotPaint.setAlpha(255);
			mActivePointerId = event.getPointerId(0);
			mTouchStatus = TOUCH_DOWN;
			controlThread.suspend();
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			final int pointerIndex = event.findPointerIndex(mActivePointerId);

			cx = (int) event.getX();
			cy = (int) event.getY();
			if (mTouchpad.isPointIn(cx, cy)) {
				mTrace.add(new Point(cx, cy));
				dotX = cx;
				dotY = cy;
			}

			px = cx;
			py = cy;

			break;
		}
		case MotionEvent.ACTION_UP: {
			mActivePointerId = INVALID_POINTER_ID;
			analyzeTrace();
			dotPaint.setAlpha(0);
			mTouchStatus = TOUCH_UP;
			controlThread.resume();
			break;
		}
		}
		return true;
	}

	private int analyzeTrace() {
		if (!mTrace.isEmpty()) {
			boolean[] mIntersaction = new boolean[8];
			Point node = new Point();
			for (int i = 0; i < 8; i++) {
				float d = checkD(mTrace.get(0),
						mTrace.get(mTrace.size() - 1), mPointArray[0],
						mPointArray[i + 1]);
				if (d != 0) {
					node = getNode(mTrace.get(0),
							mTrace.get(mTrace.size() - 1),
							mPointArray[0], mPointArray[i + 1], d);
					mIntersaction[i] = checkIntersection(node,
							mTrace.get(0),
							mTrace.get(mTrace.size() - 1),
							mPointArray[0], mPointArray[i + 1]);
				}
			}

			tip = " ";
			// for(int i=0;i<8;i++) {
			// tip += Boolean.toString(mIntersaction[i]) + " ";
			// }
			if ((mIntersaction[0] && mIntersaction[1] && mIntersaction[2] && mTrace
					.get(0).y < mTouchpad.getCenterY())) {
				tip = "FORWARD";
				mRobot.moveForward();
			} else if ((mIntersaction[0] && mIntersaction[1]
					&& mIntersaction[2] && mTrace.get(0).y > mTouchpad
					.getCenterY())) {
				tip = "BACKWARD";
				mRobot.moveBackward();
			} else if ((mIntersaction[4] && mIntersaction[5]
					&& mIntersaction[6] && mTrace.get(0).y < mTouchpad
					.getCenterY())) {
				tip = "FORWARD";
				mRobot.moveForward();
			} else if ((mIntersaction[4] && mIntersaction[5]
					&& mIntersaction[6] && mTrace.get(0).y > mTouchpad
					.getCenterY())) {
				tip = "BACKWARD";
				mRobot.moveBackward();
			} else if ((mIntersaction[7] && mIntersaction[0]
					&& mIntersaction[1] && mTrace.get(0).y < mTouchpad
					.getCenterY())
					|| (mIntersaction[5] && mIntersaction[4]
							&& mIntersaction[3] && mTrace.get(0).y < mTouchpad
							.getCenterY())) {
				tip = "RIGHT";
				mRobot.moveRight();
			} else if ((mIntersaction[7] && mIntersaction[6]
					&& mIntersaction[5] && mTrace.get(0).y < mTouchpad
					.getCenterY())
					|| (mIntersaction[1] && mIntersaction[2]
							&& mIntersaction[3] && mTrace.get(0).y < mTouchpad
							.getCenterY())) {
				tip = "LEFT";
				mRobot.moveLeft();
			}

		} else
			return -1;
		return 0;

	}

	boolean checkIntersection(Point point0, Point point1, Point point2,
			Point point3, Point point4) {
		if ((point0.x - point1.x) * (point0.x - point2.x) > 0)
			return false;
		if ((point0.x - point3.x) * (point0.x - point4.x) > 0)
			return false;
		if ((point0.y - point1.y) * (point0.y - point2.y) > 0)
			return false;
		if ((point0.y - point3.y) * (point0.y - point4.y) > 0)
			return false;
		return true;

	}

	Point getNode(Point point1, Point point2, Point point3, Point point4,
			float d) {
		float x, y;
		x = ((point2.x - point1.x) * (point4.x - point3.x)
				* (point3.y - point1.y) + (point2.y - point1.y)
				* (point4.x - point3.x) * point1.x - (point4.y - point3.y)
				* (point2.x - point1.x) * point3.x)
				/ d;
		y = ((point2.y - point1.y) * (point4.y - point3.y)
				* (point3.x - point1.x) + (point2.x - point1.x)
				* (point4.y - point3.y) * point1.y - (point4.x - point3.x)
				* (point2.y - point1.y) * point3.y)
				/ (-d);
		return new Point((int) x, (int) y);
	}

	float checkD(Point point1, Point point2, Point point3, Point point4) {
		return (point2.y - point1.y) * (point4.x - point3.x)
				- (point4.y - point3.y) * (point2.x - point1.x);

	}



	class GraphicObject {
		private Bitmap mBitmap;
		private int mX, mY, mCenterX, mCenterY;
		private Matrix mMatrix;

		public GraphicObject(Bitmap bitmap, int centerX, int centerY) {
			mBitmap = bitmap;
			mX = centerX - bitmap.getWidth() / 2;
			mY = centerY - bitmap.getHeight() / 2;
			mCenterX = centerX;
			mCenterY = centerY;
			mMatrix = new Matrix();
			mMatrix.setTranslate(mX, mY);
		}

		public void setRotate(float degree, float x, float y) {
			mMatrix.postRotate(degree, x, y);
		}

		public Bitmap getGraphic() {
			return mBitmap;
		}

		public void drawObject(Canvas canvas) {
			canvas.drawBitmap(mBitmap, mMatrix, null);
		}

		public int getCenterX() {
			return mCenterX;
		}

		public int getCenterY() {
			return mCenterY;
		}

		public int getLeftBoundary() {
			return mX;
		}

		public int getRightBoundary() {
			return mX + 2 * (mCenterX - mX);
		}

		public int getTopBoundary() {
			return mY;
		}

		public int getBottomBoundary() {
			return mY + 2 * (mCenterY - mY);
		}

		public boolean isPointIn(int x, int y) {
			if (x > mX && x < mX + 2 * (mCenterX - mX) && y > mY
					&& y < mY + 2 * (mCenterY - mY))
				return true;
			else
				return false;
		}
	}

	private Thread drawingThread = new Thread() {
		@Override
		public void run() {
			while (true) {
				MimicView.this.postInvalidate();

				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	};
	
	private Thread controlThread = new Thread() {
		@Override
		public void run() {
			while (true) {
				if (mTouchStatus == TOUCH_UP) {
					try {
						Thread.sleep(1500);
						if (mTouchStatus == TOUCH_UP) {
							mTouchStatus = TOUCH_UNKNOWN;
							mRobot.moveStop();
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	};
}
