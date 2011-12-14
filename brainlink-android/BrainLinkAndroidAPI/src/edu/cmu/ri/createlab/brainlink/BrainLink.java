package edu.cmu.ri.createlab.brainlink;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.cmu.ri.createlab.util.ByteUtils;
import edu.cmu.ri.createlab.util.MathUtils;

/**
 * The main class for interacting with a Brainlink. Contains methods necessary to directly control
 * or read from Brainlink. You must use the BluetoothConnection class to get the input and output
 * streams necessary to instantiate Brainlink.
 * @author Chris Bartley (bartley@cmu.edu)
 * @author Modified by Huaishu Peng
 */
public final class BrainLink implements BrainLinkInterface {

	/* This thread keeps BrainLink from resetting so long as the program is active. 
	 * basically just sends a request to read the battery voltage every 30 seconds.
	 */
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

	// Listing of the Brainlink serial commands (specified in http://www.brainlinksystem.com/brainlink-hardware-description#commands)
	private static final byte CONNECT_ON = '*';
	private static final byte CONNECT_OFF = 'Q';
	private static final byte BATTERY_VALUE = 'V';
	private static final byte TONE_VALUE = 'B';
	private static final byte LED_VALUE = 'O';
	private static final byte LIGHT_VALUE = 'L';
	private static final byte ACCELEROMETOR_VALUE = 'A';
	private static final byte ANALOG_VALUE = 'X';
	private static final byte DIGITALINPUT_VALUE = '<';
	private static final byte DIGITALOUTPUT_VALUE = '>';
	private static final byte PWM_VALUE = 'P';
	private static final byte DAC_VALUE = 'd';
	private static final byte SERIALSETTING_VALUE = 'C';
	private static final byte SPEAKER_OFF = 'b';
	private static final byte IR_VALUE = 'I';
	private static final byte IRTRANSMIT_VALUE = 'i';
	private static final byte IROFF_VALUE = '!';
	private static final byte IRRECORD_VALUE = 'R';
	private static final byte IRSTORE_VALUE = 'S';
	private static final byte IRPLAYSTORE_VALUE = 'G';
	private static final byte IRPRINTSTORE_VALUE = 'g';
	private static final byte RAW_VALUE = 's';
	private static final byte AUXRECEIVE_VALUE = 'r';
	private static final byte AUXTRANSMIT_VALUE = 't';
	
	private WakeupThread wakeupThread;

   /**
    * Creates the <code>BrainLink</code> object without a specified input or output stream, not recommended.
    */
	public BrainLink() {
		this(null, null);
	}

   /**
    * Creates the <code>BrainLink</code> by specifying input and output streams obtained using the 
    * BluetoothConnection class. Also starts a keep alive thread and moves Brainlink out of "discovery mode".
    * 
    */
	public BrainLink(final InputStream i, final OutputStream o) {
		outStream = o;
		inStream = i;
		isConnected.set(true);
		sendCommand(CONNECT_ON); // Send the connect command in case Brainlink is in discovery mode
		sleep(50);
		readTempBuffer();
		// Check that no thread is already running
		if (wakeupThread != null)
			wakeupThread = null;

		wakeupThread = new WakeupThread();
		wakeupThread.start();
	}
	   /** Returns <code>true</code> if the BrainLink is connected, <code>false</code> otherwise. */
	@Override
	public boolean isConnected() {
		return isConnected.get();
	}

	   /**
	    * Returns true if the battery reads low (less than 3500 millivolts), false otherwise.
	    *
	    * @return Low battery status
	    */
	@Override
	public boolean isBatteryLow() {
		return getBatteryVoltage() < 3600;
	}

	   /**
	    * Returns the light sensor values; returns <code>null</code> if the light sensors could not be read.
	    *
	    * @return an int containing the light sensor value.
	    */
	@Override
	public Integer getLightSensor() {
		return returnIntegerValue(LIGHT_VALUE);
	}

	   /**
	    * Returns the accelerometer values in Gs; returns <code>null</code> if the accelerometer could not be read.
	    *
	    * @return an array containing the X, Y, and Z accelerometer readings in Gs
	    */
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

	   /**
	    * Returns the value of the accelerometer's X axis in Gs; returns <code>null</code> if the accelerometer could not be
	    * read.
	    *
	    * @return the X acceleration in Gs
	    */
	@Override
	public Double getXAccelerometer() {
		return getAccelerometerAxisValue(0);
	}

