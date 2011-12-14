package brainlink.remoteController.data;

public class ListRobotItem {
	private String mRobotItemName;
	private String mRobotItemDescription;
	private String mRobotItemIcon;
	
	public void putString(String key, String value) {
		if(key.equals(ListRobot.KEY_ROBOTLIST_NAME))
			mRobotItemName = value;
		else if(key.equals(ListRobot.KEY_ROBOTLIST_DESCRIPTION))
			mRobotItemDescription = value;
		else if(key.equals(ListRobot.KEY_ROBOTLIST_ICON))
			mRobotItemIcon = value;
	}

	public String getRobotItemName() {
		return mRobotItemName;
	}
	
	public String getRobotItemDescription() {
		return mRobotItemDescription;
	}
	
	public String getRobotItemIcon() {
		return mRobotItemIcon;
	}
}
