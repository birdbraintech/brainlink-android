package edu.cmu.ri.createlab.brainlink;

/**
 * <p>
 * <code>AccelerometerUnitConverterFreescaleMMA7660FC</code> performs unit conversions for the Freescale MMA7660FC
 * accelerometer.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class AccelerometerUnitConverterFreescaleMMA7660FC
   {
   private static final AccelerometerUnitConverterFreescaleMMA7660FC INSTANCE = new AccelerometerUnitConverterFreescaleMMA7660FC();

   static final int MIN_NATIVE_VALUE = 0x00;        // 0
   static final int MIDPOINT_NATIVE_VALUE = 0x1F;   // 31
   static final int MAX_NATIVE_VALUE = 0x3F;        // 63

   private static final double MULTIPLIER = 1.5 / 32.0;
   static final double MIN_G = -1.5;
   static final double MAX_G = 1.453125;

   static AccelerometerUnitConverterFreescaleMMA7660FC getInstance()
      {
      return INSTANCE;
      }

   private AccelerometerUnitConverterFreescaleMMA7660FC()
      {
      // private to prevent instantiation
      }

   /**
    * Converts the native values in the given <code>rawValues</code> to Gs.  Native values which are less than
    * {@link #MIN_NATIVE_VALUE} will be mapped to {@link #MIN_G} and native values which are greater than
    * {@link #MAX_NATIVE_VALUE} will be mapped to -0.046875 Gs.  The given <code>rawValues</code> array must be of
    * length greater than or equal to {@link BrainLinkConstants#ACCELEROMETER_AXIS_COUNT}.  If the array is longer,
    * only the first {@link BrainLinkConstants#ACCELEROMETER_AXIS_COUNT} values are converted, so that the returned
    * array is guaranteed to be of length {@link BrainLinkConstants#ACCELEROMETER_AXIS_COUNT} (or <code>null</code>,
    * as described above).
    */
   double[] convert(final int[] rawValues)
      {
      if (rawValues != null && rawValues.length >= BrainLinkConstants.ACCELEROMETER_AXIS_COUNT)
         {
         final double[] values = new double[BrainLinkConstants.ACCELEROMETER_AXIS_COUNT];
         for (int i = 0; i < values.length; i++)
            {
            values[i] = convertToGs(rawValues[i]);
            }
         return values;
         }
      return null;
      }

   /**
    * Converts the G values in the given <code>gValues</code> to native values.  G values which are less than
    * {@link #MIN_G} will be mapped to 32 and G values which are greater than {@link #MAX_G} will be mapped to
    * {@link #MIDPOINT_NATIVE_VALUE}.  The given <code>gValues</code> array must be of length greater than or equal to
    * {@link BrainLinkConstants#ACCELEROMETER_AXIS_COUNT}.  If the array is longer, only the first
    * {@link BrainLinkConstants#ACCELEROMETER_AXIS_COUNT} values are converted, so that the returned array is guaranteed
    * to be of length {@link BrainLinkConstants#ACCELEROMETER_AXIS_COUNT} (or <code>null</code>, as described above).
    */
   int[] convert(final double[] gValues)
      {
      if (gValues != null && gValues.length >= BrainLinkConstants.ACCELEROMETER_AXIS_COUNT)
         {
         final int[] values = new int[BrainLinkConstants.ACCELEROMETER_AXIS_COUNT];
         for (int i = 0; i < values.length; i++)
            {
            values[i] = convertToNative(gValues[i]);
            }
         return values;
         }
      return null;
      }

   private static int convertNativeToSixBit(final int val)
      {
      final int cleanedVal = Math.max(Math.min(val, MAX_NATIVE_VALUE), MIN_NATIVE_VALUE);
      if (cleanedVal <= MIDPOINT_NATIVE_VALUE)
         {
         return cleanedVal;
         }

      return cleanedVal - 64;
      }

   private static int convertSixBitToNative(final int val)
      {
      if (val < 0)
         {
         return val + 64;
         }

      return val;
      }

   double convertToGs(final int nativeValue)
      {
      final int sixBitValue = convertNativeToSixBit(nativeValue);
      final double g = sixBitValue * MULTIPLIER;

      if (g < MIN_G)
         {
         return MIN_G;
         }
      else if (g > MAX_G)
         {
         return MAX_G;
         }

      return g;
      }

   int convertToNative(final double gValue)
      {
      final double cleanedGVal = Math.max(Math.min(gValue, MAX_G), MIN_G);

      final int sixBitValue = (int)(cleanedGVal / MULTIPLIER);
      final int nativeValue = convertSixBitToNative(sixBitValue);

      if (nativeValue < MIN_NATIVE_VALUE)
         {
         return MIN_NATIVE_VALUE;
         }
      else if (nativeValue > MAX_NATIVE_VALUE)
         {
         return MAX_NATIVE_VALUE;
         }

      return nativeValue;
      }
   }
