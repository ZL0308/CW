package uk.ac.soton.comp1206.scene;

import javafx.animation.*;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.*;
import java.util.Set;

import static uk.ac.soton.comp1206.utility.Multimedia.stopPlaying;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

  /**
   * A logger to log messages and events that occur during the execution of an application.
   */
  private static final Logger logger = LogManager.getLogger(ChallengeScene.class);

  /**
   * The game
   */
  protected Game game;

  /**
   * The first piece board to display the current piece
   */
  protected PieceBoard pieceBoard1;

  /**
   * The second piece board to display the following piece
   */
  protected PieceBoard pieceBoard2;

  /**
   * The game board
   */
  protected GameBoard board;

  /**
   * The outer time bar
   */
  protected Rectangle timerBar;

  /**
   * The inner time bar
   */
  protected Rectangle innerBar;

  /**
   * The timeline to display the bar
   */
  protected Timeline progressTimeline;

  /**
   * The timeline to display the color
   */
  protected Timeline colorTimeline;

  /**
   * A reader to read the score from the local file
   */
  protected BufferedReader reader;

  /**
   * Create a new Single Player challenge scene
   *
   * @param gameWindow the Game Window
   */
  public ChallengeScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Challenge Scene");
  }

  /**
   * Build the Challenge window
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    setupGame();

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var challengePane = new StackPane();
    challengePane.setMaxWidth(gameWindow.getWidth());
    challengePane.setMaxHeight(gameWindow.getHeight());

    if (gameWindow.getBoolean()) {
      challengePane.getStyleClass().add("wallpaper");     // change the wall paper
    } else {
      challengePane.getStyleClass().add("menu-background");
    }
    root.getChildren().add(challengePane);

    var mainPane = new BorderPane();
    challengePane.getChildren().add(mainPane);

    board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2, gameWindow.getWidth() / 2);
    mainPane.setCenter(board);
    board.setHover();
    //Handle block on gameboard grid being clicked
    board.setOnBlockClick(this::blockClicked);
    board.setOnRightClicked(this::rightClicked);

    var labelScore = new Label("Score :");
    var labelLevel = new Label("Level :");
    var labelLives = new Label("Lives :");
    var labelMultiplier = new Label("Multiplier :");
    var labelIncoming = new Label("Incoming :");
    var labelHighestScore = new Label("HighestScore");

    labelScore.getStyleClass().add("score");
    labelLevel.getStyleClass().add("level");
    labelLives.getStyleClass().add("lives");
    labelMultiplier.getStyleClass().add("heading");
    labelIncoming.getStyleClass().add("heading");
    labelHighestScore.getStyleClass().add("hiscore");

    var score = new Label();
    score.textProperty().bind(game.scoreProperty().asString());
    score.getStyleClass().add("score");

    var level = new Label();
    level.textProperty().bind(game.levelProperty().asString());
    level.getStyleClass().add("level");

    var lives = new Label();
    lives.textProperty().bind(game.livesProperty().asString());
    lives.getStyleClass().add("lives");

    var multiplier = new Label();
    multiplier.textProperty().bind(game.multiplierProperty().asString());
    multiplier.getStyleClass().add("heading");

    var highestScore = new Label();
    highestScore.textProperty().bind(game.highestScoreProperty().asString());
    highestScore.getStyleClass().add("hiscore");

    game.setNextPieceListener(this::nextPiece);         // associate a event handler to a listener, 捆绑时接受的接口参数为listener
    // 接口中的方法接收的参数为gamePiece
    // this::updateThePiece  方法引用，调用本类中的此方法
    game.setLineClearedListener(this::fadeOut);

    pieceBoard1 = new PieceBoard(3, 3, 150, 150);
    pieceBoard2 = new PieceBoard(3, 3, 80, 80);
    pieceBoard1.setOnRightClicked(this::rightClicked);
    pieceBoard1.setIndicator();
    pieceBoard2.setOnSwapPiece(this::swapPiece);

    VBox component1 = new VBox();
    VBox component2 = new VBox();

    component1.setLayoutX(50);
    component1.setLayoutY(800);
    component1.setSpacing(15);

    component1.getChildren().addAll(labelLives, lives, labelHighestScore, highestScore, labelLevel, level, labelIncoming, pieceBoard1, pieceBoard2);

    mainPane.setRight(component1);

    component2.setLayoutX(50);
    component2.setLayoutY(800);
    component2.setSpacing(15);

    component2.getChildren().addAll(labelScore, score, labelMultiplier, multiplier);
    mainPane.setLeft(component2);

    timerBar = new Rectangle(800, 20, Color.GRAY);
    innerBar = new Rectangle(timerBar.getWidth(), timerBar.getHeight());

    var bar = new Pane();
    bar.getChildren().addAll(timerBar, innerBar);

    mainPane.setBottom(bar);
    game.setHighestScore(this::updateTheScore);
    game.setGameLoopListener(this::startTimerBar);
    game.setGameEndListener(this::endTheGame);

    var colorAdjust = new ColorAdjust();
    colorAdjust.setBrightness(gameWindow.getTheBrightness());
    mainPane.setEffect(colorAdjust);
    challengePane.setEffect(colorAdjust);
  }


  /**
   * Handle when a block is clicked
   *
   * @param gameBlock the Game Block that was clocked
   */
  public void blockClicked(GameBlock gameBlock) {
    game.blockClicked(gameBlock);
  }

  /**
   * Set up the game object and model
   */
  public void setupGame() {
    logger.info("Starting a new challenge");

    //Start new game
    game = new Game(5, 5);
  }

  /**
   * Initialise the scene and start the game
   */
  @Override
  public void initialise() {
    logger.info("Initialising Challenge");
    game.start();
    getHighScore();
    scene.setOnKeyPressed(keyEvent -> {
      switch (keyEvent.getCode()) {
        case ESCAPE -> {
          shutDown();
          gameWindow.startMenu();       // back to the menu
          logger.info("BAck to menu");
        }
        case UP, W -> {
          if (game.getPositionY() != 0) {     // can't be the block (0,0)
            board.removeHover(game.getPositionX(), game.getPositionY());
            game.setPositionY(game.getPositionY() - 1);                 // update the position after removing
            board.addHover(game.getPositionX(), game.getPositionY());   // add the hover
            board.setCoordinate(game.getPositionX(), game.getPositionY()); // update the new position
          }
        }
        case DOWN, S -> {
          if (game.getPositionX() < game.getRows()) {
            board.removeHover(game.getPositionX(), game.getPositionY());
            game.setPositionY(game.getPositionY() + 1);
            board.addHover(game.getPositionX(), game.getPositionY());
            board.setCoordinate(game.getPositionX(), game.getPositionY());
          }
        }
        case LEFT, A -> {
          if (game.getPositionX() != 0) {
            board.removeHover(game.getPositionX(), game.getPositionY());
            game.setPositionX(game.getPositionX() - 1);
            board.addHover(game.getPositionX(), game.getPositionY());
            board.setCoordinate(game.getPositionX(), game.getPositionY());
          }
        }
        case RIGHT, D -> {
          if (game.getPositionX() < game.getCols()) {
            board.removeHover(game.getPositionX(), game.getPositionY());
            game.setPositionX(game.getPositionX() + 1);
            board.addHover(game.getPositionX(), game.getPositionY());
            board.setCoordinate(game.getPositionX(), game.getPositionY());
          }
        }
        case SPACE, R -> swapPiece();
        case ENTER, X -> game.playThePiece();
        case Q, Z, OPEN_BRACKET -> rotateLeft();
        case E, C, CLOSE_BRACKET -> rightClicked();
      }
    });
  }

  /**
   * A event handler to handle the event
   */
  public void nextPiece() {      // gamePiece    An event
    pieceBoard1.setPiece(game.getCurrentPiece());
    pieceBoard2.setPiece(game.getFollowingPiece());
  }

  /**
   * Shut Down the game
   */
  public void shutDown() {
    logger.info("Shutdown the backgrounds tasks");
    stopPlaying();
    game.terminateTheGame();
  }

  /**
   * Right-clicked and rotate the piece
   */
  public void rightClicked() {
    game.rotateCurrentPiece();
    pieceBoard1.setPiece(game.getCurrentPiece());
  }

  /**
   * Swap the piece
   */
  public void swapPiece() {
    game.swapCurrentPiece();
    pieceBoard1.setPiece(game.getCurrentPiece());
    pieceBoard2.setPiece(game.getFollowingPiece());
  }

  /**
   * Rotate the piece to the left
   */
  public void rotateLeft() {
    game.getCurrentPiece().rotate(3);
    pieceBoard1.setPiece(game.getCurrentPiece());
  }

  /**
   * Add fade out effect
   *
   * @param gameBlockCoordinate a set of coordinate
   */
  public void fadeOut(Set<GameBlockCoordinate> gameBlockCoordinate) {
    board.fadeOut(gameBlockCoordinate);
  }

  /**
   * Display the inner time bar
   *
   * @return the timeline
   */
  public Timeline fillInnerBar() {
    progressTimeline = new Timeline(
            new KeyFrame(getDuration(), new KeyValue(innerBar.widthProperty(), 0)),
            new KeyFrame(Duration.ZERO, new KeyValue(innerBar.widthProperty(), timerBar.getWidth()))
    );
    return progressTimeline;
  }

  /**
   * The timeline to display the different color
   *
   * @return the color timeline
   */
  public Timeline colorTimeline() {
    colorTimeline = new Timeline(         // Different color in different time
            new KeyFrame(Duration.ZERO, new KeyValue(innerBar.fillProperty(), Color.GREEN)),
            new KeyFrame(getDuration().divide(4), new KeyValue(innerBar.fillProperty(), Color.YELLOW)),
            new KeyFrame(getDuration().divide(2), new KeyValue(innerBar.fillProperty(), Color.ORANGE)),
            new KeyFrame(getDuration().multiply(0.75), new KeyValue(innerBar.fillProperty(), Color.RED)),
            new KeyFrame(getDuration(), new KeyValue(innerBar.fillProperty(), null)));
    colorTimeline.setCycleCount(1);
    return colorTimeline;
  }

  /**
   * Add the timeline into the transition and start to play it
   */
  public void startTimerBar() {
    var parallelTransition = new ParallelTransition(colorTimeline(), fillInnerBar());
    parallelTransition.play();
  }

  /**
   * Get the time of a live
   *
   * @return the time
   */
  public Duration getDuration() {
    int delay = game.getDelay();
    return new Duration(delay);    // return a new duration class
  }

  /**
   * Terminate the game
   */
  public void endTheGame() {
    game.terminateTheGame();
    stopPlaying();
    gameWindow.startScoreScene(game);     // jump to the score scene
  }

  /**
   * Get the high score
   */
  public void getHighScore() {
    var file = new File("Scores.txt");
    try {
      if (!file.exists()) {
        file.createNewFile();
        var writer = new FileWriter(file);
        for (int i = 0; i < 10; i++) {             //  If there is no file, write a default list
          writer.write("aaa:50\n");
        }
        writer.close();
      }
      this.reader = new BufferedReader(new FileReader(file));
      while (fileIsReady()) {
        String[] s = getLine().split(":");
        int score = Integer.parseInt(s[1]);
        if (score > game.getHighestScore()) {
          game.setHighestScore(score);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Read a line from the text
   *
   * @return the line
   */
  public String getLine() {
    String line;
    try {
      line = reader.readLine();
      return line;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Determine if the file is ready
   *
   * @return a boolean
   */
  public boolean fileIsReady() {
    try {
      return reader.ready();
    } catch (RuntimeException | IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Update the score
   */
  public void updateTheScore() {
    if (game.getScore() > game.getHighestScore()) {
      game.setHighestScore(game.getScore());
    }
  }
}
