package lab9part2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class Quiz {

  BufferedReader reader;
  ArrayList<FlashCard> cards;
  FlashCardReader Fc;
  Toolbox myToolbox = new Toolbox();

  public Quiz(String fileName) {
    Fc = new FlashCardReader(fileName);
    cards = Fc.getFlashCards();
    try {
      this.reader = new BufferedReader(new FileReader(fileName));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  play();
  }

  public void play() {
    String input;

    for (FlashCard q : cards) {
      System.out.print(q.getQuestion());
      input = myToolbox.readStringFromCmd();
      if (input.equals(q.getAnswer())) {
        System.out.println("right");
      } else {
        System.out.println("wrong" + q.getAnswer());
      }
    }
  }


  public void main(String[] args) {
    Quiz q = new Quiz("");
  }
}
