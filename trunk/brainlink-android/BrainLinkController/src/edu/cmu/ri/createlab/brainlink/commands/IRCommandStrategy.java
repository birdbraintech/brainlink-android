package edu.cmu.ri.createlab.brainlink.commands;

//import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class IRCommandStrategy
   {
   /** The command character used to send an IR command. */
   private static final byte COMMAND_PREFIX = 'i';

   private final byte[] command;

   public IRCommandStrategy(final byte[] commandBytes, final byte repeatCommandByte1, final byte repeatCommandByte2)
      {
      this.command = new byte[commandBytes.length + 3];
      this.command[0] = COMMAND_PREFIX;
      System.arraycopy(commandBytes, 0, this.command, 1, commandBytes.length);
      this.command[this.command.length - 2] = repeatCommandByte1;
      this.command[this.command.length - 1] = repeatCommandByte2;
      }

//   @Override
   protected byte[] getCommand()
      {
      return command.clone();
      }
   }