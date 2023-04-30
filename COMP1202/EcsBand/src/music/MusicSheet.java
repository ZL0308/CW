package music;

import java.util.ArrayList;
import java.util.List;

/**
 * A MusicSheet class, which contain the composition name, tempo, length and Music Score.
 */
public class MusicSheet implements Composition {
  private String name;
  private String tempo;
  private int length;
  ArrayList<Integer> Arr;
  MusicScore m;
  MusicScore[] ms;
  ArrayList<MusicScore> am = new ArrayList<>();

  /**
   * A constructor which initialize the name, tempo and length of a composition.
   *
   * @param name   name of composition.
   * @param tempo  tempo of composition.
   * @param length length of composition.
   */
  public MusicSheet(String name, String tempo, int length) {
    this.name = name;
    this.tempo = tempo;
    this.length = length;
  }

  /**
   * fetch the name of composition.
   *
   * @return name of composition.
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * fetch the name of composition.
   *
   * @return name of composition.
   */
  @Override
  public int getNoteLength() {
    int a = 0;
    switch (tempo) {
      case "Larghissimo" -> a = 1500;
      case "Lento" -> a = 1000;
      case "Andante" -> a = 500;
      case "Moderato" -> a = 300;
      case "220" -> a =220;
      case "Allegro" -> a = 175;
      case "Presto" -> a = 150;
      default -> System.out.println("Wrong input");
    }
    return a;
  }

  /**
   * transfer notes into the relevant number, generate the music scores and add scores into the collection of music score.
   *
   * @param instrumentName the name of instrument
   * @param notes          the notes of composition
   * @param soft           the volume
   */
  @Override
  public void addScore(String instrumentName, List<String> notes, boolean soft) {
    int a = 0;
    Arr = new ArrayList<>();
    for (String s : notes) {
      switch (s) {
        case "A#2","Bb2" -> a=46;
        case "B2" -> a=47;
        case "C3" -> a = 48;
        case "C#3", "Db3" -> a = 49;
        case "D3" -> a = 50;
        case "D#3", "Eb3" -> a = 51;
        case "E3" -> a = 52;
        case "F3" -> a = 53;
        case "F#3", "Gb3" -> a = 54;
        case "G3" -> a = 55;
        case "G#3", "Ab3" -> a = 56;
        case "A3" -> a = 57;
        case "A#3", "Bb3" -> a = 58;
        case "B3" -> a = 59;
        case "C4" -> a = 60;
        case "C#4", "Db4" -> a = 61;
        case "D4" -> a = 62;
        case "D#4", "Eb4" -> a = 63;
        case "E4" -> a = 64;
        case "F4" -> a = 65;
        case "F#4", "Gb4" -> a = 66;
        case "G4" -> a = 67;
        case "G#4", "Ab4" -> a = 68;
        case "A4" -> a = 69;
        case "A#4", "Bb4" -> a = 70;
        case "B4" -> a = 71;
        case "C5" -> a = 72;
        case "C#5", "Db5" -> a = 73;
        case "D5" -> a = 74;
        case "D#5", "Eb5" -> a = 75;
        case "E5" -> a = 76;
        case "F5" -> a = 77;
        case "F#5", "Gb5" -> a = 78;
        case "G5" -> a = 79;
        case "G#5", "Ab5" -> a = 80;
        case "A5" -> a = 81;
        case "A#5", "Bb5" -> a = 82;
        case "B5" -> a = 83;
        case "C6" -> a = 84;
        case "C#6", "Db6" -> a = 85;
        case "D6" -> a = 86;
        case "D#6", "Eb6" -> a = 87;
        case "none" -> a = 0;
        default -> System.out.println("Wrong notes");
      }
      Arr.add(a);      // fetch notes
    }
    int[] in = new int[Arr.size()];    //transfer arraylist into int [] with the same size
    for (int i = 0; i < Arr.size(); i++) {
      in[i] = Arr.get(i);
    }
    m = new MusicScore(instrumentName, in, soft);     // build a music score
    am.add(m);                        // add music score into a arraylist in order to get the size of MusicScore
    ms = new MusicScore[am.size()];   // determine the size of the collection of MusicScore
    for (int i = 0; i < am.size(); i++) {      // add music score into MusicScore []
      ms[i] = am.get(i);
    }
  }

  /**
   * get the length of a notes.
   *
   * @return the size of notes arraylist.
   */
  @Override
  public int getLength() {
    return Arr.size();
  }

  /**
   * get the collection of Music Score.
   *
   * @return the collection of music score.
   */
  @Override
  public MusicScore[] getScores() {
    return ms;
  }

}
