package uk.ac.soton.comp1206.component;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.event.LastBlockListener;
import uk.ac.soton.comp1206.event.RightClickedListener;
import uk.ac.soton.comp1206.event.SwapPieceListener;
import uk.ac.soton.comp1206.game.Grid;

import java.util.Set;

/**
 * A GameBoard is a visual component to represent the visual GameBoard.
 * It extends a GridPane to hold a grid of GameBlocks.
 * <p>
 * The GameBoard can hold an internal grid of it's own, for example, for displaying an upcoming block. It also be
 * linked to an external grid, for the main game board.
 * <p>
 * The GameBoard is only a visual representation and should not contain game logic or model logic in it, which should
 * take place in the Grid.
 */
public class GameBoard extends GridPane {

  /**
   * A logger to log messages and events that occur during the execution of an application.
   */
  private static final Logger logger = LogManager.getLogger(GameBoard.class);

  /**
   * Number of columns in the board
   */
  private final int cols;

  /**
   * Number of rows in the board
   */
  private final int rows;

  /**
   * The visual width of the board - has to be specified due to being a Canvas
   */
  private final double width;

  /**
   * The visual height of the board - has to be specified due to being a Canvas
   */
  private final double height;

  /**
   * The grid this GameBoard represents
   */
  final Grid grid;

  /**
   * The blocks inside the grid
   */
  GameBlock[][] blocks;

  /**
   * The listener to call when a specific block is clicked
   */
  private BlockClickedListener blockClickedListener;

  /**
   * The listener to call when a specific is right-clicked
   */
  private RightClickedListener rightClickedListener;

  /**
   * The listener to call when swapping
   */
  private SwapPieceListener swapPieceListener;

  /**
   * Track the coordinate after moving by the keyboard
   */
  private int x;

  /**
   * Track the coordinate after moving by the keyboard
   */
  private int y;

  /**
   * Create a new GameBoard, based off a given grid, with a visual width and height.
   *
   * @param grid   linked grid
   * @param width  the visual width
   * @param height the visual height
   */
  public GameBoard(Grid grid, double width, double height) {
    this.cols = grid.getCols();
    this.rows = grid.getRows();
    this.width = width;
    this.height = height;
    this.grid = grid;

    //Build the GameBoard
    build();
  }

  /**
   * Create a new GameBoard with it's own internal grid, specifying the number of columns and rows, along with the
   * visual width and height.
   *
   * @param cols   number of columns for internal grid
   * @param rows   number of rows for internal grid
   * @param width  the visual width
   * @param height the visual height
   */
  public GameBoard(int cols, int rows, double width, double height) {
    this.cols = cols;
    this.rows = rows;
    this.width = width;
    this.height = height;
    this.grid = new Grid(cols, rows);

    //Build the GameBoard
    build();
  }

  /**
   * Get a specific block from the GameBoard, specified by it's row and column
   *
   * @param x column
   * @param y row
   * @return game block at the given column and row
   */
  public GameBlock getBlock(int x, int y) {
    return blocks[x][y];
  }

  /**
   * Build the GameBoard by creating a block at every x and y column and row
   */
  protected void build() {
    logger.info("Building grid: {} x {}", cols, rows);

    setMaxWidth(width);
    setMaxHeight(height);

    setGridLinesVisible(true);

    blocks = new GameBlock[cols][rows];

    for (var y = 0; y < rows; y++) {
      for (var x = 0; x < cols; x++) {
        createBlock(x, y);
        blocks[x][y].setLastBlockListener(this::clearLastBlock); // register listener to each blocks
      }
    }
  }

