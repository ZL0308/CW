package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.time.LocalTime;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static uk.ac.soton.comp1206.utility.Multimedia.playSound;
import static uk.ac.soton.comp1206.utility.Multimedia.stopPlaying;

/**
 * A Lobby Scene extends the Base Scene, which is the lobby scene of the multiplayer mode.
 */
public class LobbyScene extends BaseScene {

  /**
   * A logger to log messages and events that occur during the execution of an application.
   */
  private static final Logger logger = LogManager.getLogger(LobbyScene.class);

  /**
   * A border pane as main pane.
   */
  private BorderPane mainPane;

  /**
   * A communicator takes responsible to communicate with the server.
   */
  private Communicator communicator;

  /**
   * A Vbox as a channel box.
   */
  private VBox channelBox;

  /**
   * A string to store the channel name.
   */
  private String channelName;

  /**
   * A text field to store the content the inputted name.
   */
  private TextField nameInput;

  /**
   * A string to store the message.
   */
  private String previousMessage;

  /**
   * A HBox to contain the player.
   */
  private HBox playerBox;

  /**
   * A right box as the chatting window.
   */
  private VBox rightBox;

  /**
   * A VBox as a message box.
   */
  private VBox messageBox;

  /**
   * A string to store the nickname.
   */
  private String nickName;

  /**
   * A button to start the game.
   */
  private Button startButton;

  /**
   * A schedule to update the channel list
   */
  private ScheduledExecutorService scheduler;

  /**
   * My nickname
   */
  private String theNickName;

  /**
   * Create a new scene, passing in the GameWindow the scene will be displayed in
   *
   * @param gameWindow the game window
   */
  public LobbyScene(GameWindow gameWindow) {
    super(gameWindow);
  }

  /**
   * Initialize the Lobby Scene
   */
  @Override
  public void initialise() {
    playSound("/music/menu.mp3");
    communicator = gameWindow.getCommunicator();
    communicator.addListener(this::receiveCommunication);
    scene.setOnKeyPressed(this::quit);
    scheduler = new ScheduledThreadPoolExecutor(1);
    scheduler.scheduleAtFixedRate(() -> {
      communicator.send("LIST");                // Request the channel list
    }, 0, 1, TimeUnit.SECONDS);
  }

  /**
   * Build the Challenge window
   */
  @Override
  public void build() {
    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var lobbyPane = new StackPane();
    lobbyPane.setMaxWidth(gameWindow.getWidth());
    lobbyPane.setMaxHeight(gameWindow.getHeight());
    if (gameWindow.getBoolean()) {
      lobbyPane.getStyleClass().add("wallpaper");
    } else {
      lobbyPane.getStyleClass().add("menu-background");
    }
    root.getChildren().add(lobbyPane);

    mainPane = new BorderPane();
    lobbyPane.getChildren().add(mainPane);

    VBox leftBox = new VBox();
    leftBox.setPrefSize(300, 800);

    channelBox = new VBox();

    var scrollPane = new ScrollPane();    // The scroll pane
    scrollPane.setPrefViewportWidth(230);
    scrollPane.setPrefViewportHeight(450);
    scrollPane.getStyleClass().add("scroller");
    scrollPane.setContent(channelBox);

    var title = new Label("Multiplayer");
    title.getStyleClass().add("title");
    var titleBox = new HBox();
    titleBox.getChildren().add(title);
    title.setAlignment(Pos.TOP_CENTER);
    titleBox.setAlignment(Pos.TOP_CENTER);
    mainPane.setTop(titleBox);

    var label1 = new Label("Current Games");
    var button = new Button("Host New Game");
    var buttonBox = new HBox();
    var labelBox = new HBox();
    labelBox.getChildren().add(label1);
    labelBox.setAlignment(Pos.TOP_CENTER);
    label1.getStyleClass().add("heading");
    label1.setAlignment(Pos.TOP_CENTER);

    buttonBox.getChildren().add(button);
    button.setAlignment(Pos.TOP_CENTER);

    VBox inputBox = new VBox();
    inputBox.getChildren().add(buttonBox);
    buttonBox.setAlignment(Pos.TOP_CENTER);

    var nameBox = new HBox();
    nameInput = new TextField();
    nameBox.getChildren().add(nameInput);
    HBox.setHgrow(nameInput, Priority.ALWAYS);
    inputBox.getChildren().add(nameBox);
    nameBox.setAlignment(Pos.TOP_CENTER);
    nameBox.setVisible(false);

    button.setOnAction((event) -> {
      nameBox.setVisible(true);
      nameInput.setOnKeyPressed((actionEvent) -> {
        if (actionEvent.getCode() != KeyCode.ENTER) return;
        channelName = nameInput.getText();
        communicator.send("CREATE\n" + channelName);
        nameInput.clear();
        nameBox.setVisible(false);
        playSound("/sounds/pling.wav");
      });
    });
    leftBox.getChildren().addAll(labelBox, inputBox, scrollPane);
    mainPane.setLeft(leftBox);

    var colorAdjust = new ColorAdjust();
    colorAdjust.setBrightness(gameWindow.getTheBrightness());
    mainPane.setEffect(colorAdjust);
    lobbyPane.setEffect(colorAdjust);
  }

