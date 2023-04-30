package uk.ac.soton.comp1206.scene;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;

import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

import java.io.*;

import static uk.ac.soton.comp1206.utility.Multimedia.playSound;
import static uk.ac.soton.comp1206.utility.Multimedia.stopPlaying;

/**
 * The extension part, a Setting Scene to change the volume, wall paper and the brightness
 */
public class SettingScene extends BaseScene {

  /**
   * A reader to read the config from the file
   */
  private BufferedReader reader;

  /**
   * The volume of the game
   */
  private double volume;

  /**
   * The brightness of the game
   */
  private double brightness;


  /**
   * Create a new scene, passing in the GameWindow the scene will be displayed in
   *
   * @param gameWindow the game window
   */
  public SettingScene(GameWindow gameWindow) {
    super(gameWindow);
  }

  /**
   * Initialize the Setting Scene
   */
  @Override
  public void initialise() {
    playSound("/music/menu.mp3");
    scene.setOnKeyPressed(this::exit);
  }

  /**
   * Build the Challenge window
   */
  @Override
  public void build() {
    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var settingPane = new StackPane();
    settingPane.setMaxHeight(gameWindow.getHeight());

    if (gameWindow.getBoolean()) {
      settingPane.getStyleClass().add("wallpaper");
    } else {
      settingPane.getStyleClass().add("menu-background");
    }
    root.getChildren().add(settingPane);

    var mainPane = new BorderPane();            // Use a border pane to adjust the format
    settingPane.getChildren().add(mainPane);

    // Add the button to switch the wall paper
    var button1 = new Button("WallPaper");
    button1.setPrefSize(150, 60);

    button1.setOnAction(event -> {
      gameWindow.changeBackground();

      if (gameWindow.getBoolean()) {
        settingPane.getStyleClass().remove("menu-background");
        settingPane.getStyleClass().add("wallpaper");
      } else {
        settingPane.getStyleClass().remove("wallpaper");
        settingPane.getStyleClass().add("menu-background");
      }
      recordWallPaper(gameWindow.getBoolean());
    });

    // Add the volume slider and brightness slider to adjust the volume and brightness
    loadBrightness();
    loadVolume();
    var volumeSlider = new Slider(0, 1, volume);        // Set the slider
    var brightnessSlider = new Slider(-0.8, 0.8, brightness);
    var colorAdjust = new ColorAdjust();
    var label1 = new Label();
    var label2 = new Label();
    label1.setMaxSize(100, 30);
    label2.setMaxSize(100, 30);
    var label3 = new Label("Volume");
    var label4 = new Label("Brightness");
    label1.getStyleClass().add("heading");
    label2.getStyleClass().add("heading");
    label3.getStyleClass().add("heading");
    label4.getStyleClass().add("heading");

    label1.textProperty().bind(volumeSlider.valueProperty().asString("%.1f"));      // Display the volume
    label2.textProperty().bind(brightnessSlider.valueProperty().asString("%.1f"));  // Display the brightness

    colorAdjust.setBrightness(gameWindow.getTheBrightness());                          // Set the brightness

    volumeSlider.setOrientation(Orientation.VERTICAL);
    brightnessSlider.setOrientation(Orientation.VERTICAL);
    volumeSlider.setMaxHeight(150);
    brightnessSlider.setMaxHeight(150);

    volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
      Multimedia.setVolume(newValue.doubleValue());                                    // Set the new volume
      recordVolume(newValue.doubleValue());
    });

    brightnessSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
      colorAdjust.setBrightness(newValue.doubleValue());                                // Set the new brightness
      gameWindow.setTheBrightness(newValue.doubleValue());
      recordBrightness(newValue.doubleValue());
    });

    mainPane.setEffect(colorAdjust);
    settingPane.setEffect(colorAdjust);

    var box = new HBox();
    var box1 = new VBox();
    var box2 = new VBox();
    box.setSpacing(40);
    box1.setSpacing(40);
    box2.setSpacing(40);
    box1.getChildren().addAll(volumeSlider, label1);
    box2.getChildren().addAll(brightnessSlider, label2);
    box.getChildren().addAll(button1, box1, label3, box2, label4);
    mainPane.setCenter(box);
    box.setAlignment(Pos.CENTER);
    box1.setAlignment(Pos.CENTER);
    box2.setAlignment(Pos.CENTER);
    label3.setAlignment(Pos.CENTER);
    label4.setAlignment(Pos.CENTER);
  }

  /**
   * Exit the Setting Scene
   *
   * @param event the key event
   */
  public void exit(KeyEvent event) {
    if (event.getCode() != KeyCode.ESCAPE) return;
    stopPlaying();
    gameWindow.startMenu();
  }

  /**
   * Record the current volume
   *
   * @param d volume
   */
  public void recordVolume(Double d) {
    try {
      var file = new File("VolumeConfiguration.txt");
      var writer = new FileWriter(file);
      writer.write(d.toString());
      writer.close();
    } catch (Exception e) {
    }
  }

  /**
   * Read the data from the config file
   *
   * @return a line of data
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
   * Load the volume from the config file
   */
  public void loadVolume() {
    var file = new File("VolumeConfiguration.txt");
    try {
      reader = new BufferedReader(new FileReader(file));
      if (fileIsReady()) {
        String s = getLine();
        volume = Double.valueOf(s);
      }
    } catch (Exception e) {
    }
  }

  /**
   * Record the wall paper
   *
   * @param b a boolean
   */
  public void recordWallPaper(Boolean b) {
    try {
      var file = new File("WallPaperConfiguration.txt");
      var writer = new FileWriter(file);
      writer.write(b.toString());
      writer.close();
    } catch (Exception e) {
    }
  }

  /**
   * Record the brightness
   *
   * @param d the brightness
   */
  public void recordBrightness(Double d) {
    try {
      var file = new File("BrightnessConfiguration.txt");
      var writer = new FileWriter(file);
      writer.write(d.toString());
      writer.close();
    } catch (Exception e) {
    }
  }

  /**
   * Load the brightness from the config file
   */
  public void loadBrightness() {
    var file = new File("BrightnessConfiguration.txt");
    try {
      reader = new BufferedReader(new FileReader(file));
      if (fileIsReady()) {
        String s = getLine();
        brightness = Double.parseDouble(s);
      }
    } catch (Exception e) {
    }
  }

}
