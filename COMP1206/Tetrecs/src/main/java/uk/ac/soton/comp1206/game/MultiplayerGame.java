package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The Multiplayer Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class MultiplayerGame extends Game {

  /**
   * A new game window to display the game
   */
  private GameWindow gameWindow;

  /**
   * A communicator to communicate with the sever
   */
  private Communicator communicator;

  /**
   * A string to store previous message
   */
  private String previousMessage;

  /**
   * A queue to preload the game piece
   */
  private Queue<Integer> queue;

  /**
   * Create a new game with the specified rows and columns. Creates a corresponding grid model.
   *
   * @param cols       number of columns
   * @param rows       number of rows
   * @param gameWindow the game window
   */
  public MultiplayerGame(int cols, int rows, GameWindow gameWindow) {
    super(cols, rows);
    this.gameWindow = gameWindow;
  }

  /**
   * Initialize the game
   */
  @Override
  public void initialiseGame() {
    this.communicator = gameWindow.getCommunicator();
    communicator.addListener(this::receiveMessage);
    queue = new LinkedList<>();
    for (int i = 0; i < 5; i++) {
      communicator.send("PIECE");
    }
  }

  /**
   * Process the message which sent by the server
   *
   * @param message the message
   */
  public void receiveMessage(String message) {
    Platform.runLater(() -> {
      previousMessage = message;
      if (previousMessage.contains("PIECE")) {     // receive the next piece
        previousMessage = previousMessage.replace("PIECE", "").trim();
        int value = Integer.parseInt(previousMessage);
        queue.add(value);
        if (currentPiece == null) {
          nextPiece();          // load next piece
        }
      }
    });
  }

  /**
   * Generate the new game piece
   *
   * @return the game piece
   */
  @Override
  public GamePiece spawnPiece() {
    var gamePiece = GamePiece.createPiece(queue.remove());
    communicator.send("PIECE");       // request a new piece
    return gamePiece;
  }

  /**
   * Clear the blocks and send the new score to the server
   */
  @Override
  public void afterPiece() {
    super.afterPiece();
    communicator.send("SCORE " + getScore());
  }

  /**
   * The game loop for each live
   */
  @Override
  public void gameLoop() {
    scheduler = Executors.newScheduledThreadPool(1);

    getTimerDelay();
    gameLoopListener.startTimerBar();
    Runnable task = () -> {
      gameLoopListener.startTimerBar();
      Platform.runLater(() -> {               // guarantee the thread safety
        lives.set(lives.get() - 1);
        communicator.send("LIVES " + getLives());
        Multimedia.playSound("/sounds/lifelose.wav");
        nextPiece();
        if (lives.get() < 0) {
          gameEndListener.endTheGame();
          scheduler.shutdown();
        } else {
          nextPiece();
          multiplier.set(1);
        }
      });
    };
    scheduler.scheduleAtFixedRate(task, delay - 1, delay, TimeUnit.MILLISECONDS);
  }
}
