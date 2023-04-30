package music;

import java.util.List;

/**
 * An interface of MusicSheet.
 */
public interface Composition {

  /**
   * Get the name of composition.
   *
   * @return the name of composition.
   */
  String getName();

  /**
   * Add the music score to the collection of music score.
   *
   * @param instrumentName the name of instrument.
   * @param notes          the notes for musicians to play.
   * @param soft           the volume of musicians when they are playing.
   */
  void addScore(String instrumentName, List<String> notes, boolean soft);

  /**
   * To get a collection of music score.
   *
   * @return a collection of music score.
   */
  MusicScore[] getScores();

  /**
   * Get the length of the notes.
   *
   * @return the size of notes.
   */
  int getLength();

  /**
   * Fetch the playing length of each note
   *
   * @return the length of each note.
   */
  int getNoteLength();
}
