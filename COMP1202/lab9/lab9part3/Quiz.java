package lab9part3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;

public class Quiz {

  BufferedReader reader;
  ArrayList<FlashCard> cards;
  FlashCardReader Fc;
  private final Toolbox myToolbox = new Toolbox();
  private int scores;
  private int questions;
  private int per;
  private ArrayList<String> Op;
  private PrintStream ps;
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
    scores = 0;
    questions = 0;
    per = 0;
    Op = new ArrayList<>();
    String decision;
    String input;

    System.out.println("Would you like to save your results?");
    decision = myToolbox.readStringFromCmd();

    for (FlashCard q : cards) {
      System.out.println(q.getQuestion());
      questions++;
      input = myToolbox.readStringFromCmd();

      if (input.equals(q.getAnswer())) {
        System.out.println("right");
        Op.add(q.getQuestion() + "," + input + ",right");
        scores++;
      } else {
        System.out.println("wrong" + q.getAnswer());
        Op.add(q.getQuestion() + "," + input + ",wrong");
      }

      per = ((scores * 100) / questions);

      if (decision.equals("Y")) {
        save();
      }
    }
  }


  public void main(String[] args) {
    Quiz q = new Quiz("");
  }


  public void save() {

    try {
      ps = new PrintStream("save.txt");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    for (String o : Op) {
      ps.println(o);
    }


    ps.print(scores + ",");
    ps.print(questions + ",");
    ps.println(per);
  }

}
