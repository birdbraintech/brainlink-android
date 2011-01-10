package edu.cmu.ri.createlab.brainlink.android;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import edu.cmu.ri.createlab.util.ByteUtils;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class NativeActivity extends Activity implements SensorEventListener {

	private TextView orientXValue;
	private TextView orientYValue;
	private TextView orientZValue;

	private Bundle bundle;
	private SensorManager sensorManager = null;

	private BluetoothDevice brainLinkBluetooth;
	private BluetoothSocket btSocket = null;
	
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private String mRobotName;
	private OutputStream outStream = null;
	private InputStream inStream = null;
	byte[] b = new byte[]{};

	static final byte[] INITIALIZATION_COMMAND = new byte[]{0x49, 0x03, 0x49, 0x02, 0x0B, 0x6D, 0x03, ByteUtils.intToUnsignedByte(0xB6), 0x03, 0x15, 0x02, ByteUtils.intToUnsignedByte(0xD5), 0x00, ByteUtils.intToUnsignedByte(0xB6), 0x03, 0x15, 0x02, ByteUtils.intToUnsignedByte(0xE1), 0x00, 0x00};
    static final byte[] FORWARD = new byte[]{0x69, ByteUtils.intToUnsignedByte(0xAF), ByteUtils.intToUnsignedByte(0xEF), ByteUtils.intToUnsignedByte(0xE8), 0x00, 0x00};

    static final byte[] RC = new byte[] {0x49, 0x03, 0x49, 0x01, ByteUtils.intToUnsignedByte(0x0D), 0x05, 0x02, 0x08, 0x06, ByteUtils.intToUnsignedByte(0x83), 0x01, ByteUtils.intToUnsignedByte(0xA0), 0x01, ByteUtils.intToUnsignedByte(0xA0)};
    static final byte[] RF = new byte[] {0x69, ByteUtils.intToUnsignedByte(0x86),  0x00, 0x00};
    static final byte[] BF = new byte[] {0x69, ByteUtils.intToUnsignedByte(0x87),  0x00, 0x00};
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//get the data from MainActivity
		bundle = getIntent().getExtras();
		brainLinkBluetooth = (BluetoothDevice) (bundle
				.getParcelable(MainActivity.BUNDLE_BLUETOOTH));
		mRobotName = (String) (bundle.getString(MainActivity.BUNDLE_ROBOT));

		try {
			btSocket = brainLinkBluetooth.createRfcommSocketToServiceRecord(MY_UUID);
			btSocket.connect();

			outStream = btSocket.getOutputStream();
			inStream = btSocket.getInputStream();
			
			outStream.write(RC);

			inStream.read(b);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// Get a reference to a SensorManager
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		setContentView(R.layout.act_native);

		// Capture orientation related view elements
		orientXValue = (TextView) findViewById(R.id.orient_x_value);
		orientYValue = (TextView) findViewById(R.id.orient_y_value);
		orientZValue = (TextView) findViewById(R.id.orient_z_value);

		// Initialize orientation related view elements
		orientXValue.setText("0.00");
		orientYValue.setText("0.00");
		orientZValue.setText("0.00");
	}

	// This method will update the UI on new sensor events
	public void onSensorChanged(SensorEvent sensorEvent) {
		synchronized (this) {
			if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
				orientXValue.setText(Float.toString(sensorEvent.values[0]));
				orientYValue.setText(Float.toString(sensorEvent.values[1]));
				orientZValue.setText(Float.toString(sensorEvent.values[2]));
			}
			
			if(sensorEvent.values[1]>30)
				MoveForward();
			
			if(sensorEvent.values[1]<-30)
				MoveBackward();
		}
	}

	private void MoveBackward() {
		try {
			outStream.write(BF);
			inStream.read(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	private void MoveForward() {
		try {
			outStream.write(RF);
			inStream.read(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	// I've chosen to not implement this method
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		super.onResume();
		
		
		// ...and the orientation sensor
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onStop() {
		// Unregister the listener
		sensorManager.unregisterListener(this);
		super.onStop();
	}

}
