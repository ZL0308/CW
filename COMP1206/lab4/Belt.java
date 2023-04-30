public class Belt extends CyclicQueue {
  private static final Object lock = new Object();

  public Belt(int size) {
    super(size);
  }

  @Override
  public void enqueue(int i) throws IndexOutOfBoundsException {
    synchronized (lock) {
      if (size == arr.length) {
        try {
          lock.wait();
        } catch (InterruptedException e) {
        }
      }
      super.enqueue(i);
      lock.notify();
    }
  }

  @Override
  public int dequeue() throws IndexOutOfBoundsException {
    synchronized (lock) {
      if (isEmpty()) {
        try {
          lock.wait();
        } catch (InterruptedException e) {
        }
      }
      int a = super.dequeue();
      lock.notify();
      return a;
    }
  }
}
