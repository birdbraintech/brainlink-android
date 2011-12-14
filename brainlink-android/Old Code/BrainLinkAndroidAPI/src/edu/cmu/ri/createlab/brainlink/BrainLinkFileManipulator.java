package edu.cmu.ri.createlab.brainlink;

import edu.cmu.ri.createlab.util.ByteUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

/**
 * Class for reading and writing brainlink device files.
 * @author Tom Lauwers (tlauwers@birdbraintechnologies.com)
 * @author Modified by Huaishu Peng
 */
public class BrainLinkFileManipulator {

    private boolean updateData = false;
    private boolean isEncoded;
    private String fileLocation;
    private String fileDirectory = "/sdcard/BirdBrainTechnologies/BrainLink/devices/";

    HashMap<String, int[]> fileContents = new HashMap<String, int[]>();

    /**
     * Constructs a new file manipulator built on the file name specified. Create a new file or edit/read an existing file
     * with this constructor.
     * @param fileName The name of the file, without an extension
     * @param encoded True if the file is encoded, false if it is raw
     */
    public BrainLinkFileManipulator(String fileName, boolean encoded)
    {
        fileLocation = fileDirectory + fileName + (encoded?".encsig":".rawsig");
        File storefile = new File(fileLocation);
        isEncoded = encoded;
        if(storefile.exists()) {
            readFileIntoMap();
        }
    }

    /**
     * An empty constructor, can be used to get a list of all the files in the current file directory.
     */
    public BrainLinkFileManipulator()
    {
    }

