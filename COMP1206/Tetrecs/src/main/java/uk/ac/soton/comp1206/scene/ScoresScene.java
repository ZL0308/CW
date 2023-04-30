package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.ScoresList;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

import static uk.ac.soton.comp1206.utility.Multimedia.*;

/**
 * The Scores Scene to display the score of each player
 */
public class ScoresScene extends BaseScene {

  /**
   * The game
   */
  private final Game game;

  /**
   * The list property of the local score
   */
  private final SimpleListProperty<Pair<String, Integer>> localScores;

  /**
   * The list property of the remote score
   */
  private SimpleListProperty<Pair<String, Integer>> remoteScores;

  /**
   * A logger to log messages and events that occur during the execution of an application.
   */
  private static final Logger logger = LogManager.getLogger(ScoresScene.class);

  /**
   * A buffer reader to read the local score
   */
  private BufferedReader reader;

  /**
   * The text field to store the player name
   */
  private TextField inputField;

  /**
   * The border pane
   */
  private BorderPane mainPane;

  /**
   * The local score list
   */
  private ScoresList scoresList;

  /**
   * The remote score list
   */
  private ScoresList remoteScoreList;

  /**
   * A communicator takes responsible to communicate with the server.
   */
  private final Communicator communicator;

  /**
   * A string to store the message.
   */
  private String previousMessage;
  /**
   * The player name in multiplayer mode
   */
  private String playerName;

  /**
   * The player name
   */
  private String name;

  /**
   * A boolean flag to determine if the current score beat any local score
   */
  private boolean local;

  /**
   * A boolean flag to determine if the current score beat any remote score
   */
  private boolean remote;

  /**
   * Create a new scene, passing in the GameWindow the scene will be displayed in, and the game
   *
   * @param gameWindow the game window
   * @param game       the game
   */
  public ScoresScene(GameWindow gameWindow, Game game) {
    super(gameWindow);
    this.game = game;
    communicator = gameWindow.getCommunicator();
    localScores = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
  }

  /**
   * Create a new scene, passing in the GameWindow the scene will be displayed in, the game, and the score list in the channel
   *
   * @param gameWindow the game window
   * @param game       the game
   * @param name       the name of the player
   * @param list       the score list in the channel
   */
  public ScoresScene(GameWindow gameWindow, Game game, String name, SimpleListProperty<Pair<String, Integer>> list) {
    super(gameWindow);
    this.game = game;
    playerName = name;
    communicator = gameWindow.getCommunicator();
    localScores = new SimpleListProperty<>(list);   // Receive the score list directly
  }

  /**
   * Initialize the Score Scene
   */
  @Override
  public void initialise() {
    communicator.addListener(this::receiveCommunication);
    playBGM("/music/end.wav");
    loadOnlineScores();
    if (!(game instanceof MultiplayerGame)) {
      loadScore();          // If is single player, load the score directly
    }
    this.scene.setOnKeyPressed((event) ->
    {
      if (event.getCode() != KeyCode.ESCAPE) return;
      gameWindow.startMenu();
      stopPlaying();
    });
    compareWithLocal();     // Compare the current score with the local score
  }