	   /**
	    * Returns the value of the accelerometer's Y axis in Gs; returns <code>null</code> if the accelerometer could not be
	    * read.
	    *
	    * @return the Y Acceleration in Gs
	    */
	@Override
	public Double getYAccelerometer() {
		return getAccelerometerAxisValue(1);
	}
	
	   /**
	    * Returns the value of the accelerometer's Z axis in Gs; returns <code>null</code> if the accelerometer could not be
	    * read.
	    *
	    * @return the Z acceleration in Gs
	    */
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

	   /**
	    * Returns <code>true</code> if the BrainLink has been shaken since the last accelerometer read, <code>false</code>
	    * otherise.  Returns <code>null</code> if the accelerometer could not be read.
	    *
	    * @return <code>true</code> if the accelerometer was shaken, <code>false</code> otherwise, <code>null</code> if read was unsuccessful
	    */
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

	   /**
	    * Returns <code>true</code> if the BrainLink has been tapped since the last accelerometer read, <code>false</code>
	    * otherise.  Returns <code>null</code> if the accelerometer could not be read.
	    *
	    * @return <code>true</code> if the accelerometer was tapped, <code>false</code> otherwise, <code>null</code> if read was unsuccessful
	    */
	@Override
	public Boolean wasTapped() {
		final int[] rawValues = getRawAccelerometerState();
		if (rawValues != null) {
			final int tapShake = rawValues[3];
			return (tapShake & 32) == 32; // apply mask to get the tap state
		}
		return false;
	}

	   /**
	    * Returns <code>true</code> if the BrainLink has been shaken or tapped since the last accelerometer read,
	    * <code>false</code> otherise.  Returns <code>null</code> if the accelerometer could not be read.
	    *
	    * @return <code>true</code> if the accelerometer was shaken or tapped, <code>false</code> otherwise, <code>null</code> if read was unsuccessful
	    */
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

	   /**
	    * Returns the value of the given analog input; returns <code>null</code> if the specified port could not be read or
	    * is invalid.
	    *
	    * @return The raw analog reading (0-255) of one of the six external analog ports, <code>null</code> if the port couldn't be read
	    */
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

	   /**
	    *  Returns true if the digital input is logic high, false if low, and null if the port was invalid or could not be read.
	    *
	    * @param port sets the input port to read, valid numbers are 0-9
	    * @return <code>true</code> if the value on the port is logic high, <code>false</code> if logic low, and null if it
	    * could not be read
	    */
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

	   /**
	    *  Sets one of the digital output ports; true for logic high, false for low.
	    *
	    * @param port sets the output port to use, valid numbers are 0-9
	    * @param value sets the port to either logic high or low
	    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
	    */
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

	   /**
	    * Sets the duty cycle of one of the two PWM ports.
	    *
	    * @param port the PWM port to set
	    * @param dutyCycle the duty of the PWM signal, specified in % (0-100)
	    * @param PWMfrequency the frequency of the PWM signal in Hertz.
	    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
	    */
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

	   /**
	    * Sets the voltage of one of the two DAC ports
	    *
	    * @param port the DAC port to set
	    * @param value the value, in milliVolts, to set the DAC to.
	    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
	    */
	@Override
	// ///////////////////////////////////////////////////////////////////////////
	public boolean setDAC(final int port, final int value) {
		final byte[] command = new byte[] { DAC_VALUE, (byte) (port + 48),
				(byte) (value * 255 / 3300) };
		return sendCommand(command);

	}

	   /**
	    *  Sets the baud rate of the auxiliary serial port. Only the baud rate is configurable. The serial port always uses
	    *  8 bits, no flow control, and one stop bit.
	    *
	    * @param baudRate the baudrate, in baud, to configure the serial port to.
	    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
	    */
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

	   /**
	    * Transmits a stream of bytes over the auxiliary serial port.
	    *
	    * @param  bytesToSend an array of bytes to send over the serial port
	    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
	    */
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean transmitBytesOverSerial(final byte[] bytesToSend) {
		final byte[] command = new byte[bytesToSend.length+2];
		command[0] = AUXTRANSMIT_VALUE;
		command[1] = (byte)bytesToSend.length;
		System.arraycopy(bytesToSend, 0, command, 2, bytesToSend.length);
		writeCommand(10,16,command); 
		return false;
	}

