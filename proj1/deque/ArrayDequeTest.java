package deque;
import org.junit.Test;
import static org.junit.Assert.*;
import edu.princeton.cs.algs4.StdRandom;
public class ArrayDequeTest {
    @Test
    public void testgetmethod1() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        for (int x = 0; x < 5; x += 1) {
            a.addFirst(x);
        }
        int x = a.get(2);
        assertEquals(2, x);
    }

    @Test
    public void testsize1() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        for (int x = 0; x < 5; x += 1) {
            a.addFirst(x);
        }
        int x = a.removeLast();
        assertEquals(0, x);
        assertEquals(4, a.size());
    }

    @Test
    public void randomizedTest(){
        ArrayDeque<Integer> a = new ArrayDeque<>();
        int N = 5000;
        int size = 0;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber < 2) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                a.addFirst(randVal);
                size += 1;
            } else if (operationNumber == 2) {
                // size
                int asize = a.size();
                assertEquals(asize, size);
            }

        }
    }

    @Test
    public void removeLasttest(){
        ArrayDeque<Integer> a = new ArrayDeque<>();
        int N = 5000;
        int M = 4500;
        for (int i = 0; i < N; i += 1) {
            int randVal = StdRandom.uniform(0, 100);
            a.addFirst(randVal);
        }
        for (int i = 0; i < M; i += 1) {
            a.removeLast();
        }
        System.out.println();

        for (int i = 0; i < 480; i += 1) {
            a.removeFirst();
        }
        System.out.println();
    }


    @Test
    public void equalstest(){
        ArrayDeque<Integer> a = new ArrayDeque<>();
        ArrayDeque<Integer> b = new ArrayDeque<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int randVal = StdRandom.uniform(0, 100);
            a.addFirst(randVal);
            b.addFirst(randVal);
        }
        assertEquals(true, a.equals(b));
        b.addFirst(N);
        a.addLast(1);
        assertEquals(false, b.equals(a));

    }
}


