import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.threading.UnitCounter;

public class Counter implements UnitCounter {

  private static int i;
  @Override
  public synchronized void addOne() {
    i++;
  }

  @Override
  public int getCounter() {
    return i;
  }
}
