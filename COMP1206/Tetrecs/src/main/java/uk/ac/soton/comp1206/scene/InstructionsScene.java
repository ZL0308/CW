package uk.ac.soton.comp1206.scene;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import static uk.ac.soton.comp1206.utility.Multimedia.*;

/**
 * An Instruction Scene extends Base Scene, which show the game instructions.
 */
public class InstructionsScene extends BaseScene {

  /**
   * A logger to log messages and events that occur during the execution of an application.
   */
  private static final Logger logger = LogManager.getLogger(InstructionsScene.class);

  /**
   * Create a new scene, passing in the GameWindow the scene will be displayed in
   *
   * @param gameWindow the game window
   */
  public InstructionsScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating Instruction Scene");
  }

  /**
   * Initialize the scene
   */
  @Override
  public void initialise() {
    playBGM("/music/menu.mp3");
    logger.info("Initialising Instruction Scene");
    scene.setOnKeyPressed((keyEvent -> {
      if (keyEvent.getCode() != KeyCode.ESCAPE) return;
      stopPlaying();
      gameWindow.startMenu();
    }));
  }

  /**
   * Build the Instruction Scene
   */
  @Override
  public void build() {
    logger.info("Building " + this.getClass().getName());
    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var instructionPane = new StackPane();
    instructionPane.setMaxWidth(gameWindow.getWidth());
    instructionPane.setMaxHeight(gameWindow.getHeight());
    if (gameWindow.getBoolean()) {
      instructionPane.getStyleClass().add("wallpaper");
    } else {
      instructionPane.getStyleClass().add("menu-background");
    }
    root.getChildren().add(instructionPane);


    var label1 = new Label("Instruction");
    label1.getStyleClass().add("heading");

    var imageBox = new HBox();
    imageBox.setPrefWidth(550);
    imageBox.setPrefHeight(300);
    String imageAddress = getAddress("Instructions.png");
    ImageView imageView = new ImageView(imageAddress);
    imageView.setFitWidth(imageBox.getPrefWidth());
    imageView.setFitHeight(imageBox.getPrefHeight());
    imageBox.getChildren().add(imageView);
    imageBox.setAlignment(Pos.CENTER);

    var paneBox = new HBox();
    var piecePane = new GridPane();
    piecePane = setPiece(piecePane);
    piecePane.setHgap(10);
    piecePane.setVgap(10);
    paneBox.getChildren().add(piecePane);
    paneBox.setAlignment(Pos.CENTER);

    var label2 = new Label("Game Pieces");
    label2.getStyleClass().add("heading");

    var box = new VBox();
    box.setSpacing(10);
    box.getChildren().addAll(label1, imageBox, label2, paneBox);
    box.setAlignment(Pos.CENTER);
    instructionPane.getChildren().add(box);
    StackPane.setAlignment(box, Pos.CENTER);

    var colorAdjust = new ColorAdjust();
    colorAdjust.setBrightness(gameWindow.getTheBrightness());
    instructionPane.setEffect(colorAdjust);
  }

  /**
   * Fetch the address of the image.
   *
   * @param address the address of the image
   * @return the full address
   */
  public String getAddress(String address) {
    return InstructionsScene.class.getResource("/images/" + address).toExternalForm();
  }

  /**
   * Display all the game piece
   *
   * @param pane tbe pane
   * @return the pane
   */
  public GridPane setPiece(GridPane pane) {
    for (int i = 0; i < 15; i++) {
      var pieceBoard = new PieceBoard(3, 3, 60, 60);
      var piece = GamePiece.createPiece(i);
      pieceBoard.setPiece(piece);
      pane.add(pieceBoard, i % 5, i / 5);         // Generate a 3*5 format
    }
    pane.getStyleClass().add("gamepane");
    return pane;
  }
}
