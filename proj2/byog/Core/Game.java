package byog.Core;

import byog.SaveDemo.World;
import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.io.*;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public static final int ROOMMAXLEN = 8;
    public static final int HWYMAXLEN = 16;
    private int seed;
    private List<Room> rms;
    private List<Hallway> hwys;


    public Game() {
        seed = 0;
        rms = new ArrayList<>();
        hwys = new ArrayList<>();
        ter.initialize(WIDTH, HEIGHT);
    }

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
                finalWorldFrame = generateWorld();
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
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    private class Hallway {
        int[] point1;  // point1[0] is x position of point1, point[1] is t position of point2
        int[] point2;
        boolean connected;
        public Hallway(int[] pt1, int[] pt2) {
            point1 = pt1;
            point2 = pt2;
            connected = false;
        }
    }

    private class Room {
        int[] upperRight;
        int[] bottomLeft;
        public Room(int[] bl, int[] ur) {
            upperRight = ur;
            bottomLeft = bl;
        }
    }

    private TETile[][] generateWorld() {
        // TODO: randomly generate the world
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        Random r = new Random(seed);
        // initialize tiles
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        generateRoom(r, world);
        return world;
    }

    public void generateRoom(Random r, TETile[][] world) {
        final int limit = 100;
        for (int i = 0; i < limit; i++) {
            int bottomLeftX = RandomUtils.uniform(r, 1,  WIDTH - 2);
            int bottomLeftY = RandomUtils.uniform(r, 1, HEIGHT - 2);
            int upperRightX = RandomUtils.uniform(r, bottomLeftX + 1, Integer.min(WIDTH - 1, bottomLeftX + ROOMMAXLEN));
            int upperRightY = RandomUtils.uniform(r, bottomLeftY + 1, Integer.min(HEIGHT - 1, bottomLeftY + ROOMMAXLEN));
            int[] bl = new int[2];
            int[] ur = new int[2];
            bl[0] = bottomLeftX;
            bl[1] = bottomLeftY;
            ur[0] = upperRightX;
            ur[1] = upperRightY;
            Room rm = new Room(bl, ur);
            if (!isRoomOverlap(rms, rm)) {
                rms.add(rm);
            }
        }
        fillRoom(rms, world);
    }

    private boolean isRoomOverlap(List<Room> rms, Room rm) {
        for (int i = 0; i < rms.size(); i++) {
            Room rmPtr = rms.get(i);
            boolean res;
            res = !(rm.upperRight[0] +1 < rmPtr.bottomLeft[0] || rm.bottomLeft[1] - 1 > rmPtr.upperRight[1]
            || rm.bottomLeft[0] - 1 > rmPtr.upperRight[0] || rm.upperRight[1] + 1 < rmPtr.bottomLeft[1]);
            if (res) {
                return res;
            }
        }
        return false;
    }

    private void fillRoom(List<Room> rms, TETile[][] world) {
        for (int i = 0; i < rms.size(); i++) {
            Room rm = rms.get(i);
            for (int x = rm.bottomLeft[0]; x <= rm.upperRight[0]; x++) {
                for (int y = rm.bottomLeft[1]; y <= rm.upperRight[1]; y++) {
                    world[x][y] = Tileset.FLOOR;
                }
            }
        }
    }

}
