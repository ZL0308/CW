public class Gate implements Runnable {

  private int guests;
  private Counter counter;

  public Gate(Counter counter, int guests) {
    this.guests = guests;
    this.counter = counter;
  }

  @Override
  public void run() {
    for (int i = 0; i < guests; i++) {
      counter.addOne();
    }
  }

  public int expectedNum() {
    return guests;
  }
}


