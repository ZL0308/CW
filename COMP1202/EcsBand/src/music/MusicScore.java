package music;

/**
 * A MusicScore class
 */
public class MusicScore {
  private final String instrumentName;
  private final int[] notes;
  private final boolean soft;

  /**
   * A constructor which initialize the instrumentName, the collection of notes, and the volume for each musicians.
   *
   * @param instrumentName the name of instrumentName
   * @param notes          the notes for musician to play
   * @param soft           the volume of musician
   */
  public MusicScore(String instrumentName, int[] notes, boolean soft) {
    this.instrumentName = instrumentName;
    this.notes = notes;
    this.soft = soft;
  }

  /**
   * fetch the instrumentID of each instrument.
   *
   * @return the instrumentID
   */
  public int getInstrumentID() {
    int a = 0;
    switch (instrumentName) {
      case "Piano" -> a = 1;
      case "Cello" -> a = 43;
      case "Violin" -> a = 41;
      default -> System.out.println("Wrong instrument name");
    }
    return a;
  }

  /**
   * fetch the collection of notes for the musicians.
   *
   * @return the collection of notes in a music score.
   */
  public int[] getNotes() {
    return notes;
  }

  /**
   * determine if the volume is soft.
   *
   * @return a boolean which can determine if the volume is soft.
   */
  public boolean isSoft() {
    return soft;
  }
}
