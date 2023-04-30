import people.musicians.Cellist;
import people.musicians.Musician;
import people.musicians.Pianist;
import people.musicians.Violinist;
import utils.SoundSystem;

import javax.sound.midi.MidiUnavailableException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A helper class to split musicians file.
 */
public class splitter1 {
  private SoundSystem Ss;
  private BufferedReader reader;
  private ArrayList<Musician> m;

  /**
   * A constructor to initialize the buffer reader and read the file.
   *
   * @param Musicians the file name of musician file.
   */
  public splitter1(String Musicians) {
    try {
      this.reader = new BufferedReader(new FileReader(Musicians));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * to read the line and get it.
   *
   * @return to exit the function.
   */
  public String getLine() {
    String line;
    try {
      line = reader.readLine();
      return line;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * to determine if file is ready.
   *
   * @return a boolean to determine if file is ready.
   */
  public boolean fileIsReady() {
    try {
      return reader.ready();
    } catch (IOException | NullPointerException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * to get each line from the file, split them and add them to an arraylist, finally return this arraylist.
   *
   * @return return an arraylist which contain the correct format
   * @throws MidiUnavailableException while file isn't ready, throws exception.
   */
  public ArrayList<Musician> splitMusicians() throws MidiUnavailableException {
    Ss = new SoundSystem();
    m = new ArrayList<>();
    while (fileIsReady()) {
      String A = getLine().replace(" ","");
      String[] a = A.split("\\(|\\)");
      switch (a[1]) {
        case "Piano" -> m.add(new Pianist(a[0],Ss));
        case "Cello" -> m.add(new Cellist(a[0],Ss));
        case "Violin" -> m.add(new Violinist(a[0],Ss));
        default -> System.out.println("wrong Musician type");
      }
    }
    return m;
  }
}
