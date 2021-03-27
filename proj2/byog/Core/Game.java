package byog.Core;

import byog.SaveDemo.World;
import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;

import java.io.*;
import java.text.StringCharacterIterator;

public class Game {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
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
     */
    public TETile[][] playWithInputString(String input) {
        // TODO: Fill out this method to run the game using the input passed in,
        // and return a 2D tile representation of the world that would have been
        // drawn if the same inputs had been given to playWithKeyboard().

        TETile[][] finalWorldFrame = null;
        StringCharacterIterator it = new StringCharacterIterator(input);
        Character cur;
        boolean seedFlag = false;
        boolean quitFlag = false;
        int seed = 0;
        while (it.current() != StringCharacterIterator.DONE) {
            cur = it.current();
            if (cur.equals('l') || cur.equals('L')) {
                finalWorldFrame = loadWorld();
                break;
            } else if (cur.equals('n') || cur.equals('N')) {
                seedFlag = true;
                quitFlag = false;
            } else if (Character.isDigit(cur)) {
                if (seedFlag) {
                    seed = seed * 10 + cur - '0';
                }
            } else if (cur.equals('S') || cur.equals('s')) {
                finalWorldFrame = generateWorld(seed);
            } else if (cur.equals(':')) {
                quitFlag = true;
                seedFlag = false;
            } else if (cur.equals('q') || cur.equals('Q')) {
                if (quitFlag) {
                    saveWorld(finalWorldFrame);
                    break;
                }
                quitFlag = false;
                seedFlag = false;
            }
            it.next();
        }
        return finalWorldFrame;
    }

    private static TETile[][] loadWorld() {
        // TODO: load the world
        File f = new File("./game.ser");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                TETile[][] loadWorld = (TETile[][]) os.readObject();
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
        TETile[][] retworld = null;
        return retworld;
    }

    private static void saveWorld(TETile[][] t) {
        // TODO: save the world
        File f = new File("./game.ser");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(t);
            os.close();
        }  catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    private static TETile[][] generateWorld(int seed) {
        // TODO: randomly generate the world
        TETile[][] retworld = null;
        return retworld;
    }

}
