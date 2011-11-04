package edu.cmu.ri.createlab.brainlink;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class BrainLinkConstants
   {
   /** The minimum supported full-color LED intensity */
   public static final int FULL_COLOR_LED_DEVICE_MIN_INTENSITY = 0;

   /** The maximum supported full-color LED intensity */
   public static final int FULL_COLOR_LED_DEVICE_MAX_INTENSITY = 255;

   /** The minimum supported tone frequency */
   public static final int TONE_MIN_FREQUENCY = 1;

   /** The maximum supported tone frequency */
   public static final int TONE_MAX_FREQUENCY = 31250;

   /** The number of photoresistors. */
   public static final int PHOTORESISTOR_DEVICE_COUNT = 2;

   /** The number of analog inputs. */
   public static final int ANALOG_INPUT_COUNT = 6;

   /** The number of accelerometer axes. */
   public static final int ACCELEROMETER_AXIS_COUNT = 3;

   private BrainLinkConstants()
      {
      // private to prevent instantiation
      }
   }
