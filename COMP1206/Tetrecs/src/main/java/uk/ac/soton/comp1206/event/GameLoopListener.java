package uk.ac.soton.comp1206.event;

/**
 * The Game Loop Listener is used to handle the event when a game loop is started.
 */
public interface GameLoopListener {

  /**
   * Handle the game start event, and start the time bar.
   */
  void startTimerBar();

}
