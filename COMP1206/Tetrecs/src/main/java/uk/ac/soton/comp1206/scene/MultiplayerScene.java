package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.Leaderboard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static uk.ac.soton.comp1206.utility.Multimedia.*;

/**
 * A Multiplayer Scene extends ChallengeScene, which holds the UI for the multiplayer challenge mode in the game.
 */
public class MultiplayerScene extends ChallengeScene {

  /**
   * A logger to log messages and events that occur during the execution of an application.
   */
  private static final Logger logger = LogManager.getLogger(MultiplayerScene.class);

  /**
   * A communicator takes responsible to communicate with the server.
   */
  private Communicator communicator;

  /**
   * A string to store the userName
   */
  private final String name;

  /**
   * The message which is received by the communicator
   */
  private String previousMessage;

  /**
   * The scheduler to request the score
   */
  private ScheduledExecutorService scheduler;

  /**
   * The HBox to display the message in the game
   */
  private HBox messageBox;

  /**
   * The input field to store the chatting message during the game
   */
  private TextField inputField;

  /**
   * The list to be displayed during the game
   */
  private SimpleListProperty<Pair<String, Integer>> userList;

  /**
   * The list to be sent to the score scene
   */
  private SimpleListProperty<Pair<String, Integer>> scoreList;

  /**
   * The leader board to display the real time score
   */
  private Leaderboard leaderboard;

  /**
   * A boolean flag to avoid the conflict between sending message and placing a piece
   */
  private boolean ifMessage = false;

  /**
   * A boolean flag to determine the status of the game
   */
  private boolean ifEnd = false;


  /**
   * Create a new Single Player challenge scene
   *
   * @param gameWindow the Game Window
   * @param name       the name of the player
   */
  public MultiplayerScene(GameWindow gameWindow, String name) {
    super(gameWindow);
    this.name = name;
    logger.info("Creating Multiplayer Challenge Scene");
  }

  /**
   * Build the menu layout
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
      challengePane.getStyleClass().add("wallpaper");
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

    var labelScore = new Label("Scores");
    var labelLives = new Label("Lives");
    var labelVersus = new Label("Versus");
    var labelIncoming = new Label("Incoming :");

    labelScore.getStyleClass().add("heading");
    labelLives.getStyleClass().add("heading");
    labelVersus.getStyleClass().add("heading");
    labelIncoming.getStyleClass().add("heading");
    var score = new Label();
    score.textProperty().bind(game.scoreProperty().asString());
    score.getStyleClass().add("score");

    var lives = new Label();
    lives.textProperty().bind(game.livesProperty().asString());
    lives.getStyleClass().add("lives");

    game.setNextPieceListener(this::nextPiece);
    game.setLineClearedListener(this::fadeOut);

    pieceBoard1 = new PieceBoard(3, 3, 150, 150);
    pieceBoard2 = new PieceBoard(3, 3, 80, 80);
    pieceBoard1.setOnRightClicked(this::rightClicked);
    pieceBoard1.setIndicator();
    pieceBoard2.setOnSwapPiece(this::swapPiece);

    var component1 = new VBox();            // Left box to contain the score and name
    var component2 = new VBox();            // Right box to contain the real time score and the peice board

    component1.setLayoutX(50);
    component1.setLayoutY(800);
    component1.setSpacing(15);

    component2.setLayoutX(50);
    component2.setLayoutY(800);
    component2.setSpacing(15);
    component2.setPrefSize(150, 800);

    mainPane.setLeft(component1);
    mainPane.setRight(component2);

    userList = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
    scoreList = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
    leaderboard = new Leaderboard();
    leaderboard.scoresProperty().bind(userList);
    leaderboard.setAlignment(Pos.CENTER);

    component1.getChildren().addAll(labelScore, score);
    component2.getChildren().addAll(labelLives, lives, labelVersus, leaderboard, labelIncoming, pieceBoard1, pieceBoard2);
    var boxText = new Text("In-Game Chat: Press T to send a chat message");
    boxText.getStyleClass().add("heading");
    messageBox = new HBox(boxText);

    //input and sending message
    inputField = new TextField();
    inputField.setVisible(false);

    timerBar = new Rectangle(800, 20, Color.GRAY);
    innerBar = new Rectangle(timerBar.getWidth(), timerBar.getHeight());
    Pane bar = new Pane();
    bar.getChildren().addAll(timerBar, innerBar);

    game.setHighestScore(this::updateTheScore);
    game.setGameLoopListener(this::startTimerBar);
    game.setGameEndListener(this::endTheGame);

    var bottomBox = new VBox();
    bottomBox.setSpacing(10);
    bottomBox.getChildren().addAll(messageBox, inputField, bar);

    mainPane.setBottom(bottomBox);
    bottomBox.setAlignment(Pos.BOTTOM_CENTER);

    var colorAdjust = new ColorAdjust();          // set the brightness
    colorAdjust.setBrightness(gameWindow.getTheBrightness());
    mainPane.setEffect(colorAdjust);
    challengePane.setEffect(colorAdjust);
  }

  /**
   * Set up the game
   */
  @Override
  public void setupGame() {
    logger.info("Starting a new challenge");
    this.game = new MultiplayerGame(5, 5, this.gameWindow);
  }

