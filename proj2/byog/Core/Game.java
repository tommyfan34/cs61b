package byog.Core;

import byog.SaveDemo.World;
import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.io.*;
import java.text.StringCharacterIterator;
import java.util.*;

public class Game {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    // width and height should be odd
    public static final int WIDTH = 81;
    public static final int HEIGHT = 31;
    public static final int ROOMMAXLEN = 9;
    public static final int WINDY = 40;
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int WEST = 2;
    public static final int EAST = 3;
    public static final int NORTHWEST = 4;
    public static final int NORTHEAST = 5;
    public static final int SOUTHWEST = 6;
    public static final int SOUTHEAST = 7;
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
     * @source https://indienova.com/indie-game-development/rooms-and-mazes-a-procedural-dungeon-generator/#iah-5
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
        List<Coordinate> coords;
        public Hallway() {
            coords = new ArrayList<>();
        }
    }

    private class Coordinate {
        int x;
        int y;
        public Coordinate(int a, int b) {
            x = a;
            y = b;
        }
    }

    private class Room {
        Coordinate upperRight;
        Coordinate bottomLeft;
        public Room(Coordinate bl, Coordinate ur) {
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
        generateMaze(world, r);
        return world;
    }

    public void generateRoom(Random r, TETile[][] world) {
        final int limit = 200;
        for (int i = 0; i < limit; i++) {
            int bottomLeftX = 2 * RandomUtils.uniform(r, 0,  (WIDTH - 3) / 2) + 1;
            int bottomLeftY = 2 * RandomUtils.uniform(r, 0, (HEIGHT - 3) / 2) + 1;
            int upperRightX = 2 * RandomUtils.uniform(r, (bottomLeftX + 1) / 2, Integer.min(WIDTH - 1, bottomLeftX + ROOMMAXLEN) / 2) + 1;
            int upperRightY = 2 * RandomUtils.uniform(r, (bottomLeftY + 1) / 2, Integer.min(HEIGHT - 1, bottomLeftY + ROOMMAXLEN) / 2) + 1;
            Room rm = new Room(new Coordinate(bottomLeftX, bottomLeftY), new Coordinate(upperRightX, upperRightY));
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
            res = !(rm.upperRight.x +1 < rmPtr.bottomLeft.x || rm.bottomLeft.y - 1 > rmPtr.upperRight.y
            || rm.bottomLeft.x - 1 > rmPtr.upperRight.x || rm.upperRight.y + 1 < rmPtr.bottomLeft.y);
            if (res) {
                return res;
            }
        }
        return false;
    }

    private void fillRoom(List<Room> rms, TETile[][] world) {
        for (int i = 0; i < rms.size(); i++) {
            Room rm = rms.get(i);
            for (int x = rm.bottomLeft.x; x <= rm.upperRight.x; x++) {
                for (int y = rm.bottomLeft.y; y <= rm.upperRight.y; y++) {
                    world[x][y] = Tileset.FLOOR;
                }
            }
            for (int x = rm.bottomLeft.x - 1; x <= rm.upperRight.x + 1; x++) {
                world[x][rm.bottomLeft.y - 1] = Tileset.WALL;
                world[x][rm.upperRight.y + 1] = Tileset.WALL;
            }
            for (int y = rm.bottomLeft.y; y <= rm.upperRight.y; y++) {
                world[rm.bottomLeft.x - 1][y] = Tileset.WALL;
                world[rm.upperRight.x + 1][y] = Tileset.WALL;
            }
        }
    }

    public void generateMaze(TETile[][] world, Random r) {
        for (int x = 1; x < WIDTH - 1; x++) {
            for (int y = 1; y < HEIGHT - 1; y++) {
                if (world[x][y].equals(Tileset.NOTHING)) {
                    Hallway hwy = new Hallway();
                    Coordinate cor = new Coordinate(x, y);
                    floodFill(world, cor, r, hwy);
                    hallwayWall(hwy, world);
                    hwys.add(hwy);
                }
            }
        }
    }

    private void floodFill(TETile[][] world, Coordinate cor, Random r, Hallway hwy) {
        int x = cor.x;
        int y = cor.y;
        if (world[x][y].equals(Tileset.FLOOR) || world[x][y].equals(Tileset.WALL)) {
            return;
        }
//        boolean surroundedByWall = true;
//        for (int i = 0; i < 4; i++) {
//            Coordinate temp = applyDir(i, 1, cor);
//            if (!world[temp.x][temp.y].equals(Tileset.WALL)) {
//                surroundedByWall = false;
//            }
//        }
//        if (surroundedByWall) {
//            return;
//        }
        if (x == 0 || x == WIDTH - 1 || y == 0 || y == HEIGHT - 1) {
            return;
        }
        //hwy.coords.add(new Coordinate(x, y));
        ArrayDeque<Coordinate> stack = new ArrayDeque<>();
        stack.add(cor);
        hwy.coords.add(cor);
        int lastDir = RandomUtils.uniform(r, 4);
        world[x][y] = Tileset.FLOOR;
        while (!stack.isEmpty()) {
            cor = stack.getLast();
            List<Integer> availableDir = new ArrayList<>();
            int dir;
            for (int i = 0; i < 4; i++) {
                Coordinate cord1 = applyDir(i, 1, cor);
                Coordinate cord2 = applyDir(i, 2, cor);
                if (cord2.x <= 0 || cord2.x >= WIDTH - 1 || cord2.y <= 0 || cord2.y >= HEIGHT - 1) {
                    continue;
                }
                if (world[cord2.x][cord2.y].equals(Tileset.NOTHING)) {
                    availableDir.add(i);
                }
            }
            if (!availableDir.isEmpty()) {
                if (availableDir.contains(lastDir) && RandomUtils.uniform(r, 100) > WINDY) {
                    dir = lastDir;
                } else {
                    dir = availableDir.get(RandomUtils.uniform(r, availableDir.size()));
                }
                Coordinate cord1 = applyDir(dir, 1, cor);
                Coordinate cord2 = applyDir(dir, 2, cor);
                world[cord1.x][cord1.y] = Tileset.FLOOR;
                world[cord2.x][cord2.y] = Tileset.FLOOR;
                stack.addLast(cord2);
                if (!hwy.coords.contains(cord1)) {
                    hwy.coords.add(cord1);
                }
                if (!hwy.coords.contains(cord2)) {
                    hwy.coords.add(cord2);
                }
                lastDir = dir;
            } else {
                stack.removeLast();
            }
        }
    }

    private Coordinate applyDir(int dir, int distance, Coordinate cor) {
        switch (dir) {
            case NORTH:
                return new Coordinate(cor.x, cor.y + distance);
            case SOUTH:
                return new Coordinate(cor.x, cor.y - distance);
            case WEST:
                return new Coordinate(cor.x - distance, cor.y);
            case EAST:
                return new Coordinate(cor.x + distance, cor.y);
            case NORTHWEST:
                return new Coordinate(cor.x - 1, cor.y + 1);
            case NORTHEAST:
                return new Coordinate(cor.x + 1, cor.y + 1);
            case SOUTHWEST:
                return new Coordinate(cor.x - 1, cor.y - 1);
            case SOUTHEAST:
                return new Coordinate(cor.x + 1, cor.y - 1);
            default:
                return null;
        }
    }

    private void hallwayWall(Hallway hwy, TETile[][] world) {
        for (int i = 0; i < hwy.coords.size(); i++) {
            Coordinate cor = hwy.coords.get(i);
            for (int j = 0; j < 8; j++) {
                Coordinate cor2 = applyDir(j, 1, cor);
                if (world[cor2.x][cor2.y].equals(Tileset.NOTHING)) {
                    world[cor2.x][cor2.y] = Tileset.WALL;
                }
            }
        }
    }
}
