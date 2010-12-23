/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.ri.createlab.brainlink.commands;

/**
 *
 * @author BILL
 */
public class SimpleIRCommandStrategy {
   /** The command character used to send an IR command. */
   private static final byte COMMAND_PREFIX = 'i';

   private final byte[] command;

   public SimpleIRCommandStrategy(final byte command)
      {
      this.command = new byte[]{COMMAND_PREFIX, command, 0x0, 0x0};
      }

   public SimpleIRCommandStrategy(final byte[] command)
    {
       this.command = new byte[] {COMMAND_PREFIX, command[0],command[1],command[2], 0x0, 0x0};
   }
   protected byte[] getCommand()
      {
      return command.clone();
      }
}