  /**
   * Initialize the multiplayer scene
   */
  @Override
  public void initialise() {
    communicator = gameWindow.getCommunicator();
    communicator.addListener(this::receiveMessage);
    logger.info("Initialising Challenge");
    game.start();
    playBGM("/music/game_start.wav");
    getHighScore();

    scene.setOnKeyPressed(keyEvent -> {
      switch (keyEvent.getCode()) {
        case ESCAPE -> {
          communicator.send("DIE");
          scheduler.close();
          game.terminateTheGame();
          stopPlaying();
          gameWindow.startMultiplayerGame();
        }
        case UP, W -> {
          if (game.getPositionY() != 0) {
            board.removeHover(game.getPositionX(), game.getPositionY());
            game.setPositionY(game.getPositionY() - 1);
            board.addHover(game.getPositionX(), game.getPositionY());
            board.setCoordinate(game.getPositionX(), game.getPositionY());
          }
        }
        case DOWN, S -> {
          if (game.getPositionX() <= game.getRows()) {
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
          if (game.getPositionX() <= game.getCols()) {
            board.removeHover(game.getPositionX(), game.getPositionY());
            game.setPositionX(game.getPositionX() + 1);
            board.addHover(game.getPositionX(), game.getPositionY());
            board.setCoordinate(game.getPositionX(), game.getPositionY());
          }
        }
        case SPACE, R -> swapPiece();
        case ENTER -> {               // Avoid the conflict of the sending message and placing a piece
          if (!ifMessage) {
            game.playThePiece();
          } else {
            communicator.send("MSG " + inputField.getText());
            inputField.clear();
            inputField.setVisible(false);
            ifMessage = false;
          }
        }
        case Q, Z, OPEN_BRACKET -> rotateLeft();
        case E, C, CLOSE_BRACKET -> rightClicked();
        case T -> {
          inputField.setVisible(true);
          ifMessage = true;
        }
        case X -> game.playThePiece();
      }
    });
    scheduler = new ScheduledThreadPoolExecutor(1);
    scheduler.scheduleAtFixedRate(() -> {                   // Keep request the score from the server
      communicator.send("SCORES");
    }, 1, 3, TimeUnit.SECONDS);
  }

  /**
   * Process the message
   *
   * @param message the message
   */
  public void receiveMessage(String message) {
    Platform.runLater(() -> {
      previousMessage = message;
      if (previousMessage.contains("SCORES")) {    // Get and update the scores
        String[] s = previousMessage.replace("SCORES ", "").split("\n");
        userList.clear();                // a list to be displayed in leader board
        if (!ifEnd) {
          scoreList.clear();            // a list to be displayed in score scene
        }
        for (String s1 : s) {
          String[] s2 = s1.split(":");
          userList.add(new Pair<>(s2[0] + " : " + s2[2], Integer.parseInt(s2[1])));
          if (!ifEnd) {
            scoreList.add(new Pair<>(s2[0], Integer.parseInt(s2[1])));
          }
        }
        userList.sort((pair1, pair2) -> Integer.compare(pair2.getValue(), pair1.getValue()));     // sort the list by the score
        scoreList.sort((pair1, pair2) -> Integer.compare(pair2.getValue(), pair1.getValue()));
        leaderboard.displayScore();
      } else if (previousMessage.contains("MSG")) {                                               // get the chatting message
        String[] s = previousMessage.replace("MSG", "").trim().split(":");
        messageBox.getChildren().clear();
        var text = new Text("<" + s[0] + "> " + s[1]);
        text.wrappingWidthProperty().bind(messageBox.widthProperty());
        text.getStyleClass().add("heading");
        messageBox.getChildren().add(text);
      }
    });
  }

  /**
   * End the game
   */
  @Override
  public void endTheGame() {
    ifEnd = true;
    scheduler.close();
    game.terminateTheGame();
    stopPlaying();
    communicator.send("DIE");
    gameWindow.startScoreScene(game, name, scoreList);
  }
}
