import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DAGSortTest {

  /**
   * Test if the code can deal with the null input
   */
  @Test
  void testNull() {
    assertThrows(NullPointerException.class, () -> DAGSort.sortDAG(null));
  }

  /**
   * Test if the code can deal with the cycle
   */
  @Test
  void testCycle() {

    int[][] edge1 = {{}, {3}, {3}, {4}, {5}, {6}, {5}};
    int[][] edge2 = {{}, {2}, {3}, {4}, {5}, {2, 6}, {4, 7}, {5}};

    assertThrows(CycleDetectedException.class, () -> DAGSort.sortDAG(edge1));
    assertThrows(CycleDetectedException.class, () -> DAGSort.sortDAG(edge2));
  }

  /**
   * Test if the code can deal with the invalid input
   */
  @Test
  void testNode() {
    int[][] edge1 = {{9}, {3}, {3}, {4}, {5}, {6}, {5}};
    assertThrows(InvalidNodeException.class, () -> DAGSort.sortDAG(edge1));
  }

  /**
   * Test if the code can deal with the empty graph
   *
   * @throws InvalidNodeException   the exception
   * @throws CycleDetectedException the exception
   */
  @Test
  void testEmpty() throws InvalidNodeException, CycleDetectedException {
    int[][] edge = {};
    int[] arr = DAGSort.sortDAG(edge);
    assertEquals(0, arr.length);
  }

  /**
   * Test the output order
   *
   * @throws InvalidNodeException   the exception
   * @throws CycleDetectedException the exception
   */
  @Test
  void testOrder() throws InvalidNodeException, CycleDetectedException {
    int[][] edge = {{1, 2}, {2, 5}, {3}, {}, {1, 3}, {3}};
    int[] arr = DAGSort.sortDAG(edge);

    int[][] excepted = {
            {4, 0, 1, 5, 2, 3},
            {4, 0, 1, 2, 5, 3},
            {0, 4, 1, 2, 5, 3},
            {0, 4, 1, 5, 2, 3}
    };

    boolean a = false;

    for (int[] arr1 : excepted) {
      if (Arrays.equals(arr, arr1)) {
        a = true;
        break;
      }
    }
    assertTrue(a);
  }

  /**
   * Test if the sortDAG can deal with the disconnect graph
   *
   * @throws InvalidNodeException   the exception
   * @throws CycleDetectedException the exception
   */
  @Test
  void testDisconnect() throws InvalidNodeException, CycleDetectedException {
    int[][] edge = {{1}, {}, {3}, {}};
    int[] arr = DAGSort.sortDAG(edge);

    int[][] excepted = {
            {0, 1, 2, 3},
            {2, 3, 0, 1}
    };

    boolean a = false;

    for (int[] arr1 : excepted) {
      if (Arrays.equals(arr, arr1)) {
        a = true;
        break;
      }
    }
    assertTrue(a);
  }

  /**
   * Test if the code can deal with the negative node
   */
  @Test
  void testNegative() {
    int[][] edge = {{-1}, {0}, {1}};
    assertThrows(InvalidNodeException.class, () -> DAGSort.sortDAG(edge));
  }

  /**
   * Test the code with a large size of graph
   */
  @Test
  void testWithLargeSize() throws InvalidNodeException, CycleDetectedException {
    int[][] edge = new int[1000][1000];       // generate a DAG
    boolean a = true;

    for (int i = 0; i < 999; i++) {
      edge[i] = new int[]{i + 1};       // from i to i + 1
    }
    edge[999] = new int[0];

    int[] arr = DAGSort.sortDAG(edge);

    HashMap<Integer, Integer> vertexPositions = new HashMap<>();

    for (int i = 0; i < arr.length; i++) {        // set the order of each vertex
      vertexPositions.put(arr[i], i);
    }

    for (int u = 0; u < edge.length; u++) {       // iterate all the vertex
      for (int v : edge[u]) {                     // iterate the next node of each vertex
        a = vertexPositions.get(u) < vertexPositions.get(v);   // the value of next node should greater than the vertex
      }
    }
    assertTrue(a);
  }


}
