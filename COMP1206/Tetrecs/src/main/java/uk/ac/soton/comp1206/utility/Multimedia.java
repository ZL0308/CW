package uk.ac.soton.comp1206.utility;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The Multimedia to play the music and sound
 */
public class Multimedia {

  /**
   * A logger to log messages and events that occur during the execution of an application.
   */
  private static final Logger logger = LogManager.getLogger(Multimedia.class);

  /**
   * A boolean property
   */
  public static BooleanProperty soundEnabled = new SimpleBooleanProperty(true);

  /**
   * A list to hold whole the player
   */
  public static List<MediaPlayer> soundPlayers = new ArrayList<>();

  /**
   * A reader
   */
  private static BufferedReader reader;

  /**
   * The media player to play the media
   */
  public static MediaPlayer soundPlayer;

  /**
   * The initial volume of the media player
   */
  public static Double a = 0.5;

  /**
   * Play the sound
   *
   * @param file the resource address
   */
  public static void playSound(String file) {
    loadVolume();
    if (!soundEnabled.get()) return;
    String toPlay = Multimedia.class.getResource(file).toExternalForm();
    logger.info("Playing audio" + toPlay);
    try {
      var play = new Media(toPlay);
      soundPlayer = new MediaPlayer(play);
      soundPlayer.setVolume(a);
      soundPlayers.add(soundPlayer);
      soundPlayer.play();
    } catch (Exception e) {
      soundEnabled.set(false);
      e.printStackTrace();
      logger.error("Unable to play audio file");
    }
  }

  /**
   * Play the background music
   *
   * @param file the resource address
   */
  public static void playBGM(String file) {
    loadVolume();
    if (!soundEnabled.get()) return;
    String toPlay = Multimedia.class.getResource(file).toExternalForm();
    logger.info("Playing audio" + toPlay);
    try {
      var play = new Media(toPlay);
      soundPlayer = new MediaPlayer(play);
      soundPlayer.setVolume(a);
      soundPlayers.add(soundPlayer);
      soundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
      soundPlayer.play();
    } catch (Exception e) {
      soundEnabled.set(false);
      e.printStackTrace();
      logger.error("Unable to play audio file");
    }
  }


  /**
   * Stop playing the sound
   */
  public static void stopPlaying() {
    for (MediaPlayer soundPlayer : soundPlayers) {
      soundPlayer.stop();
    }
    soundPlayers.clear();
  }

  /**
   * Set the volume
   *
   * @param volume the volume
   */
  public static void setVolume(double volume) {
    if (soundPlayer != null) {
      soundPlayer.setVolume(volume);
    }
  }

  /**
   * Read a line of data from the config file
   *
   * @return a line of data
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
   * Determine if the file is ready
   *
   * @return a boolean
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
   * Load the volume
   */
  public static void loadVolume() {
    var file = new File("VolumeConfiguration.txt");
    try {
      if (!file.exists()) {
        file.createNewFile();
        recordVolume(a);
        return;
      }
      reader = new BufferedReader(new FileReader(file));
      if (fileIsReady()) {
        String s = getLine();
        a = Double.valueOf(s);
      }
    } catch (Exception e) {
    }
  }

  /**
   * Record the volume
   *
   * @param d the volume
   */
  public static void recordVolume(Double d) {
    try {
      var file = new File("VolumeConfiguration.txt");
      var writer = new FileWriter(file);
      writer.write(d.toString());
      writer.close();
    } catch (Exception e) {
    }
  }
}


