import music.Composition;
import orchestra.Orchestra;
import people.conductors.Conductor;
import people.musicians.Musician;
import utils.SoundSystem;

import javax.sound.midi.MidiUnavailableException;
import java.io.*;
import java.util.*;

/**
 * A EcsBandAid class, which can play the composition.
 */
public class EcsBandAid {
  private SoundSystem Ss;
  private List<Musician> m;
  private List<Composition> c;
  private Conductor con;
  private Orchestra o;
  private static PrintStream ps;
  private static PrintStream psc;
  private static Reader reader1;
  private static Reader reader2;
  private Reader reader3;
  private Reader reader4;
  private Reader reader5;
  private static ArrayList<String> previousMus;
  private ArrayList<String> previousCom;     /* I create this arraylist to receive the previous musicians,
                                              number of composition which has been played and the years remain.*/
  private static int Years;
  private static boolean stop = false;       // I declare a boolean to receive the command that if user want to stop.

  /**
   * A constructor which initialize the SoundSystem, musician list, composition list, conductor, and several readers.
   *
   * @param Ss a SoundSystem
   * @param m  musician list
   * @param c  composition list
   */
  public EcsBandAid(SoundSystem Ss, List<Musician> m, List<Composition> c) {
    this.Ss = Ss;
    this.m = m;
    this.c = c;
    con = new Conductor("", Ss);                // I created multiple buffer readers in order to record the information after initialize the print stream.
    try {
      reader1 = new Reader("State.txt");
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      reader2 = new Reader("compositionSet.txt");
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      reader4 = new Reader("State.txt");
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      reader5 = new Reader("State.txt");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * let the orchestra perform for a year.
   *
   * @throws FileNotFoundException I initialized a reader 3 in this method, if there is no file, it will throw an exception.
   */
  public void performForAYear() throws FileNotFoundException {
    Collections.shuffle(c);
    o = new Orchestra();
    Scanner input = new Scanner(System.in);                      // receive the input

    List<Composition> randomC = new ArrayList<>();               // randomly fetch a composition and add three compositions to the list

    if (reader2.fileIsReady()) {
      try {                                                     //////// if didn't stop between two years, I need to initialize the reader
        reader3 = new Reader("compositionSet.txt");
      } catch (Exception e) {
        e.printStackTrace();
      }
      previousCom = reader3.getState();
      if (previousCom.size() == 6) {                               // size = 6, which means last Year's perform have finished
        try {
          psc = new PrintStream("compositionSet.txt");    // initialize a new PrintStream to clear the previous txt file
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
        randomC.add(c.get(new Random().nextInt(c.size())));        // then, we need to choose three compositions randomly from whole compositions
        randomC.add(c.get(new Random().nextInt(c.size())));
        randomC.add(c.get(new Random().nextInt(c.size())));
      }

      if (previousCom.size() == 4) {                                // size = 4, which means there was only one composition has been played in last year
        for (Composition composition : c) {
          if (composition.getName().equals(previousCom.get(1)) || composition.getName().equals(previousCom.get(2))) {
            randomC.add(composition);                               // we add other two composition
            if (randomC.size() == 2) {                              // when there are two compositions are chosen, we break
              break;
            }
          }
        }
        psc = new PrintStream("compositionSet.txt");
        psc.println(previousCom.get(0) + " has chosen");          // print out what compositions have chosen and played last year
        psc.println(previousCom.get(0) + " has performed");
      } else if (previousCom.size() == 5) {                         // size = 5, which means there were two composition have been played
        for (Composition composition : c) {
          if (composition.getName().equals(previousCom.get(2))) {   // add another one
            randomC.add(composition);
            break;
          }
        }
        psc = new PrintStream("compositionSet.txt");
        psc.println(previousCom.get(0) + " has chosen");          // print out what compositions have been chosen and played
        psc.println(previousCom.get(1) + " has chosen");
        psc.println(previousCom.get(0) + " has performed");
        psc.println(previousCom.get(1) + " has performed");
      }
    } else {
      randomC.add(c.get(new Random().nextInt(c.size())));        // if file is not ready, which means this is the first year we start
      randomC.add(c.get(new Random().nextInt(c.size())));
      randomC.add(c.get(new Random().nextInt(c.size())));
      psc = new PrintStream("compositionSet.txt");      // when play the composition in first time, initialize a new PrintStream
    }

    for (Composition comp : randomC) {
      System.out.println(comp.getName() + " is chosen");          //print out what compositions were chosen
      psc.println(comp.getName());
    }

    if (reader4.fileIsReady() && con.contain().isEmpty()) {
      previousMus = reader4.getState();
      for (String s : previousMus) {            // register previous musicians who should stay in the orchestra but be removed when restarted.
        for (Musician m : m) {
          if (m.getName().equals(s)) {
            con.registerMusician(m);
            break;                              // If it registers musician successfully, break.
          }
        }
      }
    }

    for (Musician m : m) {
      if (!con.contain().contains(m)) {            // get musicians list and find out who hasn't been registered
        con.registerMusician(m);                   // register musician
        System.out.println(m.getName() + " is invited");
      }
    }

    int numberOfComposition = 0;                  // I create this int to count how many composition has been played in a year

    if (reader5.fileIsReady()) {                  // fetch the number of composition which has been played before stop.
      previousMus = reader5.getState();
      if (Integer.parseInt(previousMus.get(previousMus.size() - 2)) != 3) {
        /* I put the number in arraylist, the index is size() -2, if the number isn't 3,
        which means orchestra haven't finish last year's performance */

        numberOfComposition = Integer.parseInt(previousMus.get(previousMus.size() - 2));
      // thus, we fetch the number of composition which has been played last year.
      }
    }

    for (Composition composition : randomC) {       // iterate composition and play
      System.out.println(composition.getName() + " is performed at the moment");
      con.playComposition(composition);
      psc.println(composition.getName());           // print composition into the txt file
      boolean jump = false;                         // I declare a boolean to break this function "PerformForAYear" if I want.
      numberOfComposition++;                        // count the number of composition which has been played
      while (true) {                                // to guarantee the input of user is right
        System.out.println("Do you want to stop playing? Y/N");     // ask user if they want to stop the playing
        String choice = input.nextLine();
        if (choice.equals("Y")) {
          if (numberOfComposition < 3) {        // if number < 3, which means orchestra haven't finish perform.
            Years++;                            // I need to make sure that it will stay in this year when next time I restarted it.
          }                                     // to avoid Year changing, remain Year should +1
          stop = true;                          // if user want to stop it, stop is true
          jump = true;                          // and jump is true
          break;
        } else if (choice.equals("N")) {        // if "NO", break the while loop, and continue play the composition
          break;
        } else {
          System.out.println("Invalid input");
        }
      }
      if (jump) {                               // if want to stop, break this function.
        break;
      }
    }

    for (Musician m : m) {
      int r = (int) (Math.random() * 10);     // each musicians have 50% leave the band
      if (r < 5) {
        o.standUp(m);                         // stand up
        con.removeMusician(m);                // remove musician from register
        System.out.println(m.getName() + " leave the band ");
      }
    }

    System.out.println();

    ArrayList<Musician> theRestOfMusician = new ArrayList<>(con.contain());  // fetch the rest of musician in register

    ps = new PrintStream("State.txt");     // initialize a print stream

    for (Musician remain : theRestOfMusician) {
      ps.println(remain.getName());     // print out the rest of musician
    }
    ps.println(numberOfComposition);    // print out how many composition has been played
  }

  /**
   * A main method which can receive the parameters and play the composition.
   *
   * @param args the file name of musician file, the file name of composition file, the  number of year that orchestra supposed to perform.
   * @throws MidiUnavailableException when the notes input is error and can't be transferred to midi notes, it will throw an exception.
   * @throws InterruptedException if the current thread has been interrupted, it will throw an exception
   * @throws FileNotFoundException I initialized a reader to get the previous state, if there is no relevant file, it will throw the exception.
   */
  public static void main(String[] args) throws MidiUnavailableException, InterruptedException, FileNotFoundException {
    SoundSystem soundSystem = new SoundSystem();
    splitter1 s1 = new splitter1(args[0]);
    splitter2 s2 = new splitter2(args[1]);
    int Year = Integer.parseInt(args[2]);
    var musicians = s1.splitMusicians();
    var composition = s2.splitComposition();
    EcsBandAid ecsBandAid = new EcsBandAid(soundSystem, musicians, composition);

    Years = Year;                       // I created a variable, which is the real Year EcsBandAid have to play.
    int i = 1;                          // I initialized this int as 1.

    if (reader1.fileIsReady()) {        // if file is ready, which means it has stopped before, and we need fetch real Year from the file
      previousMus = reader1.getState();
      Years = Integer.parseInt(previousMus.get(previousMus.size() - 1));
    }

    for (; i <= Years; i++) {         // start to iterate the number of years which need to be played
      ecsBandAid.performForAYear();
      if (stop) {               // if stopped, print the rest of Years into the file
        Years = Years - i;
        ps.println(Years);
        break;                  // break the loop, and exit the programme.
      }
      ps.println(Years - i);        // if user don't stop, print each remain Year
    }
  }
}