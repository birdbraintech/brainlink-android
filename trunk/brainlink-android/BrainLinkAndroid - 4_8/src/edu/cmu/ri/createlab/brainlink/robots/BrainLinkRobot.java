package edu.cmu.ri.createlab.brainlink.robots;

public abstract class BrainLinkRobot {

    public static final int CONDITION_STOP = 0;
    public static final int CONDITION_FORWARD = 1;
    public static final int CONDITION_BACKWARD = 2;
    public static final int CONDITION_LEFT = 3;
    public static final int CONDITION_RIGHT = 4;
    protected int mRobotCondition;
    
	abstract public void moveStop();
	abstract public void moveForward();
	abstract public void moveBackward();
	abstract public void moveLeft();
	abstract public void moveRight();
}
