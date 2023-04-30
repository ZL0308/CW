package people.conductors;

import music.Composition;
import music.MusicScore;
import orchestra.Orchestra;
import people.Person;
import people.musicians.Musician;
import utils.SoundSystem;

import java.util.ArrayList;

/**
 * a subclass of person, a Conductor class.
 */
public class Conductor extends Person {

  ArrayList<Musician> am = new ArrayList<>();
  Orchestra o;
  SoundSystem Ss;

  /**
   * Initialize the Conductor.
   *
   * @param name the name of conductor
   * @param Ss   a SoundSystem
   */
  public Conductor(String name, SoundSystem Ss) {
    super(name);
    this.Ss = Ss;
    o = new Orchestra();
  }

  /**
   * register the musician, and add the musician who I want to an arraylist.
   *
   * @param m a musician who I want to register.
   */
  public void registerMusician(Musician m) {
    am.add(m);
  }

  /**
   * remove the musician from arraylist after perform for a year.
   *
   * @param m the musician who I want to remove.
   */
  public void removeMusician(Musician m) {
    am.remove(m);
  }

  /**
   * to play the composition.
   *
   * @param c the composition which orchestra need to play.
   */
  public void playComposition(Composition c) {
    MusicScore[] ms = c.getScores();
    ArrayList<Musician> bm = new ArrayList<>(am);   // I create an arraylist in local which contain the whole musicians in order to avoid the infinite loop which might happen below

    Ss.setSilentMode(false);                        // open the SoundSystem

    for (MusicScore m : ms) {                       // iterate the Music Score
      for (Musician musician : bm) {                // iterate the musician
        if (m.getInstrumentID() == musician.InstrumentID()) {   // if the ID of musician equals to the ID in MusicScore
          o.sitDown(musician);                     // musician sit down
          musician.readScore(m.getNotes(), m.isSoft());     // musician read score
          bm.remove(musician);                     // remove the musician from local arraylist after they sit down
          break;                                   // break the loop after one Music Score be assigned to the musician
        }
      }
    }
    for (int i = 1; i <= c.getLength(); i++) {     // iterate the number below length and play the next note
      try {
        Thread.sleep(c.getNoteLength());           // the delay of each notes
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      o.playNextNote();                            // orchestra call this method and start play the notes
    }
    Ss.init();                                     // initialize the SoundSystem
    Ss.setSilentMode(true);                        // turn off the SoundSystem
  }

  /**
   * to return the arraylist which contain the registered musicians
   *
   * @return the arraylist which contain the registered musicians
   */
  public ArrayList<Musician> contain() {
    return am;
  }
}