  /**
   * Create a block at the given x and y position in the GameBoard
   *
   * @param x column
   * @param y row
   */
  protected void createBlock(int x, int y) {
    var blockWidth = width / cols;
    var blockHeight = height / rows;

    //Create a new GameBlock UI component
    GameBlock block = new GameBlock(this, x, y, blockWidth, blockHeight);

    //Add to the GridPane
    add(block, x, y);

    //Add to our block directory
    blocks[x][y] = block;

    //Link the GameBlock component to the corresponding value in the Grid
    block.bind(grid.getGridProperty(x, y));

    //Add a mouse click handler to the block to trigger GameBoard blockClicked method
    block.setOnMouseClicked((e) -> {
      if (e.getButton() == MouseButton.PRIMARY) {
        if (grid.getRows() == 3 && width == 150) {   // iff left-clicked the first piece board, rotate
          rightClicked(e);                           // rotate the piece
        } else if (grid.getRows() == 3 && width == 80) {
          swapPiece(e);                              // iff left-clicked the second piece board, swap
        }
        blockClicked(e, block);
      } else if (e.getButton() == MouseButton.SECONDARY && width != 80 && width != 150) {
        rightClicked(e);        // iff right-clicked the game block, rotates
      }
    });
  }

  /**
   * Set the listener to handle an event when a block is clicked
   *
   * @param listener listener to add
   */
  public void setOnBlockClick(BlockClickedListener listener) {
    this.blockClickedListener = listener;
  }

  /**
   * Triggered when a block is clicked. Call the attached listener.
   *
   * @param event mouse event
   * @param block block clicked on
   */
  private void blockClicked(MouseEvent event, GameBlock block) {
    logger.info("Block clicked: {}", block);

    if (blockClickedListener != null) {
      blockClickedListener.blockClicked(block);
    }
  }

  /**
   * Set the listener to handle an event when a block is right-clicked
   *
   * @param listener listener to add
   */
  public void setOnRightClicked(RightClickedListener listener) {
    this.rightClickedListener = listener;
  }

  /**
   * Triggered when a block is right-clicked. Call the attached listener
   *
   * @param event the mouse event
   */
  private void rightClicked(MouseEvent event) {
    logger.info("Rotate the current piece");

    if (rightClickedListener != null) {
      rightClickedListener.rightClicked();
    }
  }

  /**
   * Set the listener to handle an event when a piece is swapped
   *
   * @param listener listener to add
   */
  public void setOnSwapPiece(SwapPieceListener listener) {
    this.swapPieceListener = listener;
  }

  /**
   * Triggered when a piece is swapped. Call the attached listener
   *
   * @param event the mouse event
   */
  private void swapPiece(MouseEvent event) {
    logger.info("Swap the piece");
    if (swapPieceListener != null) {
      swapPieceListener.swapPiece();
    }
  }

  /**
   * Set the hover
   */
  public void setHover() {      // bind each block to the listener
    for (int i = 0; i < cols; i++) {
      for (int j = 0; j < rows; j++) {
        blocks[i][j].setIfHover();
      }
    }
  }

  /**
   * Set the fade out
   *
   * @param gameBlockCoordinate The coordinate of block to add fade out
   */
  public void fadeOut(Set<GameBlockCoordinate> gameBlockCoordinate) {
    for (GameBlockCoordinate g : gameBlockCoordinate) {
      getBlock(g.getX(), g.getY()).fadeOut();
    }
  }

  /**
   * Add the hover effect by the keyboard movement
   *
   * @param x the x coordinate
   * @param y the y coordinate
   */
  public void addHover(int x, int y) {
    getLastBlock();        // remove the effect which was added by the mouse
    blocks[x][y].handleKeyBoardMove();
  }

  /**
   * Remove the hover effect which is added by the keyboard movement
   *
   * @param x the x coordinate
   * @param y the y coordinate
   */
  public void removeHover(int x, int y) {
    blocks[x][y].paint();    // Repaint the block
  }

  /**
   * When there is a movement from the keyboard, remove the hover effect from the mouse
   */
  public void getLastBlock() {
    for (int i = 0; i < cols; i++) {
      for (int j = 0; j < rows; j++) {
        if (blocks[i][j].getABoolean()) {    // when the mouse move into this block
          removeHover(i, j);       // remove the hover effect
          blocks[i][j].setABoolean(false);        // set the boolean into false
          return;
        }
      }
    }
  }

  /**
   * Set the coordinate
   *
   * @param x the x coordinate
   * @param y the y coordinate
   */
  public void setCoordinate(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Remove the hover which is added by the keyboard
   */
  public void clearLastBlock() {
    removeHover(x, y);
  }
}
