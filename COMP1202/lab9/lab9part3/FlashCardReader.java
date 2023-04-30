package lab9part3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class FlashCardReader {

  private BufferedReader reader;
  ArrayList<FlashCard> list;

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

  public ArrayList<FlashCard> getFlashCards() {
    list = new ArrayList<>();
    FlashCard fc;

    while (fileIsReady()) {
      String[] s = getLine().split(":");
      fc = new FlashCard(s[0], s[1]);
      list.add(fc);
    }
    return list;
  }
}


