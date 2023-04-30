import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.threading.Seat;

import java.util.concurrent.locks.ReentrantLock;

public class SeatX implements Seat {
  private ReentrantLock leftFork;
  private ReentrantLock rightFork;

  @Override
  public synchronized void askFork1() {
    rightFork.lock();
  }

  @Override
  public synchronized void askFork2() {
    leftFork.lock();
  }

  @Override
  public void assignForks(ReentrantLock fork1, ReentrantLock fork2) {
    this.leftFork = fork1;
    this.rightFork = fork2;
  }
}
