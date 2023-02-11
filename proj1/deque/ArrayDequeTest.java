package deque;
import org.junit.Test;
import static org.junit.Assert.*;
public class ArrayDequeTest {
    @Test
    public void testgetmethod(){
        ArrayDeque<Integer> a = new ArrayDeque<>();
        for (int x = 0; x < 5; x += 1){
            a.addFirst(x);
        }
        int x = a.get(2);
        assertEquals(2, x);
    }
}
