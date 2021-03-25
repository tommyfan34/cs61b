package synthesizer;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Iterator;

/** Tests the ArrayRingBuffer class.
 *  @author Josh Hug
 */

public class TestArrayRingBuffer {
    @Test
    public void someTest() {
        ArrayRingBuffer<Integer> arb = new ArrayRingBuffer<>(4);
        assertTrue(arb.isEmpty());
        arb.enqueue(1);
        assertFalse(arb.isEmpty());
        arb.enqueue(2);
        arb.enqueue(3);
        arb.enqueue(4);
        assertTrue(arb.isFull());
        assertEquals((Integer) 1, arb.dequeue());
        assertEquals(3, arb.fillCount());
        Iterator<Integer> it = arb.iterator();
        assertTrue(it.hasNext());
        assertEquals((Integer) 2, it.next());
    }

    /** Calls tests for ArrayRingBuffer. */
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestArrayRingBuffer.class);
    }
} 
