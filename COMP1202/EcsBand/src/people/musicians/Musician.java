package people.musicians;

/**
 * An interface of musician
 */
public interface Musician {

  /**
   * tell musician what they are sitting in.
   *
   * @param seat the seat number of musician.
   */
  void setSeat(int seat);

  /**
   * read the input by an array of music notes, and the volume of musician.
   *
   * @param notes the notes that musician should play.
   * @param soft  the volume of musician when performing.
   */
  void readScore(int[] notes, boolean soft);

  /**
   * let musician to playing next note.
   */
  void playNextNote();

  /**
   * Fetch private variable instrumentID.
   *
   * @return instrumentID of musician.
   */
  int InstrumentID();

  /**
   * Fetch the seat number of musician.
   *
   * @return the seat number of musician.
   */
  int getseat();

  /**
   * Call the method in superclass to fetch the private variable name of musician.
   *
   * @return the name of musician。
   */
  String getName();
}

