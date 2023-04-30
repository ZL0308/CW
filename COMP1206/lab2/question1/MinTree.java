import uk.ac.soton.ecs.comp1206.labtestlibrary.datastructure.Tree;
import uk.ac.soton.ecs.comp1206.labtestlibrary.interfaces.recursion.MinimumInTree;

public class MinTree implements MinimumInTree {

  @Override
  public int findMin(Tree t) {
    if (t == null) return Integer.MAX_VALUE;
    if (t.left() == null && t.right() == null) return t.getVal();
    return Math.min(t.getVal(), Math.min(findMin(t.left()), findMin(t.right())));
  }
}
