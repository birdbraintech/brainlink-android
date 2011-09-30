package edu.cmu.ri.createlab.brainlink.robots;

import edu.cmu.ri.createlab.brainlink.BrainLink;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface BrainLinkControllable
   {
   BrainLink getBrainLink();

   void disconnect();
   }