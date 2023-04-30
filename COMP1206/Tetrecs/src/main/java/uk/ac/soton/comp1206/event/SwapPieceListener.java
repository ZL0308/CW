package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;

/**
 * A Swap Piece Listener is used to handle the event when a piece in piece board is swapped
 */
public interface SwapPieceListener {

  /**
   * Handle the swap piece event
   */
  void swapPiece();
}