	   /**
	    *  Returns the auxiliary serial port's receive buffer.
	    *
	    * @return an array of ints corresponding to the serial receive buffer. The buffer can only handle 256 bytes at a time,
	    * so in high-data transfer applications this must be checked frequently.
	    */
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int[] receiveBytesOverSerial() {
		byte command = AUXRECEIVE_VALUE;
		return returnVariableLengthIntegerArrayValue(command);
		
	}

	   /**
	    * Opens the device file specified by fileName and initializes the IR if the file is encoded. A number of encoded
	    * files for popular robot platforms are provided in the "devices" directory, and you can make your own with the "StoreAndPlayEncodedSignals" utility.
	    * The filename argument should not include the ".encsig" or ".rawsig" file name extensions - this is automatically appended.
	    *
	    * @param fileName The name of the file with Initialization data.
	    * @param encoded If the file is encoded or raw
	    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
	    */
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
	   /**
	    * Turns off the IR signal, used in Stop methods in certain robot classes
	    *
	    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
	    */
	@Override
	public boolean turnOffIR() {
		// TODO Auto-generated method stub
		return sendCommand(IROFF_VALUE);
	}
	
	   /**
	    *  Sends the signal stored in fileName to Brainlink for transmission over IR. Handles both encoded and raw signal
	    *  files.
	    *
	    * @param signalName The name of the signal
	    * @return true if transmission succeeded
	    * @throws IllegalStateException if the device has not yet been initialized with a call to {@link #initializeDevice(String, boolean)}
	    */
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

	   /**
	    * Used by transmitIRSignal if encoded is true. Sends an encoded IR command.
	    *
	    * @param commandStrategy An array of bytes encoding the command to send
	    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
	    */
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

	   /**
	    *  Will record any IR signal detected by the IR receiver, and returns this signal's measurements as an array of ints.
	    *  Array elements are measurements in milliseconds of the time between the signal's falling or rising edges.
	    *  The signal always begins with a rising edge and ends with a falling edge, therefore an even number of elements is always
	    *  expected.
	    *
	    * @return An array of time measurements corresponding to an infrared signal.
	    */
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int[] recordIR() {
		int[] rawValues = returnVariableLengthIntegerArrayValue(IRRECORD_VALUE, 6);
		if(rawValues == null) {
			return null;
		}
		int[] returnValues = new int[rawValues.length/2];
		for(int i = 0; i < rawValues.length/2; i++) {
			returnValues[i] = (rawValues[i*2]*256 + rawValues[i*2+1])*2;
		}
		return returnValues;
		
	}

	   /**
	    *  Stores the most recently recorded IR signal to the Brainlink's on-board EEPROM (which survives power cycling).
	    *  There are five EEPROM positions to store IR signals to, so Brainlink can store up to 5 raw IR signals.
	    *
	    * @param position the position to store the IR signal to (range is 0 to 4)
	    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
	    */
	@Override
	public boolean storeIR(int position) {
		final byte[] command = new byte[] { IRSTORE_VALUE,
				(byte) (position + 48) };
		return sendCommand(command);
	}

	   /**
	    *  Sends the IR signal recorded in the EEPROM position specified.
	    *
	    * @param position the position to play the IR signal from (range is 0 to 4)
	    * @param repeatTime the amount of delay, in milliseconds, between successive signals. Use 0 if the signal should not repeat.
	    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
	    */
	@Override
	public boolean playIR(int position, int repeatTime) {
		final byte[] command = new byte[] { IRPLAYSTORE_VALUE,
				(byte) (position + 48), getHighByteFromInt(repeatTime),
				getLowByteFromInt(repeatTime) };

		return sendCommand(command);
	}

	   /**
	    *  Used by transmitIRSignal. Sends a "Raw" format IR signal to transmit over the tether's IR LED.
	    *
	    * @param signal the raw IR signal consisting of time measurements.
	    * @param repeatTime the amount of delay, in milliseconds, between successive signals. Use 0 if the signal should not repeat.
	    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
	    */
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

