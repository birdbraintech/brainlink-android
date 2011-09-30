package edu.cmu.ri.createlab.brainlink;

import android.graphics.Color;
import edu.cmu.ri.createlab.brainlink.commands.SimpleIRCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface BrainLink 
   {
   Integer getBatteryVoltage();

   boolean setFullColorLED(final int red, final int green, final int blue);

   boolean setFullColorLED(final Color color);

   int[] getPhotoresistors();

   int[] getAccelerometerState();

   int[] getAnalogInputs();

   Integer getThermistor();

   boolean playTone(final int frequency);

   boolean turnOffSpeaker();

   boolean initializeIR(final byte[] initializationBytes);

   boolean sendSimpleIRCommand(final SimpleIRCommandStrategy commandStrategy);

   boolean sendSimpleIRCommand(final byte command);
   }