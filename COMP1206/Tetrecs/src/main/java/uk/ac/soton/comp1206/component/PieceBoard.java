package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * A Piece Board to display the current piece and the following piece
 */
public class PieceBoard extends GameBoard {

  /**
   * A constructor to initialize the piece board
   *
   * @param cols   the column of block
   * @param rows   the row of the block
   * @param width  the width of the canvas
   * @param height the height of the canvas
   */
  public PieceBoard(int cols, int rows, double width, double height) {
    super(cols, rows, width, height);
  }

  /**
   * Add the piece into the piece board.
   *
   * @param gamePiece the game piece to be added
   */
  public void setPiece(GamePiece gamePiece) {

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        grid.set(i, j, 0);  // clear whole the piece board
      }
    }
    grid.playPiece(gamePiece, 1, 1);  // set the piece into the piece board
  }

  /**
   * Set the indicator in the middle block of the board
   */
  public void setIndicator() {
    blocks[1][1].setIfSet();
  }
}
