package orchestra;

import people.musicians.Musician;

import java.util.HashMap;

/**
 * An Orchestra class, which arrange musician to sit down, and can let them to playing notes
 */
public class Orchestra {
  private final HashMap<Integer, Musician> seating;

  /**
   * A constructor which initialized the hashmap
   */
  public Orchestra() {
    seating = new HashMap<>();
  }

  /**
   * arrange musician to the seats and let them sit down.
   *
   * @param m a musician
   * @return return number to exit the function.
   */
  public int sitDown(Musician m) {

    if (seating.containsValue(m)) return 2;                             // to determine if  the musician sited down
    if (seating.size() == 16) return 1;                   // to determine if there is any empty seat in orchestra
    for (int seat = 0; seat < 16; seat++) {
      if (!seating.containsKey(seat)) {         //if the seat of musician isn't occupied, musician sit down
        m.setSeat(seat);
        seating.put(m.getseat(), m);
        break;
      }
    }
    return 0;
  }

  /**
   * determine whether the musician is seated
   *
   * @param m a musician
   * @return a boolean to determine whether the musician is seated.
   */
  public boolean isSeated(Musician m) {
    return seating.containsValue(m);
  }

  /**
   * remove musician from seating to let them stand up.
   *
   * @param m a musician
   */
  public void standUp(Musician m) {
    if (isSeated(m)) {
      seating.remove(m.getseat(), m);
    }
  }

  /**
   * Iterate whole the musician who sit in the orchestra and let them to playing the next note.
   */
  public void playNextNote() {
    seating.forEach((Key, Value) -> {
              if (isSeated(Value)) {
                Value.playNextNote();
              }
            }
    );
  }
}
