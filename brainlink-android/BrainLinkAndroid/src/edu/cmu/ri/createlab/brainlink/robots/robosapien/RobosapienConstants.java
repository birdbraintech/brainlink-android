package edu.cmu.ri.createlab.brainlink.robots.robosapien;

import edu.cmu.ri.createlab.brainlink.commands.SimpleIRCommandStrategy;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class RobosapienConstants
   {
   private static final byte[] INITIALIZATION_COMMAND = new byte[]{0x03, 0x49, 0x01, 0x0D, 0x05, 0x02, 0x08, 0x06, ByteUtils.intToUnsignedByte(0x83), 0x01, ByteUtils.intToUnsignedByte(0xA0), 0x01, ByteUtils.intToUnsignedByte(0xA0)};

   static byte[] getInitializationCommand()
      {
      return INITIALIZATION_COMMAND.clone();
      }

   private static final class Commands
      {
      private static final byte LEFT_ARM_UP = ByteUtils.intToUnsignedByte(0x89);
      private static final byte LEFT_ARM_DOWN = ByteUtils.intToUnsignedByte(0x8C);
      private static final byte LEFT_ARM_IN = ByteUtils.intToUnsignedByte(0x8D);
      private static final byte LEFT_ARM_OUT = ByteUtils.intToUnsignedByte(0x8A);

      private static final byte LEFT_HAND_THUMP = ByteUtils.intToUnsignedByte(0xA9);
      private static final byte LEFT_HAND_THROW = ByteUtils.intToUnsignedByte(0xAA);
      private static final byte LEFT_HAND_PICKUP = ByteUtils.intToUnsignedByte(0xAC);
      private static final byte LEFT_HAND_STRIKE_1 = ByteUtils.intToUnsignedByte(0xCD);
      private static final byte LEFT_HAND_STRIKE_2 = ByteUtils.intToUnsignedByte(0xCB);
      private static final byte LEFT_HAND_STRIKE_3 = ByteUtils.intToUnsignedByte(0xC8);
      private static final byte LEFT_HAND_SWEEP = ByteUtils.intToUnsignedByte(0xC9);

      private static final byte RIGHT_ARM_UP = ByteUtils.intToUnsignedByte(0x81);
      private static final byte RIGHT_ARM_DOWN = ByteUtils.intToUnsignedByte(0x84);
      private static final byte RIGHT_ARM_IN = ByteUtils.intToUnsignedByte(0x85);
      private static final byte RIGHT_ARM_OUT = ByteUtils.intToUnsignedByte(0x82);

      private static final byte RIGHT_HAND_THUMP = ByteUtils.intToUnsignedByte(0xA1);
      private static final byte RIGHT_HAND_THROW = ByteUtils.intToUnsignedByte(0xA2);
      private static final byte RIGHT_HAND_PICKUP = ByteUtils.intToUnsignedByte(0xA4);
      private static final byte RIGHT_HAND_STRIKE_1 = ByteUtils.intToUnsignedByte(0xC5);
      private static final byte RIGHT_HAND_STRIKE_2 = ByteUtils.intToUnsignedByte(0xC3);
      private static final byte RIGHT_HAND_STRIKE_3 = ByteUtils.intToUnsignedByte(0xC0);
      private static final byte RIGHT_HAND_SWEEP = ByteUtils.intToUnsignedByte(0xC1);

      private static final byte TURN_LEFT = ByteUtils.intToUnsignedByte(0x88);
      private static final byte TURN_RIGHT = ByteUtils.intToUnsignedByte(0x80);

      private static final byte TILT_BODY_LEFT = ByteUtils.intToUnsignedByte(0x8B);
      private static final byte TILT_BODY_RIGHT = ByteUtils.intToUnsignedByte(0x83);
      private static final byte LEAN_FORWARD = ByteUtils.intToUnsignedByte(0xAD);
      private static final byte LEAN_BACKWARD = ByteUtils.intToUnsignedByte(0xA5);

      private static final byte LEFT_TURN_STEP = ByteUtils.intToUnsignedByte(0xA8);
      private static final byte RIGHT_TURN_STEP = ByteUtils.intToUnsignedByte(0xA0);
      private static final byte FORWARD_STEP = ByteUtils.intToUnsignedByte(0xA6);
      private static final byte BACKWARD_STEP = ByteUtils.intToUnsignedByte(0xA7);

      private static final byte WALK_FORWARD = ByteUtils.intToUnsignedByte(0x86);
      private static final byte WALK_BACKWARD = ByteUtils.intToUnsignedByte(0x87);

      private static final byte STOP = ByteUtils.intToUnsignedByte(0x8E);

      private static final byte HIGH_FIVE = ByteUtils.intToUnsignedByte(0xC4);
      private static final byte BULLDOZER = ByteUtils.intToUnsignedByte(0xC6);
      private static final byte FEET_SHUFFLE = ByteUtils.intToUnsignedByte(0XF6);
      private static final byte RAISE_ARM_THROW = ByteUtils.intToUnsignedByte(0XFC);
      private static final byte KARATE_CHOP = ByteUtils.intToUnsignedByte(0XD6);

      private static final byte BURP = ByteUtils.intToUnsignedByte(0xC2);
      private static final byte ROAR = ByteUtils.intToUnsignedByte(0xCE);
      private static final byte OOPS = ByteUtils.intToUnsignedByte(0xC7);
      private static final byte WHISTLE = ByteUtils.intToUnsignedByte(0xCA);
      private static final byte TALKBACK = ByteUtils.intToUnsignedByte(0xCC);

      private static final byte SLEEP = ByteUtils.intToUnsignedByte(0xA3);
      private static final byte WAKEUP = ByteUtils.intToUnsignedByte(0xB1);

      private static final byte LISTEN = ByteUtils.intToUnsignedByte(0xAB);
      private static final byte RESET = ByteUtils.intToUnsignedByte(0xAE);

      private static final byte ALL_DEMO = ByteUtils.intToUnsignedByte(0xD0);
      private static final byte KARATE_DEMO = ByteUtils.intToUnsignedByte(0xD2);
      private static final byte RUDE_DEMO = ByteUtils.intToUnsignedByte(0xD3);
      private static final byte DANCE = ByteUtils.intToUnsignedByte(0xD4);

      private static final byte NOTHING = ByteUtils.intToUnsignedByte(0XFB);

      private Commands()
         {
         // private to prevent instantiation
         }
      }

   static final class CommandStrategies
      {
      static final SimpleIRCommandStrategy LEFT_ARM_UP_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.LEFT_ARM_UP);
      static final SimpleIRCommandStrategy LEFT_ARM_DOWN_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.LEFT_ARM_DOWN);
      static final SimpleIRCommandStrategy LEFT_ARM_IN_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.LEFT_ARM_IN);
      static final SimpleIRCommandStrategy LEFT_ARM_OUT_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.LEFT_ARM_OUT);

      static final SimpleIRCommandStrategy LEFT_HAND_THUMP_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.LEFT_HAND_THUMP);
      static final SimpleIRCommandStrategy LEFT_HAND_THROW_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.LEFT_HAND_THROW);
      static final SimpleIRCommandStrategy LEFT_HAND_PICKUP_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.LEFT_HAND_PICKUP);
      static final SimpleIRCommandStrategy LEFT_HAND_STRIKE_1_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.LEFT_HAND_STRIKE_1);
      static final SimpleIRCommandStrategy LEFT_HAND_STRIKE_2_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.LEFT_HAND_STRIKE_2);
      static final SimpleIRCommandStrategy LEFT_HAND_STRIKE_3_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.LEFT_HAND_STRIKE_3);
      static final SimpleIRCommandStrategy LEFT_HAND_SWEEP_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.LEFT_HAND_SWEEP);

      static final SimpleIRCommandStrategy RIGHT_ARM_UP_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.RIGHT_ARM_UP);
      static final SimpleIRCommandStrategy RIGHT_ARM_DOWN_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.RIGHT_ARM_DOWN);
      static final SimpleIRCommandStrategy RIGHT_ARM_IN_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.RIGHT_ARM_IN);
      static final SimpleIRCommandStrategy RIGHT_ARM_OUT_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.RIGHT_ARM_OUT);

      static final SimpleIRCommandStrategy RIGHT_HAND_THUMP_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.RIGHT_HAND_THUMP);
      static final SimpleIRCommandStrategy RIGHT_HAND_THROW_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.RIGHT_HAND_THROW);
      static final SimpleIRCommandStrategy RIGHT_HAND_PICKUP_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.RIGHT_HAND_PICKUP);
      static final SimpleIRCommandStrategy RIGHT_HAND_STRIKE_1_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.RIGHT_HAND_STRIKE_1);
      static final SimpleIRCommandStrategy RIGHT_HAND_STRIKE_2_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.RIGHT_HAND_STRIKE_2);
      static final SimpleIRCommandStrategy RIGHT_HAND_STRIKE_3_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.RIGHT_HAND_STRIKE_3);
      static final SimpleIRCommandStrategy RIGHT_HAND_SWEEP_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.RIGHT_HAND_SWEEP);

      static final SimpleIRCommandStrategy TURN_LEFT_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.TURN_LEFT);
      static final SimpleIRCommandStrategy TURN_RIGHT_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.TURN_RIGHT);

      static final SimpleIRCommandStrategy TILT_BODY_LEFT_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.TILT_BODY_LEFT);
      static final SimpleIRCommandStrategy TILT_BODY_RIGHT_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.TILT_BODY_RIGHT);
      static final SimpleIRCommandStrategy LEAN_FORWARD_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.LEAN_FORWARD);
      static final SimpleIRCommandStrategy LEAN_BACKWARD_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.LEAN_BACKWARD);

      static final SimpleIRCommandStrategy LEFT_TURN_STEP_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.LEFT_TURN_STEP);
      static final SimpleIRCommandStrategy RIGHT_TURN_STEP_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.RIGHT_TURN_STEP);
      static final SimpleIRCommandStrategy FORWARD_STEP_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.FORWARD_STEP);
      static final SimpleIRCommandStrategy BACKWARD_STEP_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.BACKWARD_STEP);

      static final SimpleIRCommandStrategy WALK_FORWARD_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.WALK_FORWARD);
      static final SimpleIRCommandStrategy WALK_BACKWARD_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.WALK_BACKWARD);

      static final SimpleIRCommandStrategy STOP_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.STOP);

      static final SimpleIRCommandStrategy HIGH_FIVE_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.HIGH_FIVE);
      static final SimpleIRCommandStrategy BULLDOZER_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.BULLDOZER);
      static final SimpleIRCommandStrategy FEET_SHUFFLE_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.FEET_SHUFFLE);
      static final SimpleIRCommandStrategy RAISE_ARM_THROW_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.RAISE_ARM_THROW);
      static final SimpleIRCommandStrategy KARATE_CHOP_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.KARATE_CHOP);

      static final SimpleIRCommandStrategy BURP_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.BURP);
      static final SimpleIRCommandStrategy ROAR_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.ROAR);
      static final SimpleIRCommandStrategy OOPS_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.OOPS);
      static final SimpleIRCommandStrategy WHISTLE_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.WHISTLE);
      static final SimpleIRCommandStrategy TALKBACK_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.TALKBACK);

      static final SimpleIRCommandStrategy SLEEP_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.SLEEP);
      static final SimpleIRCommandStrategy WAKEUP_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.WAKEUP);

      static final SimpleIRCommandStrategy LISTEN_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.LISTEN);
      static final SimpleIRCommandStrategy RESET_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.RESET);

      static final SimpleIRCommandStrategy ALL_DEMO_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.ALL_DEMO);
      static final SimpleIRCommandStrategy KARATE_DEMO_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.KARATE_DEMO);
      static final SimpleIRCommandStrategy RUDE_DEMO_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.RUDE_DEMO);
      static final SimpleIRCommandStrategy DANCE_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.DANCE);

      static final SimpleIRCommandStrategy NOTHING_COMMAND_STRATEGY = new SimpleIRCommandStrategy(Commands.NOTHING);

      private CommandStrategies()
         {
         // private to prevent instantiation
         }
      }

   public RobosapienConstants()
      {
      // private to prevent instantiation
      }
   }
