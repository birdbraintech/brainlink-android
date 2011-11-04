package edu.cmu.ri.createlab.brainlink;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.cmu.ri.createlab.brainlink.android.BrainLinkInterface;
import edu.cmu.ri.createlab.util.ByteUtils;
import edu.cmu.ri.createlab.util.MathUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 * @author Modified by Huaishu Peng
 */
public final class BrainLink implements BrainLinkInterface {

	class WakeupThread extends Thread {
		boolean quitThread = false;

		@Override
		public void run() {
			for (;;) {

				try {
					sleep(30000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				getBatteryVoltage();
				if (quitThread)
					return;
			}
		}
	}

	private OutputStream outStream = null;
	private InputStream inStream = null;
	private final AtomicBoolean isConnected = new AtomicBoolean(false);
	private BrainLinkFileManipulator deviceFile;

	private static final byte CONNECT_ON = '*';
	private static final byte CONNECT_OFF = 'Q';
	private static final byte BATTERY_VALUE = 'V';
	private static final byte TONE_VALUE = 'B';
	private static final byte LED_VALUE = 'O';
	private static final byte LIGHT_VALUE = 'L';
	private static final byte ACCELEROMETOR_VALUE = 'A';
	private static final byte ANALOG_VALUE = 'X';
	private static final byte TEMPERATURE_VALUE = 'T';
	private static final byte DIGITALINPUT_VALUE = '<';
	private static final byte DIGITALOUTPUT_VALUE = '>';
	private static final byte PWM_VALUE = 'P';
	private static final byte DAC_VALUE = 'd';
	private static final byte SERIALSETTING_VALUE = 'C';
	private static final byte SERIALTRANSMIT_VALUE = 't';
	private static final byte SERIALRECEIVE_VALUE = 'r';
	private static final byte SPEAKER_OFF = 'b';
	private static final byte IR_VALUE = 'I';
	private static final byte IRTRANSMIT_VALUE = 'i';
	private static final byte IROFF_VALUE = '!';
	private static final byte IRRECORD_VALUE = 'R';
	private static final byte IRSTORE_VALUE = 'S';
	private static final byte IRPLAYSTORE_VALUE = 'G';
	private static final byte IRPRINTSTORE_VALUE = 'g';
	private static final byte RAW_VALUE = 's';

	private WakeupThread wakeupThread;

	public BrainLink() {
		this(null, null);
	}

	public BrainLink(final InputStream i, final OutputStream o) {
		outStream = o;
		inStream = i;
		isConnected.set(true);
		sendCommand(CONNECT_ON);
		sleep(50);
		readTempBuffer();
		if (wakeupThread != null)
			wakeupThread = null;

		wakeupThread = new WakeupThread();
		wakeupThread.start();
	}

	@Override
	public boolean isConnected() {
		return isConnected.get();
	}

	@Override
	public boolean isBatteryLow() {
		return getBatteryVoltage() < 3500;
	}

	@Override
	public Integer getLightSensor() {
		return returnIntegerValue(LIGHT_VALUE);
	}

	@Override
	public double[] getAccelerometerValuesInGs() {
		final int[] rawValues = getRawAccelerometerState();
		if (rawValues != null) {
			return AccelerometerUnitConverterFreescaleMMA7660FC.getInstance()
					.convert(rawValues);
		}
		return null;
	}

	private int[] getRawAccelerometerState() {
		return returnIntegerArrayValue(ACCELEROMETOR_VALUE, 4);
	}

	@Override
	public Double getXAccelerometer() {
		return getAccelerometerAxisValue(0);
	}

	@Override
	public Double getYAccelerometer() {
		return getAccelerometerAxisValue(1);
	}

	@Override
	public Double getZAccelerometer() {
		return getAccelerometerAxisValue(2);
	}

	private Double getAccelerometerAxisValue(final int axisIndex) {
		if (0 <= axisIndex
				&& axisIndex < BrainLinkConstants.ACCELEROMETER_AXIS_COUNT) {
			final double[] values = getAccelerometerValuesInGs();
			if (values != null) {
				return values[axisIndex];
			}
		}
		return null;
	}

	@Override
	public Boolean wasShaken() {
		final int[] rawValues = getRawAccelerometerState();
		if (rawValues != null) {
			final int tapShake = rawValues[3];
			return (tapShake & 128) == 128; // apply mask to get the shaken
											// state
		}
		return false;
	}

	@Override
	public Boolean wasTapped() {
		final int[] rawValues = getRawAccelerometerState();
		if (rawValues != null) {
			final int tapShake = rawValues[3];
			return (tapShake & 32) == 32; // apply mask to get the tap state
		}
		return false;
	}

	@Override
	public Boolean wasShakenOTapped() {
		final int[] rawValues = getRawAccelerometerState();
		if (rawValues != null) {
			final int tapShake = rawValues[3];
			final boolean wasTapped = (tapShake & 32) == 32;
			final boolean wasShaken = (tapShake & 128) == 128;
			return wasTapped || wasShaken;
		}
		return false;
	}

	@Override
	public Integer getAnalogInput(final int port) {
		if (0 <= port && port < BrainLinkConstants.ANALOG_INPUT_COUNT) {
			final int[] inputs = getAnalogInputs();
			if (inputs != null) {
				return inputs[port];
			}
		}
		return null;
	}

	@Override
	public Boolean getDigitalInput(final int port) {
		final byte[] command;
		command = new byte[] { DIGITALINPUT_VALUE, (byte) (port + 48) };
		int temp = returnIntegerValue(command);
		if (temp == 0)
			return false;
		else if (temp == 1)
			return true;
		else
			return null;
	}

	@Override
	// ///////////////////////////////////////////////////////////////////////////
	public boolean setDigitalOutput(final int port, final boolean value) {
		final byte[] command;
		if (value)
			command = new byte[] { DIGITALOUTPUT_VALUE, (byte) (port + 48), '1' };
		else
			command = new byte[] { DIGITALOUTPUT_VALUE, (byte) (port + 48), '0' };

		return sendCommand(command);
	}

	@Override
	// ///////////////////////////////////////////////////////////////////////////
	public boolean setPWM(final int port, final int dutyCycle,
			final int PWMfrequency) {

		int pwmPER = 32000000 / PWMfrequency - 1; // Calculate the Period based
													// on desired frequency
		int trueDuty = pwmPER * dutyCycle / 1000; // Calculate the real duty
													// cycle based on desired
													// duty cycle
		final byte[] command = new byte[] { PWM_VALUE,
				getHighByteFromInt(pwmPER), getLowByteFromInt(pwmPER), 'p',
				(byte) (port + 48), getHighByteFromInt(trueDuty),
				getLowByteFromInt(trueDuty) };
		return sendCommand(command);
	}

	@Override
	// ///////////////////////////////////////////////////////////////////////////
	public boolean setDAC(final int port, final int value) {
		final byte[] command = new byte[] { DAC_VALUE, (byte) (port + 48),
				(byte) (value * 255 / 3300) };
		return sendCommand(command);

	}

	@Override
	public boolean configureSerialPort(final int baudRate) {
		final byte[] command;
		if (baudRate >= 9600) {
			int baud = (32000000 / baudRate - 16);
			byte scale = -4;
			command = new byte[] { SERIALSETTING_VALUE,
					getHighByteFromInt(baud), getLowByteFromInt(baud), scale };
		} else {
			int baud = (int) ((32000000 / (baudRate * 16) - 1));
			byte scale = 0;
			command = new byte[] { SERIALSETTING_VALUE,
					getHighByteFromInt(baud), getLowByteFromInt(baud), scale };
		}
		returnIntegerValue(command);
		return true;
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean transmitBytesOverSerial(final byte[] bytesToSend) {

		return false;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int[] receiveBytesOverSerial() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean initializeDevice(final String fileName, final boolean encoded) {
		deviceFile = new BrainLinkFileManipulator(fileName, encoded);

		if (deviceFile.isEmpty()) {
			// System.out.println("Error, this file does not exist or is empty");
			return false;
		}

		if (encoded) {
			final byte[] initData = deviceFile.getInitialization();
			if (initData == null) {
				// System.out.println("Error: No initialization in this encoded file");
				return false;
			} else {
				initializeIR(initData);
				return true;
			}
		}
		return true;
	}

	@Override
	public boolean turnOffIR() {
		// TODO Auto-generated method stub
		return sendCommand(IROFF_VALUE);
	}

	@Override
	public boolean transmitIRSignal(String signalName) {
		// do a null check for deviceFile, which can happen if the user didn't
		// call initializeDevice first
		if (deviceFile == null) {
			throw new IllegalStateException(
					"Cannot transmit IR signal because the device has not been initialized yet.  You must call initializeDevice() before calling this method.");
		} else {
			if (!deviceFile.containsSignal(signalName)) {
				System.out.println("Error: signal not contained in file");
				return false;
			}

			final int[] signalValues = deviceFile.getSignalValues(signalName);
			final int repeatTime = deviceFile.getSignalRepeatTime(signalName);

			if (deviceFile.isEncoded()) {
				final byte[] signalInBytes = new byte[signalValues.length];
				for (int i = 0; i < signalValues.length; i++) {
					signalInBytes[i] = (byte) signalValues[i];
				}
				final byte repeat1 = getHighByteFromInt(repeatTime);
				final byte repeat2 = getLowByteFromInt(repeatTime);
				return sendIRCommand(signalInBytes, repeat1, repeat2);
			} else {
				return sendRawIR(signalValues, repeatTime);
			}
		}
	}

	@Override
	public boolean sendIRCommand(final byte[] commandBytes,
			final byte repeatCommandByte1, final byte repeatCommandByte2) {
		final byte[] command = new byte[commandBytes.length + 3];
		command[0] = IRTRANSMIT_VALUE;
		System.arraycopy(commandBytes, 0, command, 1, commandBytes.length);
		command[command.length - 2] = repeatCommandByte1;
		command[command.length - 1] = repeatCommandByte2;

		return sendCommand(command);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int[] recordIR() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean storeIR(int position) {
		final byte[] command = new byte[] { IRSTORE_VALUE,
				(byte) (position + 48) };
		return sendCommand(command);
	}

	@Override
	public boolean playIR(int position, int repeatTime) {
		final byte[] command = new byte[] { IRPLAYSTORE_VALUE,
				(byte) (position + 48), getHighByteFromInt(repeatTime),
				getLowByteFromInt(repeatTime) };

		return sendCommand(command);
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean sendRawIR(int[] signal, int repeatTime) {
		final byte[] command = new byte[signal.length * 2 + 4];
		command[0] = RAW_VALUE;
		command[1] = getLowByteFromInt((signal.length * 2 + 1));
		int j = 2;

		for (int i = 0; i < signal.length; i++) {
			// The signal is in microseconds, but the Brainlink expects it in
			// increments of 2 us, so we divide by 2
			signal[i] /= 2;
			command[j] = getHighByteFromInt(signal[i]);
			j++;
			command[j] = getLowByteFromInt(signal[i]);
			j++;
		}

		command[j] = getHighByteFromInt(repeatTime);
		j++;
		command[j] = getLowByteFromInt(repeatTime);

		writeCommand(10, 16, command);
		return false;
	}

	@Override
	public int[] printIR(int position) {
		final byte[] command = new byte[] { IRPRINTSTORE_VALUE, (byte) position };

		return returnIntegerArrayValue(command, 9);
	}

	@Override
	public Integer getBatteryVoltage() {

		final Integer rawValue = returnIntegerValue(BATTERY_VALUE);

		if (rawValue != null) {
			return (rawValue * 2650) / 128;
		}
		return null;
	}

	@Override
	public boolean setFullColorLED(final int red, final int green,
			final int blue) {
		final byte[] command;
		command = new byte[] {
				LED_VALUE,
				ByteUtils
						.intToUnsignedByte(MathUtils
								.ensureRange(
										red,
										BrainLinkConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY,
										BrainLinkConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY)),
				ByteUtils
						.intToUnsignedByte(MathUtils
								.ensureRange(
										green,
										BrainLinkConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY,
										BrainLinkConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY)),
				ByteUtils
						.intToUnsignedByte(MathUtils
								.ensureRange(
										blue,
										BrainLinkConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY,
										BrainLinkConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY)) };

		return sendCommand(command);
	}

	@Override
	public int[] getAnalogInputs() {
		return returnIntegerArrayValue(ANALOG_VALUE, 6);
	}

	@Override
	public boolean playTone(final int frequency) {
		final int cleanedFrequency = MathUtils.ensureRange(frequency,
				BrainLinkConstants.TONE_MIN_FREQUENCY,
				BrainLinkConstants.TONE_MAX_FREQUENCY);
		final int ccaValue = 62500 / cleanedFrequency - 1;
		final byte[] command = new byte[] { TONE_VALUE,
				getHighByteFromInt(ccaValue), getLowByteFromInt(ccaValue) };

		return sendCommand(command);
	}

	@Override
	public boolean turnOffSpeaker() {
		return sendCommand(SPEAKER_OFF);
	}

	@Override
	public boolean initializeIR(final byte[] initializationBytes) {
		final byte[] command = new byte[initializationBytes.length + 1];
		final byte[] expectedCommandEcho;

		command[0] = IR_VALUE;
		System.arraycopy(initializationBytes, 0, command, 1,
				initializationBytes.length);

		expectedCommandEcho = new byte[command.length + 1];
		System.arraycopy(command, 0, expectedCommandEcho, 0, command.length); // make
																				// an
																				// exact
																				// copy

		final int byteNumberToStartCopy = command.length - 7;
		System.arraycopy(command, byteNumberToStartCopy, expectedCommandEcho,
				byteNumberToStartCopy + 1, 7);

		sendCommand(command);
		readTempBuffer();

		return true;
	}

	@Override
	public boolean sendSimpleIRCommand(byte ircommand) {
		final byte[] command = new byte[] { IRTRANSMIT_VALUE, ircommand, 0x0,
				0x0 };
		return sendCommand(command);
	}

	byte[] readTempBuffer() {
		int count = 1023;
		try {
			if (inStream.available() > 0) {
				byte[] temp = new byte[count];
				inStream.read(temp);
				return temp;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	boolean sendCommand(byte[] command) {
		try {
			outStream.write(command);
			sleep(50);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	boolean sendCommand(byte command) {
		try {
			outStream.write(command);
			sleep(50);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	int[] returnIntegerArrayValue(byte b, int returnCount) {
		readTempBuffer();
		sendCommand(b);

		try {
			int count = inStream.available();

			if (count != 0) {
				final byte[] buff = new byte[returnCount + 1];
				inStream.read(buff);
				int[] returnValue = new int[returnCount];
				if (buff[0] == b) {
					for (int i = 0; i < returnCount; i++) {
						returnValue[i] = ByteUtils
								.unsignedByteToInt(buff[i + 1]);
					}
					return returnValue;
				} else
					return null;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	int[] returnIntegerArrayValue(byte[] b, int returnCount) {

		readTempBuffer();
		sendCommand(b);

		try {
			int count = inStream.available();

			if (count != 0) {
				final byte[] buff = new byte[returnCount + 1];
				int[] returnValue = new int[returnCount];
				inStream.read(buff);
				for (int i = 0; i < returnCount; i++) {
					returnValue[i] = ByteUtils.unsignedByteToInt(buff[i + 1]);
				}
				return returnValue;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	Integer returnIntegerValue(byte b) {

		readTempBuffer();
		sendCommand(b);

		try {
			int count = inStream.available();

			if (count != 0) {
				final byte[] buff = new byte[count + 10];
				inStream.read(buff);
				if (buff[0] == b)
					return ByteUtils.unsignedByteToInt(buff[1]);
				else
					return null;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	Integer returnIntegerValue(byte[] b) {

		readTempBuffer();
		sendCommand(b);

		try {
			int count = inStream.available();

			if (count != 0) {
				final byte[] buff = new byte[1023];
				inStream.read(buff);
				return ByteUtils.unsignedByteToInt(buff[1]);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public void sleep(final int millis) {
		try {
			if (millis > 0) {
				Thread.sleep(millis);
			} else {
				System.out.println("Error: sent negative time to sleep");
			}
		} catch (InterruptedException e) {
			System.out.println("Error while sleeping: " + e);
		}
	}

	private byte getHighByteFromInt(final int val) {
		return (byte) ((val << 16) >> 24);
	}

	private byte getLowByteFromInt(final int val) {
		return (byte) ((val << 24) >> 24);
	}
	
	
	 //copy and revised based on CreateLabSerialDeviceCommandStrategy
	protected final boolean writeCommand(int pauseInMillisBetweenChunksWritten, int chunkSizeInBytes, final byte[] command)
    {
    // initialize the retry count
    int numWrites = 0;

    boolean echoDetected;
    do
       {
       echoDetected = writeCommandWorkhorse(pauseInMillisBetweenChunksWritten,chunkSizeInBytes, command);
       numWrites++;
       if (!echoDetected)
          {
          slurp(pauseInMillisBetweenChunksWritten, chunkSizeInBytes);
          }
       }
    while (!echoDetected && numWrites < 5);

    return echoDetected;
    }
	
	
	 //copy and revised based on CreateLabSerialDeviceCommandStrategy
	 private boolean writeCommandWorkhorse(int pauseInMillisBetweenChunksWritten, int chunkSizeInBytes, final byte[] command)
     {
     try
        {


        outStream.write(command);


        // initialize the counter for reading from the command
        int pos = 0;

        // initialize the flag which tracks whether the command was correctly echoed
        boolean isMatch = true;

        // define the ending time
        final long endTime = 1000 + System.currentTimeMillis();
        while ((pos < command.length) && (System.currentTimeMillis() <= endTime))
           {
           if (inStream.available()>0)
              {
              final byte expected = command[pos];
              final int actual = inStream.read();
              pos++;                                 // increment the read counter


              // see if we reached the end of the stream
              if (actual >= 0)
                 {
                 final byte actualAsByte = (byte)actual;
                 // make sure this character in the command matches; break if not
                 if (expected != actualAsByte)
                    {
                     isMatch = false;
                    break;
                    }
                 }
              else
                 {
                 break;
                 }
              }
           }

        final boolean echoDetected = (pos == command.length) && isMatch;


        return echoDetected;
        }
     catch (IOException e)
        {
        }

     return false;
     }
	 
	 //copy and revised based on CreateLabSerialDeviceCommandStrategy
	 protected final void slurp(int pauseInMillisBetweenChunksWritten, int chunkSizeInBytes)
     {
     final long endTime = 5000 + System.currentTimeMillis();

     try
        {
        // read until we exhaust the available data, or until we run out of time
        while (inStream.available()>0 && System.currentTimeMillis() <= endTime)
           {
           try
              {
              final int c = inStream.read();
              }
           catch (IOException e)
              {
              break;
              }
           }
        }
     catch (IOException e)
        {
        }
     }

}