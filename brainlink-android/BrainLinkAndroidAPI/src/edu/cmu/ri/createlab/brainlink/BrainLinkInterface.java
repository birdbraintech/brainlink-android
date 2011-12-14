package edu.cmu.ri.createlab.brainlink;


/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface BrainLinkInterface
   {
   /** Returns <code>true</code> if the BrainLink is connected, <code>false</code> otherwise. */
   boolean isConnected();

   /**
    * Returns the current battery voltage in millivolts; returns <code>null</code> if the voltage could not be read.
    *
    * @return The battery voltage reading
    */
   Integer getBatteryVoltage();

   /**
    * Returns true if the battery reads low (less than 3500 millivolts), false otherwise.
    *
    * @return Low battery status
    */
   boolean isBatteryLow();

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
   boolean setFullColorLED(final int red, final int green, final int blue);

   /**
    * Returns the light sensor values; returns <code>null</code> if the light sensors could not be read.
    *
    * @return an int containing the light sensor value.
    */
   Integer getLightSensor();

   /**
    * Returns the accelerometer values in Gs; returns <code>null</code> if the accelerometer could not be read.
    *
    * @return an array containing the X, Y, and Z accelerometer readings in Gs
    */
   double[] getAccelerometerValuesInGs();

   /**
    * Returns the value of the accelerometer's X axis in Gs; returns <code>null</code> if the accelerometer could not be
    * read.
    *
    * @return the X acceleration in Gs
    */
   Double getXAccelerometer();

   /**
    * Returns the value of the accelerometer's Y axis in Gs; returns <code>null</code> if the accelerometer could not be
    * read.
    *
    * @return the Y Acceleration in Gs
    */
   Double getYAccelerometer();

   /**
    * Returns the value of the accelerometer's Z axis in Gs; returns <code>null</code> if the accelerometer could not be
    * read.
    *
    * @return the Z acceleration in Gs
    */
   Double getZAccelerometer();

   /**
    * Returns <code>true</code> if the BrainLink has been shaken since the last accelerometer read, <code>false</code>
    * otherise.  Returns <code>null</code> if the accelerometer could not be read.
    *
    * @return <code>true</code> if the accelerometer was shaken, <code>false</code> otherwise, <code>null</code> if read was unsuccessful
    */
   Boolean wasShaken();

   /**
    * Returns <code>true</code> if the BrainLink has been tapped since the last accelerometer read, <code>false</code>
    * otherise.  Returns <code>null</code> if the accelerometer could not be read.
    *
    * @return <code>true</code> if the accelerometer was tapped, <code>false</code> otherwise, <code>null</code> if read was unsuccessful
    */
   Boolean wasTapped();

   /**
    * Returns <code>true</code> if the BrainLink has been shaken or tapped since the last accelerometer read,
    * <code>false</code> otherise.  Returns <code>null</code> if the accelerometer could not be read.
    *
    * @return <code>true</code> if the accelerometer was shaken or tapped, <code>false</code> otherwise, <code>null</code> if read was unsuccessful
    */
   Boolean wasShakenOTapped();

   /**
    * Returns the analog input values; returns <code>null</code> if the inputs could not be read.
    *
    * @return A six element array containing the raw sensor values of the six external analog inputs
    */
   int[] getAnalogInputs();

   /**
    * Returns the value of the given analog input; returns <code>null</code> if the specified port could not be read or
    * is invalid.
    *
    * @return The raw analog reading (0-255) of one of the six external analog ports, <code>null</code> if the port couldn't be read
    */
   Integer getAnalogInput(final int port);

   /**
    *  Returns true if the digital input is logic high, false if low, and null if the port was invalid or could not be read.
    *
    * @param port sets the input port to read, valid numbers are 0-9
    * @return <code>true</code> if the value on the port is logic high, <code>false</code> if logic low, and null if it
    * could not be read
    */
   Boolean getDigitalInput(final int port);

   /**
    *  Sets one of the digital output ports; true for logic high, false for low.
    *
    * @param port sets the output port to use, valid numbers are 0-9
    * @param value sets the port to either logic high or low
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   boolean setDigitalOutput(final int port, final boolean value);

   /**
    * Sets the duty cycle of one of the two PWM ports.
    *
    * @param port the PWM port to set
    * @param dutyCycle the duty of the PWM signal, specified in % (0-100)
    * @param PWMfrequency the frequency of the PWM signal in Hertz.
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   boolean setPWM(final int port, final int dutyCycle, final int PWMfrequency);

   /**
    * Sets the voltage of one of the two DAC ports
    *
    * @param port the DAC port to set
    * @param value the value, in milliVolts, to set the DAC to.
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   boolean setDAC(final int port, final int value);

   /**
    *  Sets the baud rate of the auxiliary serial port. Only the baud rate is configurable. The serial port always uses
    *  8 bits, no flow control, and one stop bit.
    *
    * @param baudRate the baudrate, in baud, to configure the serial port to.
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   boolean configureSerialPort(final int baudRate);

   /**
    * Transmits a stream of bytes over the auxiliary serial port.
    *
    * @param  bytesToSend an array of bytes to send over the serial port
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   boolean transmitBytesOverSerial(final byte[] bytesToSend);

   /**
    *  Returns the auxiliary serial port's receive buffer.
    *
    * @return an array of ints corresponding to the serial receive buffer. The buffer can only handle 256 bytes at a time,
    * so in high-data transfer applications this must be checked frequently.
    */
   int[] receiveBytesOverSerial();

   /**
    * Plays a tone specified at the frequency in hertz specified by frequency.  Tone will not stop until turnOffSpeaker
    * is called.
    *
    * @param frequency frequency in Hz of the tone to play
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   boolean playTone(final int frequency);

   /**
    * Turns off the speaker
    *
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   boolean turnOffSpeaker();

   /**
    * Opens the device file specified by fileName and initializes the IR if the file is encoded. A number of encoded
    * files for popular robot platforms are provided in the "devices" directory, and you can make your own with the "StoreAndPlayEncodedSignals" utility.
    * The filename argument should not include the ".encsig" or ".rawsig" file name extensions - this is automatically appended.
    *
    * @param fileName The name of the file with Initialization data.
    * @param encoded If the file is encoded or raw
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   boolean initializeDevice(final String fileName, final boolean encoded);

   /**
    * Initializes the Infrared signal to mimic a given robot's communication protocol specified by initializationBytes.
    *
    * @param initializationBytes The bytes to send the Brainlink to configure it with a specific IR communication protocol.
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   boolean initializeIR(final byte[] initializationBytes);

   boolean sendSimpleIRCommand(final byte command);

   /**
    * Turns off the IR signal, used in Stop methods in certain robot classes
    *
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   boolean turnOffIR();

   /**
    *  Sends the signal stored in fileName to Brainlink for transmission over IR. Handles both encoded and raw signal
    *  files.
    *
    * @param signalName The name of the signal
    * @return true if transmission succeeded
    * @throws IllegalStateException if the device has not yet been initialized with a call to {@link #initializeDevice(String, boolean)}
    */
   boolean transmitIRSignal(final String signalName);

   /**
    * Used by transmitIRSignal if encoded is true. Sends an encoded IR command.
    *
    * @param commandStrategy An array of bytes encoding the command to send
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   boolean sendIRCommand(final byte[] commandBytes, final byte repeatCommandByte1, final byte repeatCommandByte2);

   /**
    *  Will record any IR signal detected by the IR receiver, and returns this signal's measurements as an array of ints.
    *  Array elements are measurements in milliseconds of the time between the signal's falling or rising edges.
    *  The signal always begins with a rising edge and ends with a falling edge, therefore an even number of elements is always
    *  expected.
    *
    * @return An array of time measurements corresponding to an infrared signal.
    */
   int[] recordIR();

   /**
    *  Stores the most recently recorded IR signal to the Brainlink's on-board EEPROM (which survives power cycling).
    *  There are five EEPROM positions to store IR signals to, so Brainlink can store up to 5 raw IR signals.
    *
    * @param position the position to store the IR signal to (range is 0 to 4)
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   boolean storeIR(final int position);

   /**
    *  Sends the IR signal recorded in the EEPROM position specified.
    *
    * @param position the position to play the IR signal from (range is 0 to 4)
    * @param repeatTime the amount of delay, in milliseconds, between successive signals. Use 0 if the signal should not repeat.
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   boolean playIR(final int position, final int repeatTime);

   /**
    *  Used by transmitIRSignal. Sends a "Raw" format IR signal to transmit over the tether's IR LED.
    *
    * @param signal the raw IR signal consisting of time measurements.
    * @param repeatTime the amount of delay, in milliseconds, between successive signals. Use 0 if the signal should not repeat.
    * @return <code>true</code> if the call was made successfully, <code>false</code> otherwise
    */
   boolean sendRawIR(final int[] signal, final int repeatTime);

   /**
    *  Returns the IR signal recorded in the EEPROM position specified so that the host computer can read and analyze it.
    *  Note that the signal returned is of the same format as that returned by recordIR.
    *
    * @param position the position to print the IR signal from (range is 0 to 4)
    * @return An array of time measurements corresponding to an infrared signal, null if invalid.
    */
   int[] printIR(final int position);
   }