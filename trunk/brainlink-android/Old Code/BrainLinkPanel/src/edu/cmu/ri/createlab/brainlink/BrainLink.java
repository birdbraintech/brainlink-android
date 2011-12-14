package edu.cmu.ri.createlab.brainlink;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.CharBuffer;

import edu.cmu.ri.createlab.util.ByteUtils;
import edu.cmu.ri.createlab.util.MathUtils;

import android.graphics.Color;

public class BrainLink {
	
	private boolean connectStatus;
	private boolean soundStatus=false;
	private OutputStream outStream = null;
	private InputStream inStream = null;
	
	private static final byte CONNECT_ON = '*';
	private static final byte CONNECT_OFF = 'Q';
	private static final byte BATTERY_VALUE = 'B';
	private static final byte LED_VALUE = 'O';
	private static final byte SOUND_ON = 'D';
	private static final byte SOUND_OFF = 'd';
	private static final byte LIGHT_VALUE = 'L';
	private static final byte ACCELEROMETOR_VALUE = 'A';
	private static final byte ANALOG_VALUE = 'X';
	private static final byte TEMPERATURE_VALUE = 'T';
	
	BrainLink(InputStream i, OutputStream o) {
		connectStatus = false;
		outStream = o;
		inStream = i;
	}
	
	boolean getConnectStatus() {
		return connectStatus;
	}
	
	boolean connectionOn() {
		if(!connectStatus) {
			try {
				outStream.write(CONNECT_ON);
				connectStatus = true;
			} catch (IOException e) {}
		}
		return connectStatus;
	}
	
	boolean connectionOff() {
		if(connectStatus) {
			try {
				outStream.write(CONNECT_OFF);
				connectStatus = false;
			} catch (IOException e) {}			
		}
		return connectStatus;
	}
	
   Integer getBatteryVoltage() {
	   if(connectStatus) {
		   try {
			   final byte[] buff = new byte[2];
			
			   readTempBuffer();
			   sendCommand(BATTERY_VALUE);
			
			   int count = inStream.available();
			
			if(count!=0)
				inStream.read(buff);
			
			return ByteUtils.unsignedByteToInt(buff[1]);
			} catch(IOException e) {}
	   }
	   return -1;
   }
   
   Integer getThermistor() {
	   if(connectStatus) {
		   try {
			   final byte[] buff = new byte[2];
			
			   readTempBuffer();
			   sendCommand(TEMPERATURE_VALUE);
			
			   int count = inStream.available();
			
			if(count!=0)
				inStream.read(buff);
			
			return ByteUtils.unsignedByteToInt(buff[1]);
			} catch(IOException e) {}
	   }
	   return -1;
   }
   
   int[] getPhotoresistors() {
	   if(connectStatus) {
		   try {
				final byte[] buff = new byte[3];

				readTempBuffer();
				sendCommand(LIGHT_VALUE);

				int count = inStream.available();	
				if(count!=0)
					inStream.read(buff);
					  			
				return new int[]{ByteUtils.unsignedByteToInt(buff[1]),
		                ByteUtils.unsignedByteToInt(buff[2])};
			} catch (IOException e) {}
	   }
	   return null;
   }

   int[] getAccelerometer() {
	   if(connectStatus) {
		   try {
				final byte[] buff = new byte[4];
				
				readTempBuffer();
				sendCommand(ACCELEROMETOR_VALUE);
				
				int count = inStream.available();	
				if(count!=0)
					inStream.read(buff);
				
				return new int[]{ByteUtils.unsignedByteToInt(buff[1]),
		                ByteUtils.unsignedByteToInt(buff[2]),
		                ByteUtils.unsignedByteToInt(buff[3])};
			} catch (IOException e) {}
	   }		   
	   return null;
   }
   
   int[] getAnalogInputs() {
	   if(connectStatus) {
		   try {
				final byte[] buff = new byte[6];
				
				readTempBuffer();
				sendCommand(ANALOG_VALUE);
				
				int count = inStream.available();	
				if(count!=0)
					inStream.read(buff);
				
				return new int[]{ByteUtils.unsignedByteToInt(buff[1]),
		                ByteUtils.unsignedByteToInt(buff[2]),
		                ByteUtils.unsignedByteToInt(buff[3]),
		                ByteUtils.unsignedByteToInt(buff[4]),
		                ByteUtils.unsignedByteToInt(buff[5])};
			} catch (IOException e) {}
	   }		   
	   return null;
   }


   
   boolean setFullColorLED(final int red, final int green, final int blue) {
	   if(connectStatus) {
		   byte[] command = new byte[] {LED_VALUE, ByteUtils.intToUnsignedByte(MathUtils.ensureRange(green, 0, 255)),
	           ByteUtils.intToUnsignedByte(MathUtils.ensureRange(red, 0, 255)),
	           ByteUtils.intToUnsignedByte(MathUtils.ensureRange(blue, 0, 255))};
		   try {
				outStream.write(command);
				   return true;
				} catch (IOException e) {}
	   }
	   return false;

   }
   
   boolean playTone(final int frequency) {
	   if(connectStatus) {
		   byte[] command = new byte[] {SOUND_ON, ByteUtils.intToUnsignedByte(MathUtils.ensureRange(frequency, 0, 65535))};
		   try {
				outStream.write(command);
				   return true;
				} catch (IOException e) {}
	   }
	   return false;

   }
      
   boolean turnOffSpeaker() {
	   if(connectStatus && soundStatus) {
		   try {
				outStream.write(SOUND_OFF);
				soundStatus = false;
				   return true;
				} catch (IOException e) {}
	   }
	   return false;	   
   }
   
   boolean turnOnSpeaker() {
	   if(connectStatus && !soundStatus) {
		   try {
				outStream.write(SOUND_ON);
				soundStatus = true;
				   return true;
				} catch (IOException e) {} 
	   }
	   return false;	
   }

   boolean getSpeakerStatus() {
	   return soundStatus;
   }
   boolean initializeIR(final byte[] initializationBytes) {
	   return false;
   }
   
   void sendCommand(byte b) {
		try {
			outStream.write(b);
			Thread.sleep(100);
		} catch (IOException e1) {} catch (InterruptedException e) {}
   }
   
   void readTempBuffer() {
		int count = 0;
		try {
			count = inStream.available();
			byte[] tempBuff = new byte[count];
			  if(count!=0)
					inStream.read(tempBuff);
			  } catch (IOException e) {}		
		}
   }