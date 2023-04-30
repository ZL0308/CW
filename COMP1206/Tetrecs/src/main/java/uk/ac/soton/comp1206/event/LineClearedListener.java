package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlockCoordinate;

import java.util.Set;

/**
 * A Line Cleared Listener is used to handle the event when clearing a line.
 */
public interface LineClearedListener {
  /**
   * Handle the event, add the fade out effect of the line which to be cleared.
   *
   * @param gameBlockCoordinate the coordinate the block which to be cleared.
   */
  void fadeOut(Set<GameBlockCoordinate> gameBlockCoordinate);

}
