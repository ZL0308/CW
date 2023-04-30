import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.recursion.MinimumInArray;

public class MinInt implements MinimumInArray {

  @Override
  public int findMin(int[] ints) {
    if (ints.length == 1) return ints[0];
    return Math.min(ints[ints.length - 1], findMin(java.util.Arrays.stream(ints, 0, ints.length - 1).toArray()));
  }
}
