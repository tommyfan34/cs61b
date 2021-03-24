public class Palindrome {
    public Deque<Character> wordToDeque(String Word) {
        Deque<Character> cd = new ArrayDeque<>();
        for (int i = 0; i < Word.length(); i++) {
            cd.addLast(Word.charAt(i));
        }
        return cd;
    }

    private boolean helperPalindrome(Deque<Character> cd) {
        if (cd.size() <= 1) {
            return true;
        }
        Character front = cd.removeFirst();
        Character back = cd.removeLast();
        if (front != back) {
            return false;
        } else {
            return helperPalindrome(cd);
        }
    }

    public boolean isPalindrome(String word) {
        Deque<Character> cd = wordToDeque(word);
        return helperPalindrome(cd);
    }
}