  /**
   * Build the Score window
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    remoteScores = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));    // Initialize the list as a observable arraylist
    scoresList = new ScoresList();
    remoteScoreList = new ScoresList();

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var scorePane = new StackPane();
    scorePane.setMaxWidth(gameWindow.getWidth());
    scorePane.setMaxHeight(gameWindow.getHeight());
    if (gameWindow.getBoolean()) {
      scorePane.getStyleClass().add("wallpaper");       // Load the wall paper
    } else {
      scorePane.getStyleClass().add("menu-background");
    }
    root.getChildren().add(scorePane);

    mainPane = new BorderPane();
    scorePane.getChildren().add(mainPane);

    // Load the heading
    var imageAddress2 = MenuScene.class.getResource("/images/TetrECS.png").toExternalForm();
    var image = new Image(imageAddress2);
    var imageView = new ImageView(image);
    var imageBox = new HBox();
    imageBox.setPrefWidth(650);
    imageBox.setPrefHeight(150);
    imageView.setFitWidth(imageBox.getPrefWidth());
    imageView.setFitHeight(imageBox.getPrefHeight());
    imageBox.getChildren().add(imageView);
    imageBox.setAlignment(Pos.CENTER);

    mainPane.setTop(imageBox);

    scoresList.scoresProperty().bind(localScores);        // bind the score list  with the observable local scores
    remoteScoreList.scoresProperty().bind(remoteScores);

    var colorAdjust = new ColorAdjust();                  // set the brightness
    colorAdjust.setBrightness(gameWindow.getTheBrightness());
    mainPane.setEffect(colorAdjust);
    scorePane.setEffect(colorAdjust);
  }

  /**
   * Load the score from local
   */
  public void loadScore() {
    var file = new File("Scores.txt");
    try {
      this.reader = new BufferedReader(new FileReader(file));
      while (fileIsReady()) {
        String[] s = getLine().split(":");
        localScores.add(new Pair<>(s[0], Integer.parseInt(s[1])));
      }
      localScores.sort(Comparator.comparingInt((Pair<String, Integer> pair) -> pair.getValue()).reversed());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Read a line from the file
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
   * A boolean to determine if the file is ready
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
   * Create the input box for inputting name
   */
  public void addScore() {

    //Add the highest score
    VBox nameBox = new VBox();
    nameBox.setSpacing(10);

    var label2 = new Label("Game Over");
    label2.getStyleClass().add("bigtitle");
    var gameOver = new HBox();
    gameOver.getChildren().addAll(label2);
    gameOver.setAlignment(Pos.TOP_CENTER);

    Button saveName = new Button("Save");

    var field = new HBox();
    inputField = new TextField();
    field.getChildren().add(inputField);
    HBox.setHgrow(inputField, Priority.ALWAYS);

    Text message = new Text("You got a high score");
    message.getStyleClass().add("heading");
    var text = new HBox();
    text.getChildren().add(message);

    nameBox.getChildren().addAll(gameOver, field, saveName, text);
    mainPane.setCenter(nameBox);
    nameBox.setAlignment(Pos.TOP_CENTER);
    text.setAlignment(Pos.CENTER);

    saveName.setOnAction((actionEvent -> saveScore()));

    inputField.setOnKeyPressed(actionEvent -> {
      if (actionEvent.getCode() != KeyCode.ENTER) return;
      saveScore();
    });
  }

  /**
   * Save the name and highest score
   */
  public void saveScore() {

    if (!(game instanceof MultiplayerGame)) {   // When single mode, add the name
      name = inputField.getText();
      inputField.clear();
      mainPane.getChildren().remove(1);
    }

    if (remote) {                               // current > remote
      writeOnlineScore();
      if (game.getScore() < 50000) {            // the maximum score the server can receive is 49999
        remoteScores.add(new Pair<>(name, game.getScore()));
        remoteScores.sort(Comparator.comparingInt((Pair<String, Integer> pair) -> pair.getValue()).reversed());
        remoteScores.remove(10);
      }
    }

    if (local) {                                // Single mode, current > local but < remote
      localScores.add(new Pair<>(name, game.getScore()));
      localScores.sort(Comparator.comparingInt((Pair<String, Integer> pair) -> pair.getValue()).reversed());
      localScores.remove(10);
    }

    displayScore();

    if (!(game instanceof MultiplayerGame)) {   // Only in single mode
      writeScore();
    }
  }

  /**
   * Write the score into local file in order
   */
  public void writeScore() {
    try {
      var file = new File("Scores.txt");
      var writer = new FileWriter(file);
      for (Pair<String, Integer> p : localScores) {
        String key = p.getKey();
        Integer value = p.getValue();
        String[] s = new String[2];
        s[0] = key;
        s[1] = value.toString();
        writer.write(s[0] + ":" + s[1] + "\n");
      }
      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Display the score
   */
  public void displayScore() {

    var label2 = new Label("Game Over");
    label2.getStyleClass().add("bigtitle");

    var gameOver = new HBox();
    gameOver.getChildren().addAll(label2);
    gameOver.setAlignment(Pos.TOP_CENTER);

    var label3 = new Label("High Scores");
    label3.getStyleClass().add("heading");

    var scoreTitle = new HBox();
    scoreTitle.getChildren().add(label3);

    var label4 = new Label("Local Scores");
    label4.getStyleClass().add("heading");

    var label5 = new Label("Online Scores");
    label5.getStyleClass().add("heading");

    var vBox2 = new VBox();
    vBox2.getChildren().addAll(label4, scoresList);
    label4.setAlignment(Pos.TOP_CENTER);
    scoresList.setAlignment(Pos.TOP_CENTER);

    var vBox3 = new VBox();    //online score
    vBox3.getChildren().addAll(label5, remoteScoreList);
    label5.setAlignment(Pos.TOP_LEFT);
    remoteScoreList.setAlignment(Pos.TOP_RIGHT);

    var hBox1 = new HBox();
    hBox1.getChildren().addAll(vBox2, vBox3);
    hBox1.setAlignment(Pos.CENTER);
    hBox1.setSpacing(100);
    vBox3.setAlignment(Pos.TOP_RIGHT);

    var vBox1 = new VBox();
    vBox1.setSpacing(15);
    vBox1.getChildren().addAll(label2, scoreTitle, hBox1);
    scoresList.displayScore();
    remoteScoreList.displayScore();
    scoreTitle.setAlignment(Pos.TOP_CENTER);
    mainPane.setCenter(vBox1);
    vBox1.setAlignment(Pos.TOP_CENTER);
  }

  /**
   * Process the message
   *
   * @param message the message
   */
  public void receiveCommunication(String message) {
    Platform.runLater(() -> {
      previousMessage = message;
      if (previousMessage.contains("HISCORES")) {
        previousMessage = previousMessage.replace("HISCORES", "");
        String[] s = previousMessage.split("\n");
        for (String s1 : s) {
          String[] s2 = s1.split(":");
          remoteScores.add(new Pair<>(s2[0], Integer.parseInt(s2[1])));
        }
        if (game instanceof MultiplayerGame) {
          logger.info("load the score");
          name = playerName;
        }

        compareWithRemote();

        if ((!local && !remote)) {      // Current < local & remote, for the multiplayer mode, the local must be false, if the remote is also false, display the score
          displayScore();
          return;
        } else if (game instanceof MultiplayerGame) { // Multiplayer mode, current > remote
          saveScore();
          return;
        }
        addScore();                     // Only in single mode
      } else if (previousMessage.contains("NEWSCORE")) {
        logger.info(previousMessage);
      }
    });
  }

  /**
   * Compare the current with the local score
   */
  public void compareWithLocal() {
    if (!(game instanceof MultiplayerGame)) {       // Single mode, the size of local must be 10, thus we can compare directly
      local = game.getScore() >= localScores.getValue().get(9).getValue();      // If current >= local, local = true and vice versa
    } else {            // Multiplayer mode
      local = false;
    }
  }

  /**
   * Compare the current score with the remote score
   */
  public void compareWithRemote() {
    // Size of remote must be 10, thus we can compare them directly
    remote = game.getScore() >= remoteScores.getValue().get(9).getValue();       // If current >= remote, remote = true and vice versa
  }

  /**
   * Load the high score from the server
   */
  public void loadOnlineScores() {
    communicator.send("HISCORES");
  }

  /**
   * Write the high score to the server
   */
  public void writeOnlineScore() {
    communicator.send("HISCORE " + name + ":" + game.getScore());
  }
}