  /**
   * Process the message received by communicator
   *
   * @param message the message
   */
  public void receiveCommunication(String message) {
    Platform.runLater(() -> {
      previousMessage = message;
      if (previousMessage.contains("CHANNELS")) {         // Get channel list
        channelBox.getChildren().clear();
        previousMessage = previousMessage.replace("CHANNELS", "");
        String[] s = previousMessage.split("\n");
        for (String s1 : s) {
          s1 = s1.trim();
          var channel = new Text(s1);
          channel.getStyleClass().add("heading");
          channelBox.getChildren().add(channel);
          channel.setOnMouseClicked((event -> {             // Once click a channel, join it
            communicator.send("JOIN " + channel.getText());
            channelName = channel.getText();                // Update the channel name
          }));
        }
      } else if (message.contains("JOIN")) {
        communicator.send("USERS");                     // Join a channel
        createChatBox();                                // Create the chat box
      } else if (message.contains("USERS")) {           // Load the users
        String[] s = previousMessage.replace("USERS", "").split(" ");
        playerBox.getChildren().clear();
        for (String s1 : s) {
          var name = new Text(s1);
          name.getStyleClass().add("heading");
          playerBox.getChildren().add(name);
        }
      } else if (previousMessage.contains("MSG")) {     // Receive the message
        if (previousMessage.contains("/nick")) return;  // Only display the chatting message
        String[] s = previousMessage.replace("MSG", "").trim().split(":");
        String name = s[0];
        String userMessage = s[1];
        var minute = String.format("%02d", LocalTime.now().getMinute());  // Add the message time
        var text = new Text("[" + LocalTime.now().getHour() + ":" + minute + "]" + " <" + name + "> : " + userMessage);
        text.wrappingWidthProperty().bind(messageBox.widthProperty());
        messageBox.getChildren().add(text);
        text.getStyleClass().add("heading");
        playSound("/sounds/message.wav");
      } else if (previousMessage.contains("HOST")) {        // If the user is the host, user can start the game
        startButton.setVisible(true);
      } else if (previousMessage.contains("ERROR")) {
        logger.info(previousMessage);                 // Process the error
        startErrorWindow();                 // Handle the error
      } else if (previousMessage.contains("NICK") && !previousMessage.contains(":")) {
        String s = previousMessage.replace("NICK", "").trim();
        if (s.contains(theNickName)) {
          nickName = s;                              // Update the nickname
        }
      } else if (previousMessage.contains("START")) {
        startCompetition();
      } else if (previousMessage.contains("PARTED")) {  // Quit the channel
        mainPane.getChildren().remove(rightBox);
      }
    });
  }

  /**
   * Create a chat box
   */
  public void createChatBox() {
    rightBox = new VBox();

    var channelID = new Label(channelName);
    channelID.getStyleClass().add("heading");

    var chatBox = new VBox();
    playerBox = new HBox();       // Display whole the player in a channel

    var instruction = new Text("Welcome to the lobby \nType /nick NewName to change your name");
    instruction.getStyleClass().add("heading");

    var inputField = new TextField();
    inputField.setPromptText("Send a mesasage");
    inputField.setAlignment(Pos.BOTTOM_CENTER);

    messageBox = new VBox();    // Display the message content

    var scrollPane = new ScrollPane();    // The scroll pane
    scrollPane.setFitToWidth(true);
    scrollPane.setPrefViewportHeight(250);
    scrollPane.getStyleClass().add("scroller");
    scrollPane.setContent(messageBox);

    inputField.setOnKeyPressed((keyEvent -> {
      if (keyEvent.getCode() != KeyCode.ENTER) return;
      if (inputField.getText().contains("/nick")) {           // the name which was inputted by the user
        theNickName = inputField.getText().replace("/nick", "").trim();
        communicator.send("NICK " + theNickName);
        playSound("/sounds/pling.wav");
      }
      communicator.send("MSG " + inputField.getText());
      inputField.clear();         // After sending message, clear the input field
    }));

    startButton = new Button("Start game");
    var leaveButton = new Button("Leave game");
    startButton.setVisible(false);

    startButton.setOnAction((event) -> {
      communicator.send("START");
    });

    leaveButton.setOnAction((event -> {
      communicator.send("PART");
    }));

    var gameButtonBox = new HBox();
    gameButtonBox.getChildren().addAll(startButton, leaveButton);
    gameButtonBox.setSpacing(130);
    gameButtonBox.setAlignment(Pos.BOTTOM_CENTER);

    chatBox.getChildren().addAll(playerBox, instruction, scrollPane, inputField, gameButtonBox);
    chatBox.setSpacing(20);
    chatBox.getStyleClass().add("gameBox");

    rightBox.getChildren().addAll(channelID, chatBox);
    mainPane.setCenter(rightBox);
    rightBox.setTranslateX(-15);
  }

  /**
   * Start the competition
   */
  private void startCompetition() {
    stopPlaying();
    scheduler.close();
    gameWindow.startMultiplayerCompetition(nickName);
  }

  /**
   * Quit the scene
   *
   * @param event key event
   */
  private void quit(KeyEvent event) {
    if (event.getCode() != KeyCode.ESCAPE) return;
    scheduler.close();
    stopPlaying();
    gameWindow.startMenu();
  }

  /**
   * Handle the error
   */
  public void startErrorWindow() {

    var box = new HBox();
    box.setAlignment(Pos.CENTER);
    box.getChildren().addAll(new Label("You're already in a channel."));

    var errorWindow = new Stage();       // Create a new stage for the window
    errorWindow.setScene(new Scene(box, 400, 250));      // Set the scene for the window
    errorWindow.show();
  }
}