    /**
     * Returns a list of the encoded files in the current file directory
     * @return an array of Strings holding the file names
     */
    public String[] getEncodedFileList() {
        File dir = new File(fileDirectory);

        // Filter files so it only returns ones ending with .encsig, the encoded file extension
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
            return name.endsWith(".encsig");
            }
        };

        String[] children = dir.list(filter);
        return children;
    }

    /**
     * Returns a list of the raw files in the current file directory
     * @return an array of Strings holding the file names
     */
     public String[] getRawFileList() {
        File dir = new File(fileDirectory);

         // Filter files so it only returns ones ending with .rawsig, the raw file extension
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
            return name.endsWith(".rawsig");
            }
        };
        String[] children = dir.list(filter);
        return children;
    }

    /**
     * Returns if the file specified in the constructor is empty or new. True if so, false otherwise.
     * @return True if file is empty, false otherwise
     */
    public boolean isEmpty() {
        return fileContents.isEmpty();
    }

    /**
     * Returns true if the file being manipulated is encoded
     * @return True if file is encoded, false if file is raw
     */
    public boolean isEncoded() {
        return isEncoded;
    }

    /**
     * Checks if the signal exists in the file.
     * @param signalName Name of the signal to check
     * @return True if the signal exists, false otherwise
     */
    public boolean containsSignal(String signalName) {
        return fileContents.containsKey(signalName);
    }

    /**
     * Stores an encoded signal in the file specified by the constructor. If the signal already exists in the file,
     * calling this method will over-write that signal's values with the new ones passed through the method.
     * @param signalName The name of the signal
     * @param values The encoded signal's values
     * @param repeatTime If the signal should repeat automatically (send 0 if not). Range of this value is 0 to 132 and it is specified in milliseconds.
     * @return True if the signal data was correctly formatted and storing succeeded, false otherwise.
     */
    public boolean storeEncodedSignal(String signalName, int[] values, int repeatTime)
    {
        if(!isEncoded) {
            return false;
        }
        try {
            File storefile = new File(fileLocation);
            // Make it so people must add initialization data first
            if(!storefile.exists()) {
                System.out.println("File doesn't exist, provide initialization data first.");
                return false;
            }
            // Make sure repeat time is in range
            if(repeatTime < 0 || repeatTime > 132) {
                System.out.println("Repeat time is not in valid range of 0 to 132.");
                return false;
            }
            // If the signal is already in the file, remove it and over-write it
            if(fileContents.containsKey(signalName)) {
                removeLineFromFile(fileLocation, signalName);
            }

            // Open the file for writing
            FileOutputStream fileout = new FileOutputStream(fileLocation, true);
            Writer out = new OutputStreamWriter(fileout, "UTF-8");
            // Use ; as delimited in the file
            String signalCommands = signalName+";";
            for(int i = 0; i < values.length; i++) {
                signalCommands +=  ByteUtils.intToUnsignedByte(values[i]) + ";";
            }
            // Convert repeat time from milliseconds to format used by Brainlink (2 microseconds, and 8-bit storage)
            signalCommands += getHighByteFromInt(repeatTime*500) + ";" + getLowByteFromInt(repeatTime*500);

            out.append(signalCommands+"\n");
            out.close();
            fileout.close();
            // Make sure that get commands do an update from file before returning data
            updateData = true;
            return true;
        }
        catch(IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Stores a raw signal in the file specified by the constructor. If the signal already exists in the file,
     * calling this method will over-write that signal's values with the new ones passed through the method.
     * @param signalName The name of the signal
     * @param values The raw signal's values
     * @param repeatTime If the signal should repeat automatically (send 0 if not). Range of this value is 0 to 132 and it is specified in milliseconds.
     * @return True if the signal data was correctly formatted and storing succeeded, false otherwise.
     */
    public boolean storeRawSignal(String signalName, int[] values, int repeatTime)
    {
        if(isEncoded) {
            return false;
        }
        try {
            if(repeatTime > 132 || repeatTime < 0) {
                System.out.println("Repeat time is not in valid range of 0 to 132.");
                return false;
            }
            if(fileContents.containsKey(signalName)) {
                removeLineFromFile(fileLocation, signalName);
            }
            FileOutputStream fileout = new FileOutputStream(fileLocation, true);
            Writer out = new OutputStreamWriter(fileout, "UTF-8");
            out.append(signalName);
            out.append(";"+values.length);
            for(int i = 0; i < values.length; i++) {
                out.append(";"+values[i]);
            }
            out.append(";" + repeatTime);
            out.append("\n");
            out.close();
            fileout.close();
            // Make sure that get commands do an update from file before returning data
            updateData = true;
            return true;
        }
        catch(IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Stores the initialization data for an encoded file. This data is used by Brainlink to properly mimic the remote control
     * of the device.
     * @param carrierFreq The frequency, in Hertz, of the IR carrier. Typically this is 38000.
     * @param startUpPulses An array containing the length of the pulses making up the start up sequence. Specified in microseconds.
     * @param numBits The number of bits of information in a signal.
     * @param logicalOneTime The time (in microseconds) the signal goes low or high to represent a logical one.
     * @param logicalZeroTime The time (in microseconds) the signal goes low or high to represent a logical zero.
     * @param bitSpacing  The time (in microseconds) between bits. 0 if this is an alternating or irobot style encoding.
     * @param encoding The way bits are encoded type. Should be either "alternating", "uptime", "downtime", or "irobot".
     * @return True if data was correctly formatted and successfully stored, false otherwise.
     */
    public boolean storeInitialization(int carrierFreq, int[] startUpPulses, int numBits, int logicalOneTime, int logicalZeroTime, int bitSpacing, String encoding)
    {
        try {
            File storefile = new File(fileLocation);
            if(storefile.exists()) {
                removeLineFromFile(fileLocation, "Initialization;");
            }
            FileOutputStream fileout = new FileOutputStream(fileLocation, true);
            Writer out = new OutputStreamWriter(fileout, "UTF-8");
            String initCommands = "Initialization;";
            // Start by giving it the values for the carrier
            int tempCarrier = 32000000/carrierFreq;
            initCommands += getHighByteFromInt(tempCarrier) + ";" + getLowByteFromInt(tempCarrier) + ";";
            // Add the start up pulse sequence
            if(startUpPulses == null) {
                initCommands += 0 + ";";
            }
            else {
                initCommands += startUpPulses.length + ";";
                for(int i = 0; i < startUpPulses.length; i++) {
                    initCommands +=  getHighByteFromInt(startUpPulses[i]/2) + ";" + getLowByteFromInt(startUpPulses[i]/2) + ";";
                }
            }
            // Add encoding type
            if(encoding.equals("alternating")) {
                initCommands += "0;";
            }
            else if(encoding.equals("uptime")) {
                initCommands += "1;";
            }
            else if(encoding.equals("downtime")) {
                initCommands += "2;";
            }
            else if(encoding.equals("irobot")) {
                initCommands += "3;";
            }
            else {
                System.out.println("Invalid encoding style, initialization not written to file");
                return false;
            }
            // Add number of bits, logical one, zero, and bit-spacing time
            initCommands += numBits + ";";
            initCommands += getHighByteFromInt(logicalOneTime/2) + ";" + getLowByteFromInt(logicalOneTime/2) + ";";
            initCommands += getHighByteFromInt(logicalZeroTime/2) + ";" + getLowByteFromInt(logicalZeroTime/2) + ";";
            initCommands += getHighByteFromInt(bitSpacing/2) + ";" + getLowByteFromInt(bitSpacing/2);

            out.append(initCommands+"\n");
            out.close();
            fileout.close();
            // Make sure that get commands do an update from file before returning data
            updateData = true;
            return true;
         }
        catch (FileNotFoundException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Sets the location to look for device data files
     * @param location The location to look for files in. Can be either a relative or absolute path.
     */
    public void setFileDirectory(String location)
    {
        fileDirectory = location;
    }

    /**
     * Returns the location of the file currently being read.
     * @return The location of the file
     */
    public String getFileLocation()
    {
        return fileLocation;
    }

    /**
     * Quick way to get all of the signal names in a file.
     * @return an array of Strings containing the names of every signal in the file. Null if the file is empty.
     */
    public String[] getSignalNames()
    {
         if(updateData) {
             readFileIntoMap();
             updateData = false;
         }
         if(fileContents.isEmpty()) {
             System.out.println("Warning, file is empty");
             return null;
         }
         String [] ret = new String[fileContents.size()];
         fileContents.keySet().toArray(ret);
         
         return ret;
         
    }

    /**
     * Gets the initialization data in the encoded file formatted to send directly to Brainlink
     * Returns the initialization data, formatted for direct transmission to Brainlink.
     * @return an array of bytes containing this file's initialization data
     */
    public byte[] getInitialization()
    {
        if(!isEncoded) {
            return null;
        }
        if(updateData) {
             readFileIntoMap();
             updateData = false;
        }
        if(fileContents.isEmpty()) {
             System.out.println("Warning, file is empty");
             return null;
        }
        int[] initData = fileContents.get("Initialization");
        byte [] returnData = new byte[initData.length];
        for(int i = 0; i < initData.length; i++) {
            returnData[i] =  (byte)initData[i];
        }
        return returnData;

    }

    /**
     * Quick way to get the initialization data in the encoded file.
     * @return An array of Strings holding the initialization data in human-friendly format. The array is formatted as follows:<br>
     *   0 - Carrier frequency  <br>
     *   1 - Signal encoding type <br>
     *   2 - Number of bits in a signal <br>
     *   3 - Logical one time in microseconds <br>
     *   4 - Logical zero time in microseconds  <br>
     *   5 - Bit spacing time in microseconds  <br>
     *   6 to n - The start up pulse sequence, each element specified in microseconds

     */
    public String[] getHumanReadableInitialization()
    {
        if(updateData) {
             readFileIntoMap();
             updateData = false;
        }
        if(fileContents.isEmpty()) {
             System.out.println("Warning, file is empty");
             return null;
        }
        int[] initData = fileContents.get("Initialization");
        if(initData == null)
        {
            System.out.println("Warning, file has no initialization. Maybe it's raw instead of encoded?");
            return null;
        }
        String [] returnData = new String[(initData.length+1)/2];

        returnData[0] = Integer.toString((32000000/(initData[0] * 256 + initData[1])));
        int startUpPulseLength = initData[2]*2;
        switch(initData[3+startUpPulseLength])
        {
             case 0:
                 returnData[1] = "alternating";
                 break;
             case 1:
                 returnData[1] = "uptime";
                 break;
             case 2:
                 returnData[1] = "downtime";
                 break;
             case 3:
                 returnData[1] = "irobot";
                 break;
             default:
                 returnData[1] = "invalid";
                 break;
        }
        returnData[2] = Integer.toString(initData[4+startUpPulseLength]);
        returnData[3] = Integer.toString((initData[5+startUpPulseLength] * 256 + initData[6+startUpPulseLength])*2);
        returnData[4] = Integer.toString((initData[7+startUpPulseLength] * 256 + initData[8+startUpPulseLength])*2);
        returnData[5] = Integer.toString((initData[9+startUpPulseLength] * 256 + initData[10+startUpPulseLength])*2);
        for(int i = 0; i < initData[2]; i++) {
            returnData[i+6] = Integer.toString((initData[3+i*2] * 256 + initData[4+i*2])*2);
        }
        return returnData;
    }

    /**
     * Gets the values of either an encoded or raw signal.
     * @param signalName The signal to get values for
     * @return The signal value data, either raw or encoded (dependent on whether constructor specified an encoded or raw file)
     */
    public int[] getSignalValues(String signalName)
    {
        if(updateData) {
            readFileIntoMap();
            updateData = false;
        }
        if(fileContents.isEmpty()) {
            System.out.println("Warning, file is empty");
            return null;
        }
        if(fileContents.containsKey(signalName)) {
            int[] signalValuesWithRepeat = fileContents.get(signalName);
            if(isEncoded) {
                int[] signalValues = new int[signalValuesWithRepeat.length-2];
                System.arraycopy(signalValuesWithRepeat, 0, signalValues, 0, signalValuesWithRepeat.length-2);
                return signalValues;
            }
            else {
                int[] signalValues = new int[signalValuesWithRepeat.length-1];
                System.arraycopy(signalValuesWithRepeat, 0, signalValues, 0, signalValuesWithRepeat.length-1);
                return signalValues;
            }
        }
        else {
             System.out.println("Warning, signal name doesn't exist in file");
             return null;
         }

    }

    /**
     * Gets the repeat time in milliseconds of this signal
     * @param signalName The signal to get the repeat time for
     * @return The signal's repeat time in milliseconds, 0 if none is specified
     */
    public Integer getSignalRepeatTime(String signalName)
    {
         if(updateData) {
             readFileIntoMap();
             updateData = false;
         }
          if(fileContents.isEmpty()) {
             System.out.println("Warning, file is empty");
             return null;
         }
        if(isEncoded) {
            int[] signalValues = fileContents.get(signalName);
            return ((signalValues[signalValues.length-2]*256 + signalValues[signalValues.length-1])/500);
        }
        else {
            int[] signalValues = fileContents.get(signalName);
            return signalValues[signalValues.length-1];

        }
    }

    /**
     * Returns the current file directory
     * @return The current directory for encoded/raw files
     */
    public String getFileDirectory()
    {
        return fileDirectory;
    }

    /**
     * Reads the entire file into a hashmap used by the get methods.
     * @return True if file was successfully read
     */
    private boolean readFileIntoMap()
    {
        fileContents.clear();

        try {
           // Open the file for reading
           Scanner scanner = new Scanner(new FileInputStream(fileLocation), "UTF-8");
           scanner.useDelimiter(";");

           String temp;   // Temporarily holds a line of the file for parsing
           String signalName;  // Holds the name of the signal
           int semiColonIndex;  // Signal names are always between the new line and first semi-colon
           while(scanner.hasNext()) {
              temp = scanner.nextLine();
              semiColonIndex = temp.indexOf(";");
              if(semiColonIndex > 0) {
                  signalName = temp.substring(0, semiColonIndex);
                   // Get just the signals
                  temp = temp.substring(semiColonIndex+1);
                   // Split into a whole bunch of strings, each representing a signal
                  String[] signalValues = temp.split(";");
                  int[] signalValuesAsInts = new int[signalValues.length];
                   for(int i = 0; i < signalValues.length; i++) {
                       signalValuesAsInts[i] = Integer.valueOf(signalValues[i]);
                       if(signalValuesAsInts[i] < 0) {
                            signalValuesAsInts[i] += 256;
                       }
                   }
                   fileContents.put(signalName, signalValuesAsInts);
              }
           }
           scanner.close();
           return true;
           
        }
        catch (FileNotFoundException e) {
            System.out.println("readFile Warning: No file by that name");
            return false;
        }
        catch(IOException e) {
            System.out.println("readFile Warning: Failed to read, generic IO Exception");
            return false;
        }
    }

    /**
     * Removes a line from the file, used to over-write initialization or signal data in an existing file
     * @param file The location of the file
     * @param lineToRemove The starting string of the line to remove
     */
    private void removeLineFromFile(String file, String lineToRemove) {

       try {

         File inFile = new File(file);

         if (!inFile.isFile()) {
           System.out.println("Parameter is not an existing file");
           return;
         }

         //Construct the new file that will later be renamed to the original filename.
         File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

         BufferedReader br = new BufferedReader(new FileReader(file));
         PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

         String line = null;

         //Read from the original file and write to the new
         //unless content matches data to be removed.
         while ((line = br.readLine()) != null) {

           if (!line.startsWith(lineToRemove)) {

             pw.println(line);
             pw.flush();
           }
         }
         pw.close();
         br.close();

         //Delete the original file
         if (!inFile.delete()) {
           System.out.println("Could not delete file");
           return;
         }

         //Rename the new file to the filename the original file had.
         if (!tempFile.renameTo(inFile))
           System.out.println("Could not rename file");

       }
       catch (FileNotFoundException ex) {
         ex.printStackTrace();
       }
       catch (IOException ex) {
         ex.printStackTrace();
       }
     }

       private byte getHighByteFromInt(final int val)
         {
         return (byte)((val << 16) >> 24);
         }

      private byte getLowByteFromInt(final int val)
         {
         return (byte)((val << 24) >> 24);
         }


}
