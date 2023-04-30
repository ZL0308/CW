package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.*;

import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static uk.ac.soton.comp1206.utility.Multimedia.playBGM;
import static uk.ac.soton.comp1206.utility.Multimedia.playSound;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

  /**
   * A logger to log messages and events that occur during the execution of an application.
   */
  private static final Logger logger = LogManager.getLogger(Game.class);

  /**
   * Number of rows
   */
  protected final int rows;

  /**
   * Number of columns
   */
  protected final int cols;

  /**
   * The grid model linked to the game
   */
  protected final Grid grid;

  /**
   * The current piece
   */
  protected GamePiece currentPiece;

  /**
   * The next piece
   */
  protected GamePiece followingPiece;

  /**
   * A listener to clear a line
   */
  protected LineClearedListener lineClearedListener;

  /**
   * An integer property contains the score of the game
   */
  protected IntegerProperty score;

  /**
   * An integer property contains the level of the game
   */
  protected IntegerProperty level;

  /**
   * An integer property contains the lives of the game
   */
  protected IntegerProperty lives;

  /**
   * An integer property contains the multiplier of the game
   */
  protected IntegerProperty multiplier;

  /**
   * An integer property contains the highest score of the game
   */
  protected IntegerProperty highestScore;

  /**
   * The number of lines which is to be cleared
   */
  protected int lines;

  /**
   * A next piece listener
   */
  protected NextPieceListener currentPieceListener;

  /**
   * The position of the piece
   */
  protected int positionX = 0;

  /**
   * The position of the piece
   */
  protected int positionY = 0;

  /**
   * The time of each live
   */
  protected int delay;

  /**
   * A listener of the game loop
   */
  protected GameLoopListener gameLoopListener;

  /**
   * A scheduler to schedule the time of live
   */
  protected ScheduledExecutorService scheduler;

  /**
   * A game end listener to monitor the status of the game
   */
  protected GameEndListener gameEndListener;

  /**
   * A high score listener to update the highest score
   */
  protected HighScoreListener highScoreListener;


  /**
   * Create a new game with the specified rows and columns. Creates a corresponding grid model.
   *
   * @param cols number of columns
   * @param rows number of rows
   */
  public Game(int cols, int rows) {
    this.cols = cols;
    this.rows = rows;

    //Create a new grid model to represent the game state
    this.grid = new Grid(cols, rows);

    this.score = new SimpleIntegerProperty(0);
    this.level = new SimpleIntegerProperty(0);
    this.lives = new SimpleIntegerProperty(3);
    this.multiplier = new SimpleIntegerProperty(1);
    this.highestScore = new SimpleIntegerProperty(0);
  }

  /**
   * Start the game
   */
  public void start() {
    logger.info("Starting game");
    initialiseGame();
    playBGM("/music/game_start.wav");
    gameLoop();
  }

  /**
   * Initialise a new game and set up anything that needs to be done at the start
   */
  public void initialiseGame() {
    logger.info("Initialising game");
    followingPiece = spawnPiece();           //初始化一个followingPiece
    nextPiece();                             //将followingPiece赋给currentPiece，更新一个新的followingPiece
  }

  /**
   * Handle what should happen when a particular block is clicked
   *
   * @param gameBlock the block that was clicked
   */
  public void blockClicked(GameBlock gameBlock) {

    //Get the position of this block
    int x = gameBlock.getX();
    int y = gameBlock.getY();

    if (grid.canPlayPiece(currentPiece, x, y)) {
      logger.info("Piece is placed");
      positionX = x;
      positionY = y;
      grid.playPiece(currentPiece, positionX, positionY);     // play the piece
      nextPiece();      // update the new piece
      afterPiece();     // clear any full line
      scheduler.close();  // close the scheduler
      gameLoop();       // restart a new game loop
      playSound("/sounds/place.wav");
    } else {
      logger.info("Piece can't be placed");
      playSound("/sounds/fail.wav");
    }
  }

  /**
   * Get the number of columns in this game
   *
   * @return number of columns
   */
  public int getCols() {
    return cols;
  }

  /**
   * Get the number of rows in this game
   *
   * @return number of rows
   */
  public int getRows() {
    return rows;
  }

  /**
   * Get the grid model inside this game representing the game state of the board, for multiplayer mode
   *
   * @return game grid model
   */
  public Grid getGrid() {
    return grid;
  }

  /**
   * Randomly generate a piece
   *
   * @return gamePiece
   */
  public GamePiece spawnPiece() {
    Random random = new Random();
    int randomNum = random.nextInt(15);
    return GamePiece.createPiece(randomNum);
  }

  /**
   * Replace the current piece with a new piece
   */
  public void nextPiece() {
    currentPiece = followingPiece;
    followingPiece = spawnPiece();
    currentPieceListener.nextPiece();     // call the method on the listener and trigger the event
  }

  /**
   * Clear any full vertical/horizontal lines that have been made
   */
  public void afterPiece() {
    int blocksNum = 0;                   // The number of blocks to be cleared
    lines = 0;                           // Reset the line
    var toClear = new HashSet<GameBlockCoordinate>();   // A hashset contains the blocks to be cleared

    clearCol(toClear);                    // Clear the col in the hashset
    clearRow(toClear);

    if (lines > 0) {
      for (GameBlockCoordinate block : toClear) {
        grid.set(block.getX(), block.getY(), 0);    // If a line is full, cleat it
        blocksNum++;                                      // Calculate the number of blocks which are cleared
      }
      lineClearedListener.fadeOut(toClear);               // Add fade out effect
      score(lines, blocksNum);                            // Calculate the score
      highScoreListener.updateScore();                    // Update the score
      logger.info("Clear the blocks");
      playSound("/sounds/clear.wav");
    } else {                                              // No lines to be cleared
      multiplier.set(1);
    }

    if (getScore() / 1000 != level.get()) {     // Calculate the level
      level.set(getScore() / 1000);
      playSound("/sounds/level.wav");
    }
  }

  /**
   * Clear the full column
   *
   * @param set A hashset
   */
  public void clearCol(HashSet<GameBlockCoordinate> set) {
    for (int i = 0; i < getCols(); i++) {                            // Iterate the col
      var mightBeCleared = new HashSet<GameBlockCoordinate>(); // Create a new hashset to contain a row of block
      for (int j = 0; j < getRows(); j++) {
        if (grid.get(i, j) == 0) {                                   // If a row contains empty block, don't clear it
          break;
        } else {
          mightBeCleared.add(new GameBlockCoordinate(i, j));         // Add the block into the hashset
        }
      }
      if (mightBeCleared.size() == getRows()) {                      // size == row
        lines++;                                                     // The number of lines to be cleared
        for (int j = 0; j < getRows(); j++) {
          set.add(new GameBlockCoordinate(i, j));                    // Add these blocks back to the hashset which to be cleared
        }
      }
    }
  }

  /**
   * Clear the full row
   *
   * @param set A hashset
   */
  public void clearRow(HashSet<GameBlockCoordinate> set) {
    for (int j = 0; j < getRows(); j++) {
      var mightBeCleared = new HashSet<GameBlockCoordinate>();
      for (int i = 0; i < getCols(); i++) {
        if (grid.get(i, j) == 0) {
          break;
        } else {
          mightBeCleared.add(new GameBlockCoordinate(i, j));
        }
      }
      if (mightBeCleared.size() == getCols()) {
        lines++;
        for (int i = 0; i < getCols(); i++) {
          set.add(new GameBlockCoordinate(i, j));
        }
      }
    }
  }

  /**
   * To get the score
   *
   * @return int score
   */
  public int getScore() {
    return score.get();
  }

  /**
   * To get IntegerProperty scoreProperty
   *
   * @return an Integer Property
   */
  public IntegerProperty scoreProperty() {
    return score;
  }

  /**
   * To get the level
   *
   * @return int level
   */
  public int getLevel() {
    return level.get();
  }

  /**
   * To get IntegerProperty levelProperty
   *
   * @return an Integer Property
   */
  public IntegerProperty levelProperty() {
    return level;
  }

  /**
   * To get the rest of lives
   *
   * @return int lives
   */
  public int getLives() {
    return lives.get();
  }

  /**
   * Get IntegerProperty livesProperty
   *
   * @return an Integer Property
   */
  public IntegerProperty livesProperty() {
    return lives;
  }

  /**
   * To get the multiplier
   *
   * @return int multiplier
   */
  public int getMultiplier() {
    return multiplier.get();
  }

  /**
   * To get IntegerProperty multiplierProperty
   *
   * @return an Integer Property
   */
  public IntegerProperty multiplierProperty() {
    return multiplier;
  }

  /**
   * Update the score
   *
   * @param lines  the number of lines which to be cleared
   * @param blocks the number of blocks which to be cleared
   */
  public void score(int lines, int blocks) {
    int newScore = lines * blocks * 10 * getMultiplier();
    score.set(getScore() + newScore);
    multiplier.set(getMultiplier() + 1);
  }

  /**
   * Get the current piece
   *
   * @return the current piece
   */
  public GamePiece getCurrentPiece() {
    return currentPiece;
  }

  /**
   * A method to set the listener
   *
   * @param listener A CurrentPiece listener
   */
  public void setNextPieceListener(NextPieceListener listener) {
    currentPieceListener = listener;
  }

  /**
   * Rotate the current piece
   */
  public void rotateCurrentPiece() {
    currentPiece.rotate();
    playSound("/sounds/rotate.wav");
  }

  /**
   * Swap the current Piece with the following piece
   */
  public void swapCurrentPiece() {
    GamePiece piece = followingPiece;
    followingPiece = currentPiece;
    currentPiece = piece;
    playSound("/sounds/transition.wav");
  }

  /**
   * Get the following piece
   *
   * @return the following piece
   */
  public GamePiece getFollowingPiece() {
    return followingPiece;
  }

  /**
   * Track the coordinate
   *
   * @return x coordinate
   */
  public int getPositionX() {
    return positionX;
  }

  /**
   * Track the coordinate
   *
   * @return y coordinate
   */
  public int getPositionY() {
    return positionY;
  }

  /**
   * Set the new coordinate
   *
   * @param positionX x coordinate
   */
  public void setPositionX(int positionX) {
    this.positionX = positionX;
  }

  /**
   * Set the new coordinate
   *
   * @param positionY y coordinate
   */
  public void setPositionY(int positionY) {
    this.positionY = positionY;
  }

  /**
   * A method for keyboard to play a piece
   */
  public void playThePiece() {
    if (grid.canPlayPiece(currentPiece, positionX, positionY)) {
      logger.info("Piece is placed");
      grid.playPiece(getCurrentPiece(), positionX, positionY);  // Place a piece into the provided coordinate
      nextPiece();
      afterPiece();
      playSound("/sounds/place.wav");
    } else {
      logger.info("Can't place this piece");
      playSound("/sounds/fail.wav");
    }
  }

  /**
   * Set the listener
   *
   * @param listener a listener
   */
  public void setLineClearedListener(LineClearedListener listener) {
    this.lineClearedListener = listener;
  }

  /**
   * Get the time of each live
   */
  public void getTimerDelay() {
    if (getLevel() < 19) {
      delay = 12000 - 500 * getLevel();
    } else {
      delay = 2500;
    }
  }

  /**
   * The game loop for each live
   */
  public void gameLoop() {
    scheduler = Executors.newScheduledThreadPool(1);    // a scheduler

    getTimerDelay();
    gameLoopListener.startTimerBar();           // start the time bar in the first live
    Runnable task = () -> {
      gameLoopListener.startTimerBar();
      Platform.runLater(() -> {                 // guarantee the thread safety
        lives.set(lives.get() - 1);             // lose a live
        playSound("/sounds/lifelose.wav");
        nextPiece();
        if (lives.get() < 0) {                  // game over
          gameEndListener.endTheGame();
          scheduler.close();
        } else {
          multiplier.set(1);                     // reset the multiplier
        }
      });
    };
    scheduler.scheduleAtFixedRate(task, delay - 1, delay, TimeUnit.MILLISECONDS); // initial delay to guarantee the first live be executed
  }

  /**
   * Set the listener of the game loop
   *
   * @param listener a listener
   */
  public void setGameLoopListener(GameLoopListener listener) {
    this.gameLoopListener = listener;
  }

  /**
   * Get the time of each live
   *
   * @return the time
   */
  public int getDelay() {
    getTimerDelay();
    return delay;
  }

  /**
   * Terminate the game
   */
  public void terminateTheGame() {
    scheduler.close();
  }

  /**
   * Set the Listener to monitor the game status
   *
   * @param listener the listener
   */
  public void setGameEndListener(GameEndListener listener) {
    this.gameEndListener = listener;
  }

  /**
   * Get the highest score
   *
   * @return the highest score
   */
  public int getHighestScore() {
    return highestScore.get();
  }

  /**
   * Get the highest score property
   *
   * @return a simple integer property
   */
  public IntegerProperty highestScoreProperty() {
    return highestScore;
  }

  /**
   * Set the highest
   *
   * @param highestScore the highest score
   */
  public void setHighestScore(int highestScore) {
    this.highestScore.set(highestScore);
  }

  /**
   * Set the listener to update the highest score
   *
   * @param listener a listener
   */
  public void setHighestScore(HighScoreListener listener) {
    this.highScoreListener = listener;
  }
}
