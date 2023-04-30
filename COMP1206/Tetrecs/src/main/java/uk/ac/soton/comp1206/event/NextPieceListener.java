package uk.ac.soton.comp1206.event;

/**
 * The CurrentPiece Listener is used for receiving new piece which created in Game.
 */
public interface NextPieceListener {
  /**
   * Handle the incoming gamePiece created by game.
   */
  void nextPiece();
}
