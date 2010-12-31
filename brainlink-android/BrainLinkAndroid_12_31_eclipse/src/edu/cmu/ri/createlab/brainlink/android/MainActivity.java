package edu.cmu.ri.createlab.brainlink.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

//import edu.cmu.ri.createlab.brainlink.android.data.ListRobot;
//import edu.cmu.ri.createlab.brainlink.android.data.ListRobotItem;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.content.BroadcastReceiver;

public class MainActivity extends ListActivity {
	
	private List<Map<String, Object>> mRobotList = new ArrayList<Map<String, Object>>();
	
    //Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    static BluetoothDevice brainLinkBluetooth;   
    
    private volatile boolean mBluetoothConnected = false;
    private volatile boolean mBrainLinkDeviceFound = false;
    private volatile boolean mBrainLinkDeviceConnected = false;
    
    ProgressDialog m_bluetoothDialog;
  
    protected static final int DLG_BT_CONNECTING = 0;
    protected static final int DLG_DEVICE_FINDING = 1;
    protected static final int DLG_DEVICE_PAIRED_FOUND = 2;
    protected static final int DLG_DEVICE_NOT_FOUND = 3;
    protected static final int DLG_DEVICE_CONNECTING = 4;
    
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    
    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 2;
    
    Handler bluetoothMessageHandler = new Handler () {
	    // @Override 
		  public void handleMessage(Message msg)
		  {
			  switch (msg.what)
			  {
			  case MainActivity.DLG_BT_CONNECTING:
				  m_bluetoothDialog.setMessage("Connecting Bluetooth");
				  break;
			  case MainActivity.DLG_DEVICE_FINDING:
				  m_bluetoothDialog.setMessage("Finding BrainLink Device");
				  break;
			  case MainActivity.DLG_DEVICE_PAIRED_FOUND:
				  m_bluetoothDialog.setMessage("Paired BrainLink Device Found");
				  break;
			  case MainActivity.DLG_DEVICE_CONNECTING:
				  m_bluetoothDialog.setMessage("Connecting BrainLink Device");
				  break;
			  }
		  }
    };
    
	//private List<ListRobotItem> mRobotList;
	//private RobotListAdapter mRobotAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
	//	mRobotList = new ArrayList<ListRobotItem>();
	//	mRobotList = ListRobot.loadList(MainActivity.this);
	//	mRobotAdapter = new RobotListAdapter();
	//	ListView robotLv = (ListView)findViewById(R.id.robot_list);
	//	robotLv.setAdapter(mRobotAdapter);
	//	mRobotAdapter.notifyDataSetChanged();
        //Initial the robot listview
        
        initialList();
		
        m_bluetoothDialog = new ProgressDialog(MainActivity.this);
        m_bluetoothDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_bluetoothDialog.setTitle("Connecting");
        m_bluetoothDialog.setMessage("Initializing");
        m_bluetoothDialog.setIndeterminate(false);
        m_bluetoothDialog.setCancelable(false);

		
    }
    
    private void initialList() {
		SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.list_robot,
				new String[]{"title","info","img"},
				new int[]{R.id.title,R.id.info,R.id.img});   	
		setListAdapter(adapter);
		
		
    }
    
	private List<Map<String, Object>> getData() {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", "Wall-E");
		map.put("info", "WALL-E U Command");
		map.put("img", R.drawable.i1);
		mRobotList.add(map);

		map = new HashMap<String, Object>();
		map.put("title", "Robosapien");
		map.put("info", "WowWee");
		map.put("img", R.drawable.i2);
		mRobotList.add(map);

		map = new HashMap<String, Object>();
		map.put("title", "Bossa Nova Prime-8");
		map.put("info", "Bossa Nova Prime-8");
		map.put("img", R.drawable.i3);
		mRobotList.add(map);
		
		return mRobotList;
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.layout.menu_main, menu);
        return true;
    }
    

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	
    	m_bluetoothDialog.show();
    	
    	if(brainLinkFindThread.isAlive())
    		brainLinkFindThread.destroy();
    	if(brainLinkFindThread.isAlive())
    		brainLinkFindThread.destroy();
    	if(brainLinkConnectThread.isAlive())
    		brainLinkConnectThread.destroy();
    	
    	// Connect Bluetooth and Search for Brainlink Device
    	if(!mBluetoothConnected) {	//if not setup for Bluetooth connection
    		ConnectionSetup(); //Start bluetooth
    	} else if(!mBrainLinkDeviceFound || !mBrainLinkDeviceConnected){
         	brainLinkFindThread.start(); //find Brainlink Device
    	}
    	
    }
 

    
	private void ConnectionSetup() {
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean bHasBluetooth = true;
        
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Your Device does not support Bluetooth", Toast.LENGTH_LONG).show();
            bHasBluetooth = false;
        }
		
        if (bHasBluetooth) {
        	if(!mBluetoothAdapter.isEnabled()) {
//                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        			bluetoothConnectThread.start();
                }
            else {
            	mBluetoothConnected = true;
            	brainLinkFindThread.start();
            }
        }

	}

	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.addnewrobots:
    		return true;
    	}
		return false;
    }
   
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case REQUEST_ENABLE_BT:
            if (resultCode == 0) {
            	mBluetoothConnected = true;
            	brainLinkFindThread.start();
            	
            } else {
                // User did not enable Bluetooth or an error occured
                Toast.makeText(this, "Please connect your Bluetooth first", Toast.LENGTH_LONG).show();
            }
		}
	}
	
	@Override
	public void onDestroy() {
		if(mReceiver!=null)
			unregisterReceiver(mReceiver);
	}
	
	 private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		 @Override
	     public void onReceive(Context context, Intent intent) {
	         String action = intent.getAction();

	         if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	             BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	             if(brainLinkBluetooth == null && device.getName().toString().equals("FireFly-99DE")) {
	            	 brainLinkBluetooth = device;
	            	 mBrainLinkDeviceFound = true;
	            	// Toast.makeText(MainActivity.this, "Find Unpaired Device", Toast.LENGTH_LONG).show();
		             mBluetoothAdapter.cancelDiscovery();
		             	             	 
		             brainLinkConnectThread.start();
	             
	             }
	         } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
	                
	                if(brainLinkBluetooth == null) {
	        			Message m = new Message();
	        			m.what = MainActivity.DLG_DEVICE_NOT_FOUND;
	        			bluetoothMessageHandler.sendMessage(m);  
	        			try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {}
						mBluetoothConnected = false;
						m_bluetoothDialog.cancel();
	                }

	                mBluetoothAdapter.cancelDiscovery();
	            }
	     }


	 };
	
