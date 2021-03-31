package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.StringCharacterIterator;
import java.util.Random;

public class Game {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    // width and height should be odd

    private long seed;
    private Random r;


    public Game() {
        seed = 0;
        ter.initialize(byog.Core.MapGenerator.WIDTH, byog.Core.MapGenerator.HEIGHT);
    }

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
        drawMenu();
        boolean inputSeed = false;
        boolean quitFlag = false;
        int seedTextIndention = 0;
        GameState gameState = new GameState();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == 'n' || c == 'N') {
                    Font font = new Font("Arial", Font.PLAIN, 50);
                    StdDraw.setFont(font);
                    StdDraw.text(30, 5, "Type seed: ");
                    inputSeed = true;
                    quitFlag = false;
                    seed = 0;
                } else if (Character.isDigit(c)) {
                    if (inputSeed) {
                        seed = seed * 10 + c - '0';
                        StdDraw.text(40 + 3 * seedTextIndention, 5, String.valueOf(c));
                        seedTextIndention++;
                    }
                    quitFlag = false;
                } else if (c == 's' || c == 'S') {
                    Font font = new Font("Sans Serif", Font.PLAIN, 16);
                    StdDraw.setFont(font);
                    r = new Random(seed);
                    gameState.world = MapGenerator.generateWorld(r);
                    System.out.println(TETile.toString(gameState.world));
                    ter.renderFrame(gameState.world);
                } else if (c == '\b') {
                    if (inputSeed) {
                        seed /= 10;
                        StdDraw.setPenColor(StdDraw.BLACK);
                        StdDraw.filledSquare(40 + 3 * seedTextIndention - 3, 5, 2);
                        StdDraw.setPenColor(StdDraw.WHITE);
                        if (seedTextIndention != 0) {
                            seedTextIndention--;
                        }
                    }
                    quitFlag = false;
                } else if (c == 'q') {
                    if (quitFlag) {
                        saveWorld(gameState);
                        break;
                    }
                    quitFlag = false;
                    inputSeed = false;
                } else if (c == ':') {
                    quitFlag = true;
                    inputSeed = false;
                } else if (c == 'l' || c == 'L') {
                    Font font = new Font("Sans Serif", Font.PLAIN, 16);
                    StdDraw.setFont(font);
                    gameState = loadWorld();
                    ter.renderFrame(gameState.world);
                }
                StdDraw.show();
            }
        }
        System.exit(0);
    }

    private void drawMenu() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        Font font1 = new Font("Arial", Font.PLAIN, 70);
        Font font2 = new Font("Arial", Font.PLAIN, 50);
        StdDraw.setFont(font1);
        StdDraw.text(40, 25, "CS61B: THE GAME");
        StdDraw.setFont(font2);
        StdDraw.text(40, 20, "New Game (N)");
        StdDraw.text(40, 15, "Load Game (L)");
        StdDraw.text(40, 10, "Quit (Q)");
        StdDraw.show();
    }


    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     * @source
     */
    public TETile[][] playWithInputString(String input) {
        TETile[][] finalWorldFrame = null;
        StringCharacterIterator it = new StringCharacterIterator(input);
        Character cur;
        boolean seedFlag = false;
        boolean quitFlag = false;

        while (it.current() != StringCharacterIterator.DONE) {
            cur = it.current();
            if (cur.equals('l') || cur.equals('L')) {
                finalWorldFrame = loadWorld().world;
                break;
            } else if (cur.equals('n') || cur.equals('N')) {
                seedFlag = true;
                seed = 0;
                quitFlag = false;
            } else if (Character.isDigit(cur)) {
                if (seedFlag) {
                    seed = seed * 10 + cur - '0';
                }
            } else if (cur.equals('S') || cur.equals('s')) {
                finalWorldFrame = MapGenerator.generateWorld(r);
            } else if (cur.equals(':')) {
                quitFlag = true;
                seedFlag = false;
            } else if (cur.equals('q') || cur.equals('Q')) {
                if (quitFlag) {
                    saveWorld(new GameState(finalWorldFrame, r));
                    break;
                }
                quitFlag = false;
                seedFlag = false;
            }
            it.next();
        }
        return finalWorldFrame;
    }

    private static GameState loadWorld() {
        File f = new File("./game.txt");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                GameState loadWorld = (GameState) os.readObject();
                os.close();
                return loadWorld;
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }
        GameState retworld = null;
        return retworld;
    }

    private static void saveWorld(GameState t) {
        File f = new File("./game.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(t);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }
}
