package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import timingtest.AList;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        AList<Integer> a = new AList<>();
        BuggyAList<Integer> b = new BuggyAList<>();
        int[] z = {4, 5, 6};
        for (int x : z) {
            a.addLast(x);
            b.addLast(x);
        }
        for (int x : z) {
            assertEquals(a.removeLast(), b.removeLast());
        }
    }
    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int Bsize = B.size();
                assertEquals(size, Bsize);
            }
            else {
                if (L.size() > 0){
                    int last = L.getLast();
                    int removed = L.removeLast();
                    int Blast = B.getLast();
                    int Bremoved = B.removeLast();
                    assertEquals(last, Blast);
                    assertEquals(removed, Bremoved);
                }
            }
        }
    }
}
