import org.junit.Test;
import static org.junit.Assert.*;

public class TestPalindrome {
    // You must use this palindrome, and not instantiate
    // new Palindromes, or the autograder might be upset.
    static Palindrome palindrome = new Palindrome();

    @Test
    public void testWordToDeque() {
        Deque d = palindrome.wordToDeque("persiflage");
        String actual = "";
        for (int i = 0; i < "persiflage".length(); i++) {
            actual += d.removeFirst();
        }
        assertEquals("persiflage", actual);
    }

    @Test
    public void testIsPalindrome() {
        assertTrue(palindrome.isPalindrome("adda"));
        assertFalse(palindrome.isPalindrome("Adda"));
        assertTrue(palindrome.isPalindrome("a"));
        assertTrue(palindrome.isPalindrome(""));
        assertFalse(palindrome.isPalindrome(",a"));

        CharacterComparator obo = new OffByOne();
        assertTrue(palindrome.isPalindrome("flake", obo));
        assertFalse(palindrome.isPalindrome("az", obo));
        assertTrue(palindrome.isPalindrome("", obo));

        CharacterComparator ob5 = new OffByN(5);
        assertTrue(ob5.equalChars('a', 'f'));
        assertTrue(ob5.equalChars('f', 'a'));
        assertFalse(ob5.equalChars('f', 'h'));
    }
}
