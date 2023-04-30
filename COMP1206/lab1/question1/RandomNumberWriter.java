import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.io.RandomIO;

import java.io.*;
import java.util.Random;

public class RandomNumberWriter implements RandomIO {

  public Random r;


  public RandomNumberWriter(long seed) {
    this.r = new Random(seed);
  }

  @Override
  public void writeRandomChars(String s) throws IOException {
    Writer out = new OutputStreamWriter(new FileOutputStream(s));
    for (int i = 0; i < 10000; i++) {
      int number = r.nextInt(100000);
      String str = String.valueOf(number);
      out.write(str+"\r\n");
    }
    out.close();
  }

  @Override
  public void writeRandomByte(String s) throws IOException {
    OutputStream out = new FileOutputStream(s);
    byte [] b = new byte[4];
    for (int i = 0; i < 10000; i++) {
      int number = r.nextInt(100000);
      b[3] = (byte) (number & 0xFF);
      b[2] = (byte) (number >> 8 & 0xFF);
      b[1] = (byte) (number >> 16 & 0xFF);
      b[0] = (byte) (0);
      out.write(b);
    }
    out.close();
  }
}
