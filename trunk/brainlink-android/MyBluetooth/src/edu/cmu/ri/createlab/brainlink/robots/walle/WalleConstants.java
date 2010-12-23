/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.ri.createlab.brainlink.robots.walle;

import edu.cmu.ri.createlab.brainlink.commands.SimpleIRCommandStrategy;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 *
 * @author BILL
 */
public final class WalleConstants {

    private static final byte[] INITIALIZATION_COMMAND = new byte[]{0x49, 0x03, 0x49, 0x02, 0x0B, 0x6D, 0x03, ByteUtils.intToUnsignedByte(0xB6), 0x03, 0x15, 0x02, ByteUtils.intToUnsignedByte(0xD5), 0x00, ByteUtils.intToUnsignedByte(0xB6), 0x03, 0x15, 0x02, ByteUtils.intToUnsignedByte(0xE1), 0x00, 0x00};

    public static byte[] getInitializationCommand() {
        return INITIALIZATION_COMMAND.clone();
    }

    private static final class Commands {

        private static final byte[] FORWARD = new byte[] {ByteUtils.intToUnsignedByte(0xAA),ByteUtils.intToUnsignedByte(0xEA),ByteUtils.intToUnsignedByte(0xE8)};
        private Commands() {
            // private to prevent instantiation
        }
    }

    static final class CommandStrategies {
        static final SimpleIRCommandStrategy NOTHING_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.FORWARD);
    }
}
