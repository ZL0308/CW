package lab9part1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FlashCardReader {

  private BufferedReader reader;

  public FlashCardReader(String fileName) {

    try {
      this.reader = new BufferedReader(new FileReader(fileName));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public String getLine() {
    String line;
    try {
      line = reader.readLine();
      return line;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public boolean fileIsReady() {
    try {
      return reader.ready();
    } catch (RuntimeException | IOException e) {
      e.printStackTrace();
      return false;
    }
  }
}