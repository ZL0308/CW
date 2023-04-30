public class Belt extends CyclicQueue {
  private final Object lock = new Object();

  public Belt(int size) {
    super(size);
  }

  @Override
  public void enqueue(int i) throws IndexOutOfBoundsException {
    synchronized (lock) {
      while (size == arr.length) {
        try {
          lock.wait();
        } catch (InterruptedException e) {
        }
      }
      super.enqueue(i);
      lock.notifyAll();
    }
  }

  @Override
  public int dequeue() throws IndexOutOfBoundsException {
    synchronized (lock) {
      while (isEmpty()) {
        try {
          lock.wait();
        } catch (InterruptedException e) {
        }
      }
      int a = super.dequeue();
      lock.notifyAll();
      return a;
    }
  }
}
