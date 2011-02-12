package edu.cmu.ri.createlab.brainlink.robots;

import java.util.concurrent.Semaphore;
import edu.cmu.ri.createlab.brainlink.BrainLink;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */

public abstract class BaseBrainLinkControllable implements BrainLinkControllable
   {

     private BrainLink brainLink = null;

   public BaseBrainLinkControllable() {}
	   
   public final BrainLink getBrainLink()
      {
      return brainLink;
      }



   protected abstract void prepareForDisconnect();
   
}