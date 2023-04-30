package uk.ac.soton.comp1206.event;

/**
 * A High Score Listener is used to handle the event when the current is the highest score
 */
public interface HighScoreListener {

  /**
   * Handle the event when the current score is the highest score around the local score
   */
  void updateScore();

}
