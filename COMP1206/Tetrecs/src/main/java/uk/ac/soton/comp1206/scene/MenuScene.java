package uk.ac.soton.comp1206.scene;

import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import static uk.ac.soton.comp1206.utility.Multimedia.playSound;
import static uk.ac.soton.comp1206.utility.Multimedia.stopPlaying;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

  /**
   * A logger to log messages and events that occur during the execution of an application.
   */
  private static final Logger logger = LogManager.getLogger(MenuScene.class);

  /**
   * A communicator takes responsible to communicate with the server.
   */
  private Communicator communicator;

  /**
   * Create a new menu scene
   *
   * @param gameWindow the Game Window this will be displayed in
   */
  public MenuScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Menu Scene");
  }

  /**
   * Build the menu layout
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var menuPane = new StackPane();
    menuPane.setMaxWidth(gameWindow.getWidth());
    menuPane.setMaxHeight(gameWindow.getHeight());
    if (gameWindow.getBoolean()) {        // Load the wall paper
      menuPane.getStyleClass().add("wallpaper");
    } else {
      menuPane.getStyleClass().add("menu-background");
    }
    root.getChildren().add(menuPane);

    var mainPane = new BorderPane();
    menuPane.getChildren().add(mainPane);

    var image = new Image(MenuScene.class.getResource("/images/TetrECS.png").toExternalForm());

    var imageView = new ImageView(image);         // Add the logo
    var imageBox = new HBox();
    imageBox.setPrefWidth(600);
    imageBox.setPrefHeight(130);
    imageView.setFitWidth(imageBox.getPrefWidth());
    imageView.setFitHeight(imageBox.getPrefHeight());
    imageBox = new HBox(imageView);
    imageBox.setAlignment(Pos.CENTER);

    var rotateTransition = new RotateTransition(Duration.seconds(4), imageBox);    // Set rotate transition duration
    rotateTransition.setFromAngle(-10);
    rotateTransition.setToAngle(10);
    rotateTransition.setCycleCount(RotateTransition.INDEFINITE);
    rotateTransition.setAutoReverse(true);

    var scaleTransition = new ScaleTransition(Duration.seconds(2), imageBox);       // Set scale transition duration
    scaleTransition.setFromX(1);
    scaleTransition.setToX(1.3);      // Set the size
    scaleTransition.setCycleCount(ScaleTransition.INDEFINITE);
    scaleTransition.setAutoReverse(true);

    rotateTransition.play();
    scaleTransition.play();

    mainPane.setTop(imageBox);
    BorderPane.setMargin(imageBox, new Insets(70, 0, 0, 0));

    var box = new VBox();
    box.setAlignment(Pos.CENTER);
    box.setSpacing(20);
    box.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

    var button1 = new Button("Single Player");
    button1.getStyleClass().add("button");
    button1.setPrefSize(200, 60);

    var button2 = new Button("Multi Player");
    button2.getStyleClass().add("button");
    button2.setPrefSize(200, 60);

    var button3 = new Button("How to play");
    button3.getStyleClass().add("button");
    button3.setPrefSize(200, 60);

    var button4 = new Button("Exit");
    button4.getStyleClass().add("button");
    button4.setPrefSize(200, 60);

    box.getChildren().addAll(button1, button2, button3, button4);
    mainPane.setCenter(box);

    button1.setOnAction(this::startGame);
    button2.setOnAction(this::startMultipleGame);
    button3.setOnAction(this::loadInInstruction);
    button4.setOnAction(this::quitTheGame);

    // Add a setting button
    var buttonImage = new Image(MenuScene.class.getResource("/images/gear.png").toExternalForm());
    var buttonImageView = new ImageView(buttonImage);
    buttonImageView.setFitHeight(30);
    buttonImageView.setFitWidth(30);
    var button5 = new Button();
    button5.setPrefSize(30, 30);
    button5.setGraphic(buttonImageView);
    button5.setOnAction(this::startSetting);
    mainPane.setBottom(button5);
    BorderPane.setAlignment(button5, Pos.BOTTOM_RIGHT);
    BorderPane.setMargin(button5, new Insets(15));

    // set brightness
    var colorAdjust = new ColorAdjust();
    colorAdjust.setBrightness(gameWindow.getTheBrightness());
    mainPane.setEffect(colorAdjust);
    menuPane.setEffect(colorAdjust);
  }


  /**
   * Initialise the menu
   */
  @Override
  public void initialise() {
    stopPlaying();
    playSound("/music/menu.mp3");
    communicator = gameWindow.getCommunicator();
    scene.setOnKeyPressed(this::quitTheGame);
  }

  /**
   * Handle when the Start Game button is pressed
   *
   * @param event event
   */
  private void startGame(ActionEvent event) {
    stopPlaying();
    playSound("/sounds/lifegain.wav");
    gameWindow.startChallenge();
  }

  /**
   * Load in the instruction scene
   *
   * @param event an action event
   */
  private void loadInInstruction(ActionEvent event) {
    stopPlaying();
    playSound("/sounds/lifegain.wav");
    gameWindow.startInstruction();
  }

  /**
   * Start a multiplayer game
   *
   * @param event an action event
   */
  private void startMultipleGame(ActionEvent event) {
    stopPlaying();
    playSound("/sounds/lifegain.wav");
    gameWindow.startMultiplayerGame();
  }

  /**
   * Quit the game
   *
   * @param event an action event
   */
  private void quitTheGame(ActionEvent event) {
    communicator.send("QUIT");
    gameWindow.getStage().close();
  }

  /**
   * Set the key event to quit the game
   *
   * @param event a key event
   */
  private void quitTheGame(KeyEvent event) {
    if (event.getCode() != KeyCode.ESCAPE) return;
    communicator.send("QUIT");
    gameWindow.getStage().close();
  }

  /**
   * Load in the start scene
   *
   * @param event an action event
   */
  private void startSetting(ActionEvent event) {
    stopPlaying();
    playSound("/sounds/lifegain.wav");
    gameWindow.startSetting();
  }
}