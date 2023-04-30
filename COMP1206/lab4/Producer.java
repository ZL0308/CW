import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.threading.FactoryWorker;
import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.threading.NumberQueue;

public class Producer extends FactoryWorker {

  final NumberQueue belt;

  public Producer(int id, NumberQueue belt) {
    super("Producer", id, belt);
    this.belt = belt;
  }

  @Override
  public void message(int i) {
    System.out.println("Producer " + " " + id + " put " + i + " into the belt");
  }

  @Override
  public int action() {
      int i = (int) (Math.random() * 10);
      belt.enqueue(i);
      return i;
  }

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        message(action());
      } catch (Exception e) {
        messageError();
      }
    }
  }
}