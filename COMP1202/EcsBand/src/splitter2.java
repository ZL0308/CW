import music.Composition;
import music.MusicSheet;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class splitter2 {

  private BufferedReader reader;
  private ArrayList<Composition> com;
  private String name;
  private String tempo;
  private int length;
  private Composition C;
  private String fc = null;
  private String A = null;

  public splitter2(String Compositions) {
    try {
      this.reader = new BufferedReader(new FileReader(Compositions));
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
    }
    return null;
  }

  public boolean fileIsReady() {
    try {
      return reader.ready();
    } catch (IOException | NullPointerException e) {
      e.printStackTrace();
    }
    return false;
  }

  public ArrayList<Composition> splitComposition() {
    com = new ArrayList<>();
    while (fileIsReady()) {
      if (fc == null || !fc.equals("Nam")) {
        A = getLine();
      }
      fc = A.substring(0, 4);
      if (fc.equals("Name")) {
        name = A.substring(6);
        String[] Str1 = getLine().split("\\s|:");
        String[] Str2 = getLine().split("\\s|:");
        if ("Tempo".equals(Str1[0])) tempo = Str1[2];
        if ("Length".equals(Str2[0])) length = Integer.parseInt(Str2[2]);
      } else {
        C = new MusicSheet(name, tempo, length);
        boolean Boolean = true;
        boolean Score = true;
        String[] Str3 = A.replace(" ", "").split(",|\\{|\\}");
        switch (Str3[0]) {
          case "Piano", "Cello", "Violin" -> {
            List<String> L1 = new ArrayList<>(Arrays.asList(Str3).subList(3, Str3.length));
            if (Str3[1].equals("loud")) {
              Boolean = false;
            }
            C.addScore(Str3[0], L1, Boolean);
          }
          default -> System.out.println("error type");
        }
        while (Score && fileIsReady()) {
          A = getLine();
          fc = A.substring(0, 3);
          if (fc.equals("Nam")) {
            Score = false;
          } else {
            String[] Str4 = A.replace(" ", "").split(",|\\{|\\}");
            switch (Str4[0]) {
              case "Piano", "Cello", "Violin" -> {
                List<String> L1 = new ArrayList<>(Arrays.asList(Str4).subList(3, Str4.length));
                if (Str4[1].equals("loud")) {
                  Boolean = false;
                }
                C.addScore(Str4[0], L1, Boolean);
              }
              default -> System.out.println("error type");
            }
          }
        }
        com.add(C);
      }
    }
    return com;
  }
}




