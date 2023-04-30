import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.threading.Seat;
import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.threading.SeatFactory;
import uk.ac.soton.ecs.comp1206.labtestlibrary.recursion.Tuple;

public class Factory implements SeatFactory {
  @Override
  public Tuple<java.lang.Class<? extends Seat>, java.lang.Class<? extends Seat>> getSeats() {
    return new Tuple<>(SeatX.class, SeatY.class);
  }
}
