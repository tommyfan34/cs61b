public class ArrayDequeTest {
    /* Utility method for printing out empty checks. */
    public static boolean checkEmpty(boolean expected, boolean actual) {
        if (expected != actual) {
            System.out.println("isEmpty() returned " + actual + ", but expected: " + expected);
            return false;
        }
        return true;
    }

    /* Utility method for printing out empty checks. */
    public static boolean checkSize(int expected, int actual) {
        if (expected != actual) {
            System.out.println("size() returned " + actual + ", but expected: " + expected);
            return false;
        }
        return true;
    }

    /* Prints a nice message based on whether a test passed.
     * The \n means newline. */
    public static void printTestStatus(boolean passed) {
        if (passed) {
            System.out.println("Test passed!\n");
        } else {
            System.out.println("Test failed!\n");
        }
    }

    public static void addIsEmptySizeTest() {
        System.out.println("Running add/isEmpty/Size test.");
        ArrayDeque<String> ad1 = new ArrayDeque<>();
        boolean passed = checkEmpty(true, ad1.isEmpty());
        ad1.addFirst("Zero");
        passed = checkSize(1, ad1.size()) && passed;
        ad1.addFirst("Minus One");
        ad1.addLast("One");
        ad1.addLast("Two");
        passed = checkSize(4, ad1.size()) && passed;
        ad1.printDeque();
        ad1.removeFirst();
        ad1.removeFirst();
        ad1.removeLast();
        ad1.printDeque();
        ad1.addLast("Two");
        ad1.addLast("Three");
        ad1.addLast("Four");
        ad1.addLast("Five");
        ad1.addLast("Six");
        ad1.addLast("Seven");
        ad1.addLast("Eight");
        for (int i = 0; i < 1024; i++) {
            ad1.addFirst("Test");
        }
        for (int i = 0; i < 1024; i++) {
            ad1.removeFirst();
        }
        printTestStatus(passed);
    }

    public static void main(String[] args) {
        System.out.println("Running tests.\n");
        addIsEmptySizeTest();
    }
}
