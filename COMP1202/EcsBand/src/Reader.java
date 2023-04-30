import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A reader class to read and get the line of the file
 */
public class Reader {
  private BufferedReader reader;
  private ArrayList<String> mus;

  /**
   * A constructor to initialize the buffered reader and pass the file.
   *
   * @param fileName the name of file, including musician file and composition file.
   */
  public Reader(String fileName) {
    try {
      this.reader = new BufferedReader(new FileReader(fileName));
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
    } catch (RuntimeException | IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * to get each line from the file, and add them to an arraylist, finally return this arraylist.
   *
   * @return a arraylist
   */
  public ArrayList<String> getState() {
    mus = new ArrayList<>();
    while (fileIsReady()) {
      mus.add(getLine());
    }
    return mus;
  }
}