import org.junit.Test;
import static org.junit.Assert.*;

public class TestOffByOne {

    // You must use this CharacterComparator and not instantiate
    // new ones, or the autograder might be upset.
    static CharacterComparator offByOne = new OffByOne();

    // Your tests go here.
    @Test
    public void testOffByOne() {
        OffByOne obo = new OffByOne();
        assertTrue(obo.equalChars('a', 'b'));
        assertTrue(obo.equalChars('b', 'a'));
        assertFalse(obo.equalChars('A', 'b'));
        assertFalse(obo.equalChars('a', 'a'));
        assertTrue(obo.equalChars('&', '%'));
        assertFalse(obo.equalChars('!', '@'));
    }
}
