package people;

import utils.SoundSystem;

/**
 * A super class of musicians, to avoid code duplication.
 */
public class Person {

  private String name;
  private SoundSystem soundSystem;
  private int seat;
  private int loudness;

  /**
   * A constructor to initialize the name of person.
   *
   * @param name the name of each musician.
   */
  public Person(String name) {
    this.name = name;
  }

  /**
   * A constructor to initialize the name, the SoundSystem of the person.
   *
   * @param name name of person
   * @param soundSystem a SoundSystem
   */
  public Person(String name, SoundSystem soundSystem){
    this.name = name;
    this.soundSystem = soundSystem;
  }

  /**
   * A constructor to initialize the name, soundSystem and the instrumentID.
   *
   * @param name         the name of each musician
   * @param soundSystem  a SoundSystem
   * @param seat the number of seat of each musician
   */
  public Person(String name, SoundSystem soundSystem, int seat) {
    this.name = name;
    this.soundSystem = soundSystem;
    this.seat = seat;
  }

  /**
   * fetch the name of musician.
   *
   * @return the name of musician.
   */
  public String getName() {
    return name;
  }

  /**
   * get the soundSystem
   *
   * @return the SoundSystem
   */
  public SoundSystem getSs() {
    return soundSystem;
  }

  /**
   * arrange the seat number of each musician
   *
   * @param seat seat number of musician in orchestra
   */
  public void setSeat(int seat) {
    this.seat = seat;
  }

  /**
   * get the seat number of musician.
   *
   * @return seat number of musician.
   */
  public int getSeat() {
    return seat;
  }

  /**
   * Set the Loudness of each musician.
   *
   * @param loudness the volume of each musician when they are playing.
   */
  public void setLoudness(int loudness) {
    this.loudness = loudness;
  }

  /**
   * fetch the Loudness of each musician.
   *
   * @return the volume of each musician.
   */
  public int getLoudness() {
    return loudness;
  }
}


