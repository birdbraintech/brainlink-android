package edu.cmu.ri.createlab.brainlink.robots.robosapien;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.cmu.ri.createlab.brainlink.android.MainActivity;
import edu.cmu.ri.createlab.brainlink.robots.BrainLinkRobot;
import edu.cmu.ri.createlab.util.ByteUtils;

public class WallE extends BrainLinkRobot{
	
	private OutputStream outStream = null;
	private InputStream inStream = null;

	byte[] b = new byte[]{};

    static final byte[] FORWARD = new byte[]{0x69, ByteUtils.intToUnsignedByte(0xAF), ByteUtils.intToUnsignedByte(0xEF), ByteUtils.intToUnsignedByte(0xE8), 0x00, 0x00};

    static final byte[] WALL_E = new byte[] {0x2A, 0x49, 0x03, 0x49, 0x02, 0x0B, 0x6D, 0x03, ByteUtils.intToUnsignedByte(0xB6), 0x03, 0x15, 0x02, ByteUtils.intToUnsignedByte(0xD5), 0x00, ByteUtils.intToUnsignedByte(0xE1), 0x00, 0x00};
    static final byte[] MOVE_FORWARD = new byte[] {ByteUtils.intToUnsignedByte(0xAF), ByteUtils.intToUnsignedByte(0xEF), ByteUtils.intToUnsignedByte(0xE1),  0x00, 0x00};
    static final byte[] MOVE_BACKWARD = new byte[] {};
    static final byte[] MOVE_LEFT = new byte[] {ByteUtils.intToUnsignedByte(0xAF), ByteUtils.intToUnsignedByte(0xDF), ByteUtils.intToUnsignedByte(0xD8),  0x00, 0x00};
    static final byte[] MOVE_RIGHT = new byte[] {};

    static final byte[] MOVE_STOP = new byte[] {};
    
    public WallE() {
		try {
			outStream = MainActivity.mBluetooth.getOutputStream();
			inStream = MainActivity.mBluetooth.getInputStream();
			
			outStream.write(WALL_E);
			
			mRobotCondition = CONDITION_STOP;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    }
	@Override
	public void moveStop() {
    	if(mRobotCondition != CONDITION_STOP) {
			try {
				outStream.write(MOVE_STOP);
				inStream.read(b);
				
				mRobotCondition = CONDITION_STOP;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
    	}
		
	}

	@Override
	public void moveForward() {
    	if(mRobotCondition != CONDITION_FORWARD) {
			try {
				outStream.write(MOVE_FORWARD);
				inStream.read(b);
				
				mRobotCondition = CONDITION_FORWARD;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
	}

	@Override
	public void moveBackward() {
    	if(mRobotCondition != CONDITION_BACKWARD) {    	
			try {
				outStream.write(MOVE_BACKWARD);
				inStream.read(b);
				
				mRobotCondition = CONDITION_BACKWARD;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
		
	}

	@Override
	public void moveLeft() {
    	if(mRobotCondition != CONDITION_LEFT) {
			try {
				outStream.write(MOVE_LEFT);
				inStream.read(b);
				
				mRobotCondition = CONDITION_LEFT;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
	}

	@Override
	public void moveRight() {
    	if(mRobotCondition != CONDITION_RIGHT) {
			try {
				outStream.write(MOVE_RIGHT);
				inStream.read(b);
				
				mRobotCondition = CONDITION_RIGHT;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
    	}
	}
	

}