//    private class RobotListAdapter extends BaseAdapter {
//
//		@Override
//		public int getCount() {
//			return mRobotList != null ? mRobotList.size() : 0;
//		}
//
//		@Override
//		public Object getItem(int arg0) {
//			return null;
//		}
//
//		@Override
//		public long getItemId(int arg0) {
//			return 0;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			ViewHolder holder;
//			if(convertView == null) {
//				convertView = MainActivity.this.getLayoutInflater().inflate(R.layout.list_robot, null);
//				holder = new ViewHolder();
//				holder.title = (TextView) convertView.findViewById(R.id.title);
//				holder.description = (TextView) convertView.findViewById(R.id.info);
//				holder.icon = (ImageView) convertView.findViewById(R.id.img);
//				convertView.setTag(holder);
//			}
//			else {
//				holder = (ViewHolder)convertView.getTag();
//			}
//			
//			final ListRobotItem robotItem = mRobotList.get(position);
//			final String title = robotItem.getRobotItemName();
//			final String description = robotItem.getRobotItemDescription();
//			final String img = robotItem.getRobotItemIcon();
//			
//			holder.title.setText(title);
//			holder.description.setText(description);
//			holder.icon.setImageResource(R.drawable.i1);
//			
//			return convertView;
//		}
//    	
//    }
//    
//    static class ViewHolder {
//    	TextView title;
//    	TextView description;
//    	ImageView icon;
//    }
	


	private Thread bluetoothConnectThread = new Thread() {
		
		@Override
		public void run() {
			
			Message m = new Message();
			boolean quitThread = false;
			for(;;) {
				switch(mBluetoothAdapter.getState()) {
				case BluetoothAdapter.STATE_OFF:
					m.what = MainActivity.DLG_BT_CONNECTING;
					bluetoothMessageHandler.sendMessage(m);
					mBluetoothAdapter.enable();
					break;
				case BluetoothAdapter.STATE_ON:
	            	mBluetoothConnected = true;
	            	brainLinkFindThread.start();
	            	quitThread = true;
					break;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}		
				if(quitThread) {
					bluetoothConnectThread.stop();
					break;				
				}

			}
	
		};			
	};
	
	private Thread brainLinkFindThread = new Thread() {
		
		@Override
		public void run() {
			
			Message m = new Message();
			m.what = MainActivity.DLG_DEVICE_FINDING;
			bluetoothMessageHandler.sendMessage(m);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}

			
			// Scan paired devices
	    	if(!mBrainLinkDeviceFound) {
		    	Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		         if (pairedDevices.size() > 0) {
		             // Loop through paired devices
		             for (BluetoothDevice device : pairedDevices) {
		                 if(device.getName().toString().equals("FireFly-99DE")) {
		                	 brainLinkBluetooth = device;
		                	 m = new Message();
		                	 m.what = MainActivity.DLG_DEVICE_PAIRED_FOUND;
		                	 bluetoothMessageHandler.sendMessage(m);
		         			try {
		        				Thread.sleep(100);
		        			} catch (InterruptedException e) {}
		        			
		                	 mBrainLinkDeviceFound = true;
		                	 
		                	 
		                	 brainLinkConnectThread.start();
		                	 
		                	 brainLinkFindThread.stop();
		                	 break;
		                 }
		             }
		         }
	    	}
	         
	         // if BrainLinkDevice is not paired, then search for the device
	    	if(!mBrainLinkDeviceFound) {
	        	mBluetoothAdapter.startDiscovery();
	        	 IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	        	 registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

	             // Register for broadcasts when discovery has finished
	             filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
	             MainActivity.this.registerReceiver(mReceiver, filter); 
	         }
	    	
	    	if(!mBluetoothConnected) {
	    		brainLinkConnectThread.start();
	    	}		
		}
	};
	
	
	
	private Thread brainLinkConnectThread = new Thread () {
		
		@Override
		public void run() {
			
			Message m = new Message();
			m.what = MainActivity.DLG_DEVICE_CONNECTING;
			bluetoothMessageHandler.sendMessage(m);
			
			try {
				btSocket = brainLinkBluetooth.createRfcommSocketToServiceRecord(MY_UUID);
				
				btSocket.connect();
				
			} catch (IOException e) {}
			

			if(btSocket != null) {
				mBrainLinkDeviceConnected = true;
				m_bluetoothDialog.cancel();
			} else {
				try {
					btSocket.close();
					m_bluetoothDialog.cancel();
				} catch (IOException e) {}
			}
			
		} 
				
	};
	
}

