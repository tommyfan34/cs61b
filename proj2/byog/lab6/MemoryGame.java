package byog.lab6;

import byog.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    private int width;
    private int height;
    private int round;
    private Random rand;
    private boolean gameOver;
    private boolean playerTurn;
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        int seed = Integer.parseInt(args[0]);
        MemoryGame game = new MemoryGame(seed, 40, 40);
        game.startGame();
    }

    public MemoryGame(int seed, int width, int height) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        rand = new Random(seed);
        gameOver = false;
        round = 1;
    }

    public String generateRandomString(int n) {
        String s = new String();
        for (int i = 0; i < n; i++) {
            int index = RandomUtils.uniform(rand, CHARACTERS.length);
            s += CHARACTERS[index];
        }
        return s;
    }

    public void drawFrame(String s) {
        //TODO: Take the string and display it in the center of the screen
        //TODO: If game is not over, display relevant game information at the top of the screen
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(width / 2, height / 2, s);
        if (!gameOver) {
            StdDraw.setFont(new Font("Monaco", Font.BOLD, 20));
            StdDraw.textLeft(1, height - 1, "Round: " + round);
            if (playerTurn) {
                StdDraw.text(width / 2, height - 1, "Type!");
            } else {
                StdDraw.text(width / 2, height - 1, "Watch!");
            }
            StdDraw.textRight(width - 1, height - 1, ENCOURAGEMENT[round % ENCOURAGEMENT.length]);
            StdDraw.line(0, height - 2, width, height - 2);
        }
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        for (int i = 0; i < letters.length(); i++) {
            drawFrame(Character.toString(letters.charAt(i)));
            StdDraw.pause(1000);
            StdDraw.clear(Color.BLACK);
            StdDraw.pause(500);
        }
    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        playerTurn = true;
        StringBuilder sb = new StringBuilder();
        while (n != 0) {
            if (StdDraw.hasNextKeyTyped()) {
                sb.append(StdDraw.nextKeyTyped());
                n--;
            }
            drawFrame(sb.toString());
        }
        StdDraw.pause(500);
        return sb.toString();
    }

    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        while (true) {
            playerTurn = false;
            drawFrame("Round: " + round);
            StdDraw.pause(1000);
            String s = generateRandomString(round);
            flashSequence(s);
            String back = solicitNCharsInput(round);
            if (!back.equals(s)) {
                drawFrame("Game Over! You made it to round: " + round);
                gameOver = true;
                break;
            }
            round++;
        }
        //TODO: Establish Game loop
    }

}
