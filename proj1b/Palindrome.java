public class Palindrome {
    public Deque<Character> wordToDeque(String word) {
        Deque<Character> cd = new ArrayDeque<>();
        for (int i = 0; i < word.length(); i++) {
            cd.addLast(word.charAt(i));
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

    private boolean helperPalindrome(Deque<Character> cd, CharacterComparator cc) {
        if (cd.size() <= 1) {
            return true;
        }
        Character front = cd.removeFirst();
        Character back = cd.removeLast();
        if (!cc.equalChars(front, back)) {
            return false;
        } else {
            return helperPalindrome(cd, cc);
        }
    }

    public boolean isPalindrome(String word, CharacterComparator cc) {
        Deque<Character> cd = wordToDeque(word);
        return helperPalindrome(cd, cc);
    }
}
