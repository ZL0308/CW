package uk.ac.soton.comp1206.event;

/**
 * A listener aiming to avoid the conflict between the keyboard event and mouse event
 */
public interface LastBlockListener {

  /**
   * Handle the event when move the mouse
   */
  void clearLastBlock();
}
