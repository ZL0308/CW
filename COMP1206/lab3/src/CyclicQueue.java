import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.threading.NumberQueue;

public class CyclicQueue implements NumberQueue {

  public int size;
  public int[] arr;
  private int head;
  private int tail;

  public CyclicQueue(int size) {
    this.size = 0;
    this.arr = new int[size];
    this.head = 0;
    this.tail = 0;
  }

  @Override
  public void enqueue(int i) throws IndexOutOfBoundsException {
    if ((size == arr.length)) throw new IndexOutOfBoundsException("full");
    if(!isEmpty()) tail = (tail + 1) % arr.length;
    arr[tail] = i;
    size++;
  }

  @Override
  public int dequeue() throws IndexOutOfBoundsException {
    if (isEmpty()) throw new IndexOutOfBoundsException("empty");
    int headNum = arr[head];
    arr[head] = 0;
    head = (head + 1) % arr.length;
    size--;
    return headNum;
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }
}
