package uk.ac.soton.comp1206.scene;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

import static uk.ac.soton.comp1206.utility.Multimedia.playSound;
import static uk.ac.soton.comp1206.utility.Multimedia.stopPlaying;

/**
 * A Start Scene extends the Base Scene, which display the start video when starting the game.
 */
public class StartScene extends BaseScene {

  /**
   * A logger to log messages and events that occur during the execution of an application.
   */
  private static final Logger logger = LogManager.getLogger(StartScene.class);

  /**
   * Create a new scene, passing in the GameWindow the scene will be displayed in
   *
   * @param gameWindow the game window
   */
  public StartScene(GameWindow gameWindow) {
    super(gameWindow);
  }

  /**
   * Initialize the scene
   */
  @Override
  public void initialise() {
    scene.setOnKeyPressed(this::exitTheGame);
    playSound("/sounds/intro.mp3");
  }

  /**
   * Build the Start Scene
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());
    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var imageAddress = MenuScene.class.getResource("/images/ECSGames.png").toExternalForm();
    var image = new Image(imageAddress);

    var imageView = new ImageView(image);
    imageView.setFitWidth(image.getWidth() / 3);
    imageView.setFitHeight(image.getHeight() / 3);
    imageView.setPreserveRatio(true);

    var logo = new StackPane(imageView);
    logo.setMaxSize(imageView.getFitHeight(), imageView.getFitWidth());

    var background = new StackPane();
    background.setMaxWidth(gameWindow.getWidth());
    background.setMaxHeight(gameWindow.getHeight());
    // Fill the surrounding part of the image
    background.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));

    background.getChildren().add(logo);
    root.getChildren().add(background);

    // Center the logo in the middle of the pane
    StackPane.setAlignment(imageView, Pos.CENTER);
    StackPane.setAlignment(logo, Pos.CENTER);

    FadeTransition fadeIn = new FadeTransition(Duration.seconds(3), imageView);       // time to fade in to the image
    FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), imageView);      // time to fade out to the image

    fadeIn.setFromValue(0);
    fadeIn.setToValue(1);
    fadeOut.setFromValue(1);
    fadeOut.setToValue(0);
    fadeIn.setOnFinished(e -> fadeOut.play());
    fadeOut.setOnFinished(event -> gameWindow.startMenu());       // after fade out, jump to start menu
    fadeIn.play();
  }

  /**
   * Exit the game
   *
   * @param event key event
   */
  public void exitTheGame(KeyEvent event) {
    stopPlaying();
    gameWindow.getStage().close();
  }

}
