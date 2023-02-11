package deque;
import org.junit.Test;
import static org.junit.Assert.*;
import edu.princeton.cs.algs4.StdRandom;
public class ArrayDequeTest {
    @Test
    public void testgetmethod1(){
        ArrayDeque<Integer> a = new ArrayDeque<>();
        for (int x = 0; x < 5; x += 1){
            a.addFirst(x);
        }
        int x = a.get(2);
        assertEquals(2, x);
    }

    @Test
    public void testsize1(){
        ArrayDeque<Integer> a = new ArrayDeque<>();
        for (int x = 0; x < 5; x += 1){
            a.addFirst(x);
        }
        int x = a.removeLast();
        assertEquals(0, x);
        assertEquals(4, a.size());
    }
    @Test
    public void randomizedTest(){
        ArrayDeque<Integer> L = new ArrayDeque<>();
        int N = 5000;
        int testsize = 0;

        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 2);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                testsize += 1;
            }
            else if (operationNumber == 1) {
                // size
                int size = L.size();
                assertEquals(size, testsize);
            }
            }
        }
    }