	   /**
	    *  Returns the IR signal recorded in the EEPROM position specified so that the host computer can read and analyze it.
	    *  Note that the signal returned is of the same format as that returned by recordIR.
	    *
	    * @param position the position to print the IR signal from (range is 0 to 4)
	    * @return An array of time measurements corresponding to an infrared signal, null if invalid.
	    */
	@Override
	public int[] printIR(int position) {
		final byte[] command = new byte[] { IRPRINTSTORE_VALUE, (byte) position };

		return returnIntegerArrayValue(command, 9);
	}
	
	   /**
	    * Returns the current battery voltage in millivolts; returns <code>null</code> if the voltage could not be read.
	    *
	    * @return The battery voltage reading
	    */
	@Override
	public Integer getBatteryVoltage() {

		final Integer rawValue = returnIntegerValue(BATTERY_VALUE);

		if (rawValue != null) {
			return (rawValue * 2650) / 128;
		}
		return null;
	}

	   /**
	    * Sets the color of the LED in the Brainlink.  The LED can be any color that can be created by mixing red, green,
	    * and blue; turning on all three colors in equal amounts results in white light.  Valid ranges for the red, green,
	    * and blue elements are 0 to 255.
	    *
	    * @param  red sets the intensity of the red element of the LED
	    * @param  green sets the intensity of the green element of the LED
	    * @param  blue sets the intensity of the blue element of the LED
	    * @return <code>true</code> if LED was successfully set, <code>false</code> otherwise
	    */
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

	   /**
	    * Returns the analog input values; returns <code>null</code> if the inputs could not be read.
	    *
	    * @return A six element array containing the raw sensor values of the six external analog inputs
	    */
	@Override
	public int[] getAnalogInputs() {
		return returnIntegerArrayValue(ANALOG_VALUE, 6);
	}

	   /**
	    * Plays a tone specified at the frequency in hertz specified by frequency.  Tone will not stop until turnOffSpeaker
	    * is called.
	    *
	    * @param frequency frequency in Hz of the tone to play
	    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
	    */
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

	   /**
	    * Turns off the speaker
	    *
	    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
	    */
	@Override
	public boolean turnOffSpeaker() {
		return sendCommand(SPEAKER_OFF);
	}
	   /**
	    * Initializes the Infrared signal to mimic a given robot's communication protocol specified by initializationBytes. 
	    * Typically used by initializeDevice.
	    *
	    * @param initializationBytes The bytes to send the Brainlink to configure it with a specific IR communication protocol.
	    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
	    */
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
				// Check that the buffer length is as long as we think it is. If not
				if(buff.length < returnCount) {
					
				}
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

	int[] returnVariableLengthIntegerArrayValue(byte b) {

		readTempBuffer();
		sendCommand(b);

		try {
			int count = inStream.available();

			if (count != 0) {
				final byte[] buff = new byte[257];
				inStream.read(buff);
				// If we get an error, break out of the loop immediately
				if(buff[1] == 'E' && buff[2] == 'R' && buff[3] == 'R') 
				{
					return null;
				}
				// buff[1] holds the length of the returned data, including itself
				int[] returnValue = new int[buff[1]-1];
				for (int i = 0; i < buff[1]-1; i++) {
					returnValue[i] = ByteUtils.unsignedByteToInt(buff[i + 2]);
				}
				return returnValue;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}	

	int[] returnVariableLengthIntegerArrayValue(byte b, int delay) {

		readTempBuffer();
		sendCommand(b);
		readTempBuffer();

		try {
			int delayCounter = 0;
			int count = 0;
			do
			{
				count = inStream.available();
				System.out.println("Count: " + count);
				delayCounter++;
				// wait 100 ms to check again
				sleep(100);
			} while((count ==0) && (delayCounter < (delay*10)));

			if (count != 0) {
				final byte[] buff = new byte[257];
				inStream.read(buff);
				// If we get an error, break out of the loop immediately
				if((buff[0] == 'E') && (buff[1] == 'R') && (buff[2] == 'R')) 
				{
					return null;
				}
				// buff[1] holds the length of the returned data, including itself
				for (int i = 0; i < buff.length; i++) {
					System.out.print(buff[i] + " ");
				}
				
				int[] returnValue = new int[ByteUtils.unsignedByteToInt(buff[0])-1];
				for (int i = 0; i < ByteUtils.unsignedByteToInt(buff[0])-1; i++) {
					returnValue[i] = ByteUtils.unsignedByteToInt(buff[i + 2]);
					System.out.print(buff[i] + " ");
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