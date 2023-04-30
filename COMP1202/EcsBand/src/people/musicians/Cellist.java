package people.musicians;

import people.Person;
import utils.SoundSystem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A subclass of person, a musician class.
 */
public class Cellist extends Person implements Musician {
  private final List<Integer> notes = new ArrayList<>();
  private Iterator<Integer> nextNote;
  private Integer instrumentID = 43;

  /**
   * A constructor of Cellist, to initialize the Cellist.
   *
   * @param name        name of Cellist
   * @param soundSystem SoundSystem of Cellist
   */
  public Cellist(String name, SoundSystem soundSystem) {
    super(name, soundSystem);
  }

  /**
   * A constructor of Cellist, to initialize the Cellist.
   *
   * @param name name of Cellist
   * @param soundSystem a SoundSystem
   * @param seat the number of seat
   */
  public Cellist(String name, SoundSystem soundSystem, int seat) {
    super(name, soundSystem, seat);
    setSeat(seat);
  }

  /**
   * get the instrumentID of the musician
   *
   * @return the instrumentID of musician
   */
  public Integer getInstrumentID() {
    return instrumentID;
  }

  /**
   * Fetch private variable instrumentID.
   *
   * @return instrumentID of musician.
   */
  @Override
  public int InstrumentID() {
    return getInstrumentID();
  }

  /**
   * Set seat for musician.
   *
   * @param seat the seat number.
   */
  @Override
  public void setSeat(int seat) {
    if (seat >= 0 && seat <= 15) {
      super.setSeat(seat);             // call the method in superclass to set the seat of musician.
      getSs().setInstrument(seat, 43);
    }
  }

  /**
   * To read input note into the properties List<Integer> notes</Integer>
   *
   * @param Notes a list of input notes
   * @param soft  the volume of notes.
   */
  @Override
  public void readScore(int[] Notes, boolean soft) {
    for (int i : Notes) {
      notes.add(i);              // add the notes
    }
    if (soft) {
      setLoudness(50);           // determine the volume
    } else {
      setLoudness(100);
    }
    nextNote = notes.iterator();
  }

  /**
   * Let musician to playing the next note.
   */
  @Override
  public void playNextNote() {
    if (nextNote.hasNext()) {
      getSs().playNote(getSeat(), nextNote.next(), getLoudness());
    }
  }

  /**
   * Fetch the seat number of musician.
   *
   * @return the seat number of musician.
   */
  @Override
  public int getseat() {
    return getSeat();
  }

  /**
   * Call the method in superclass to fetch the private variable name of musician.
   *
   * @return the name of musician
   */
  @Override
  public String getName() {
    return super.getName();
  }
}

