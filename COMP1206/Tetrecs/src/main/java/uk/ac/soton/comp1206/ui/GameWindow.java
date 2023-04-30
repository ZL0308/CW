package uk.ac.soton.comp1206.ui;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.scene.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * The GameWindow is the single window for the game where everything takes place. To move between screens in the game,
 * we simply change the scene.
 * <p>
 * The GameWindow has methods to launch each of the different parts of the game by switching scenes. You can add more
 * methods here to add more screens to the game.
 */
public class GameWindow {

  /**
   * A logger to log messages and events that occur during the execution of an application.
   */
  private static final Logger logger = LogManager.getLogger(GameWindow.class);

  /**
   * The width of the window
   */
  private final int width;

  /**
   * The height of the window
   */
  private final int height;

  /**
   * The stage
   */
  private final Stage stage;

  /**
   * The Base Scene
   */
  private BaseScene currentScene;

  /**
   * The scene
   */
  private Scene scene;

  /**
   * The communicator
   */
  protected final Communicator communicator;

  /**
   * A boolean flag to change the wall paper
   */
  private boolean changeWallPaper = false;

  /**
   * The initial brightness
   */
  private static double brightness = 0;

  /**
   * A reader to read the data from the config file
   */
  private static BufferedReader reader;


  /**
   * Create a new GameWindow attached to the given stage with the specified width and height
   *
   * @param stage  stage
   * @param width  width
   * @param height height
   */
  public GameWindow(Stage stage, int width, int height) {
    this.width = width;
    this.height = height;

    this.stage = stage;
    loadBrightness();
    loadWallPaper();

    //Setup window
    setupStage();

    //Setup resources
    setupResources();

    //Setup default scene
    setupDefaultScene();

    //Setup communicator
    communicator = new Communicator("ws://ofb-labs.soton.ac.uk:9700");

    //Go to menu
//        startMenu();
    startAnimation();
  }

  /**
   * Setup the font and any other resources we need
   */
  private void setupResources() {
    logger.info("Loading resources");

    //We need to load fonts here due to the Font loader bug with spaces in URLs in the CSS files
    Font.loadFont(getClass().getResourceAsStream("/style/Orbitron-Regular.ttf"), 32);
    Font.loadFont(getClass().getResourceAsStream("/style/Orbitron-Bold.ttf"), 32);
    Font.loadFont(getClass().getResourceAsStream("/style/Orbitron-ExtraBold.ttf"), 32);
  }

  /**
   * Start the animation when the game start
   */
  public void startAnimation() {
    loadScene(new StartScene(this));
  }

  /**
   * Display the main menu
   */
  public void startMenu() {
    loadScene(new MenuScene(this));
  }

  /**
   * Display the single player challenge
   */
  public void startChallenge() {
    loadScene(new ChallengeScene(this));
  }

  /**
   * Setup the default settings for the stage itself (the window), such as the title and minimum width and height.
   */
  public void setupStage() {
    stage.setTitle("TetrECS");
    stage.setMinWidth(width);
    stage.setMinHeight(height + 20);
    stage.setOnCloseRequest(ev -> App.getInstance().shutdown());
  }

  /**
   * Load a given scene which extends BaseScene and switch over.
   *
   * @param newScene new scene to load
   */
  public void loadScene(BaseScene newScene) {
    //Cleanup remains of the previous scene
    cleanup();

    //Create the new scene and set it up
    newScene.build();
    currentScene = newScene;
    scene = newScene.setScene();
    stage.setScene(scene);

    //Initialise the scene when ready
    Platform.runLater(() -> currentScene.initialise());
  }

  /**
   * Setup the default scene (an empty black scene) when no scene is loaded
   */
  public void setupDefaultScene() {
    this.scene = new Scene(new Pane(), width, height, Color.BLACK);
    stage.setScene(this.scene);
  }

  /**
   * When switching scenes, perform any cleanup needed, such as removing previous listeners
   */
  public void cleanup() {
    logger.info("Clearing up previous scene");
    communicator.clearListeners();
  }

  /**
   * Get the current scene being displayed
   *
   * @return scene
   */
  public Scene getScene() {
    return scene;
  }

  /**
   * Get the width of the Game Window
   *
   * @return width
   */
  public int getWidth() {
    return this.width;
  }

  /**
   * Get the height of the Game Window
   *
   * @return height
   */
  public int getHeight() {
    return this.height;
  }

  /**
   * Get the communicator
   *
   * @return communicator
   */
  public Communicator getCommunicator() {
    return communicator;
  }

  /**
   * Start the Instruction Scene
   */
  public void startInstruction() {
    loadScene(new InstructionsScene(this));
  }

  /**
   * Start the Score Scene
   *
   * @param game the game
   */
  public void startScoreScene(Game game) {
    loadScene(new ScoresScene(this, game));
  }

  /**
   * Start the Score Scene in multiplayer mode
   *
   * @param game the game
   * @param name the name of the player
   * @param list the score list
   */
  public void startScoreScene(Game game, String name, SimpleListProperty<Pair<String, Integer>> list) {
    loadScene(new ScoresScene(this, game, name, list));
  }

  /**
   * Start the Multiplayer mode
   */
  public void startMultiplayerGame() {
    loadScene(new LobbyScene(this));
  }

  /**
   * Start The Multiplayer Competition
   *
   * @param name the name
   */
  public void startMultiplayerCompetition(String name) {
    loadScene(new MultiplayerScene(this, name));
  }

  /**
   * Get the stage
   *
   * @return the stage
   */
  public Stage getStage() {
    return stage;
  }

  /**
   * Start the Setting Scene
   */
  public void startSetting() {
    loadScene(new SettingScene(this));
  }

  /**
   * Change the wall paper
   */
  public void changeBackground() {
    changeWallPaper = !changeWallPaper;
  }

  /**
   * Get the boolean flag of wall paper changing
   *
   * @return the boolean flag
   */
  public boolean getBoolean() {
    return changeWallPaper;
  }

  /**
   * Set the brightness of the game
   *
   * @param d the brightness
   */
  public void setTheBrightness(Double d) {
    brightness = d;
  }

  /**
   * Get the brightness
   *
   * @return the brightness
   */
  public Double getTheBrightness() {
    return brightness;
  }

  /**
   * Read the data from the config file
   *
   * @return get a line
   */
  public static String getLine() {
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
   * Determine if the config file is ready
   *
   * @return the boolean
   */
  public static boolean fileIsReady() {
    try {
      return reader.ready();
    } catch (RuntimeException | IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Load the brightness
   */
  public static void loadBrightness() {
    var file = new File("BrightnessConfiguration.txt");
    try {
      if (!file.exists()) file.createNewFile();
      reader = new BufferedReader(new FileReader(file));
      if (fileIsReady()) {
        String s = getLine();
        brightness = Double.valueOf(s);
      }
    } catch (Exception e) {
    }
  }

  /**
   * Load the wall paper
   */
  public void loadWallPaper() {
    var file = new File("WallPaperConfiguration.txt");
    try {
      if (!file.exists()) file.createNewFile();
      reader = new BufferedReader(new FileReader(file));
      if (fileIsReady()) {
        String s = getLine();
        if (s.contains("true")) changeWallPaper = true;
        if (s.contains("false")) changeWallPaper = false;
      }
    } catch (Exception e) {
    }
  }
}
