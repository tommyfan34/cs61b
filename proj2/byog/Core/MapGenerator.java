package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapGenerator {
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
    public static final int MULTICONNECTS = 1;

    private static class Coordinate {
        int x;
        int y;
        Coordinate(int a, int b) {
            x = a;
            y = b;
        }
    }

    private static class Connect {
        Coordinate coord;
        Region connectTo;
        Connect(Coordinate c, Region rj) {
            coord = c;
            connectTo = rj;
        }
    }

    private static class Region {
        List<Connect> connects;
        boolean connected;
    }

    private static class Hallway extends Region {
        List<Coordinate> coords;
        Hallway() {
            coords = new ArrayList<>();
            connects = new ArrayList<>();
            connected = false;
        }
    }

    private static class Room extends Region {
        Coordinate upperRight;
        Coordinate bottomLeft;
        Room(Coordinate bl, Coordinate ur) {
            upperRight = ur;
            bottomLeft = bl;
            connected = false;
            connects = new ArrayList<>();
        }
    }

    private static class World {
        private List<Room> rms;
        private List<Hallway> hwys;
        private TETile[][] map;

        World() {
            rms = new ArrayList<>();
            hwys = new ArrayList<>();
            map = new TETile[WIDTH][HEIGHT];
        }
    }

    /** Randomly generate the world
     *
     * @return
     */
    public static TETile[][] generateWorld(Random r) {
        World world = new World();
        TETile[][] map = world.map;
        // initialize tiles
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                map[x][y] = Tileset.NOTHING;
            }
        }
        generateRoom(world, r);
        generateMaze(world, r);
        findConnects(world);
        connectRegions(world, r);
        removeDeadEnds(world);
        addDoor(world, r);
        return world.map;
    }

    /** Randomly generate rooms
     *
     * @param r
     * @param world
     */
    private static void generateRoom(World world, Random r) {
        final int limit = 200;
        for (int i = 0; i < limit; i++) {
            int bottomLeftX = 2 * RandomUtils.uniform(r, 0,  (WIDTH - 3) / 2) + 1;
            int bottomLeftY = 2 * RandomUtils.uniform(r, 0, (HEIGHT - 3) / 2) + 1;
            int upperRightX = 2 * RandomUtils.uniform(r, (bottomLeftX + 1) / 2,
                    Integer.min(WIDTH - 1, bottomLeftX + ROOMMAXLEN) / 2) + 1;
            int upperRightY = 2 * RandomUtils.uniform(r, (bottomLeftY + 1) / 2,
                    Integer.min(HEIGHT - 1, bottomLeftY + ROOMMAXLEN) / 2) + 1;
            Room rm = new Room(new Coordinate(bottomLeftX, bottomLeftY),
                    new Coordinate(upperRightX, upperRightY));
            if (!isRoomOverlap(world, rm)) {
                world.rms.add(rm);
            }
        }
        fillRoom(world);
    }

    /** Determine whether two rooms are overlap given a list of
     * Rooms and the room
     * @param rm
     * @return
     */
    private static boolean isRoomOverlap(World world, Room rm) {
        for (int i = 0; i < world.rms.size(); i++) {
            Room rmPtr = world.rms.get(i);
            boolean res;
            res = !(rm.upperRight.x + 1 < rmPtr.bottomLeft.x
                    || rm.bottomLeft.y - 1 > rmPtr.upperRight.y
                    || rm.bottomLeft.x - 1 > rmPtr.upperRight.x
                    || rm.upperRight.y + 1 < rmPtr.bottomLeft.y);
            if (res) {
                return res;
            }
        }
        return false;
    }

    /** Fill room with floor and surrounded it with wall */
    private static void fillRoom(World world) {
        for (int i = 0; i < world.rms.size(); i++) {
            Room rm = world.rms.get(i);
            for (int x = rm.bottomLeft.x; x <= rm.upperRight.x; x++) {
                for (int y = rm.bottomLeft.y; y <= rm.upperRight.y; y++) {
                    world.map[x][y] = Tileset.FLOOR;
                }
            }
            for (int x = rm.bottomLeft.x - 1; x <= rm.upperRight.x + 1; x++) {
                world.map[x][rm.bottomLeft.y - 1] = Tileset.WALL;
                world.map[x][rm.upperRight.y + 1] = Tileset.WALL;
            }
            for (int y = rm.bottomLeft.y; y <= rm.upperRight.y; y++) {
                world.map[rm.bottomLeft.x - 1][y] = Tileset.WALL;
                world.map[rm.upperRight.x + 1][y] = Tileset.WALL;
            }
        }
    }

    /** Maze generation */
    public static void generateMaze(World world, Random r) {
        for (int x = 1; x < WIDTH - 1; x++) {
            for (int y = 1; y < HEIGHT - 1; y++) {
                if (world.map[x][y].equals(Tileset.NOTHING)) {
                    Hallway hwy = new Hallway();
                    Coordinate cor = new Coordinate(x, y);
                    floodFill(world.map, cor, r, hwy);
                    hallwayWall(hwy, world.map);
                    world.hwys.add(hwy);
                }
            }
        }
    }

    /** Flood fill algorithm to generate the maze */
    private static void floodFill(TETile[][] world, Coordinate cor, Random r, Hallway hwy) {
        int x = cor.x;
        int y = cor.y;
        if (world[x][y].equals(Tileset.FLOOR) || world[x][y].equals(Tileset.WALL)) {
            return;
        }
        if (x == 0 || x == WIDTH - 1 || y == 0 || y == HEIGHT - 1) {
            return;
        }
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

    /** Apply direction and distance to coordinate and returns coordinate */
    private static Coordinate applyDir(int dir, int distance, Coordinate cor) {
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

    /** Generate wall for hallways */
    private static void hallwayWall(Hallway hwy, TETile[][] world) {
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

    /** Find the connections between different part */
    public static void findConnects(World world) {
        for (int x = 1; x < WIDTH - 1; x++) {
            for (int y = 1; y < HEIGHT - 1; y++) {
                if (world.map[x][y].equals(Tileset.WALL)) {
                    List<Coordinate> floorCor = new ArrayList<>();
                    for (int i = 0; i < 4; i++) {
                        Coordinate cor = applyDir(i, 1, new Coordinate(x, y));
                        if (world.map[cor.x][cor.y].equals(Tileset.FLOOR)) {
                            floorCor.add(cor);
                        }
                    }
                    if (floorCor.size() == 2) {
                        List<Region> belongsTo = new ArrayList<>();
                        // find the two floor belongs to which room / hallway
                        for (int i = 0; i < 2; i++) {
                            Coordinate cor = floorCor.get(i);
                            for (int j = 0; j < world.rms.size(); j++) {
                                Room rm = world.rms.get(j);
                                if (((cor.x == rm.bottomLeft.x || cor.x == rm.upperRight.x)
                                        && (cor.y <= rm.upperRight.y && cor.y >= rm.bottomLeft.y))
                                        || ((cor.y == rm.bottomLeft.y || cor.y == rm.upperRight.y)
                                        && (cor.x <= rm.upperRight.x
                                        && cor.x >= rm.bottomLeft.x))) {
                                    belongsTo.add(rm);
                                    break;
                                }
                            }
                            for (int j = 0; j < world.hwys.size(); j++) {
                                Hallway hwy = world.hwys.get(j);
                                for (int k = 0; k < hwy.coords.size(); k++) {
                                    Coordinate temp = hwy.coords.get(k);
                                    if (temp.x == cor.x && temp.y == cor.y) {
                                        belongsTo.add(hwy);
                                    }
                                }
                            }
                        }
                        if (belongsTo.size() == 2 && !belongsTo.get(0).equals(belongsTo.get(1))) {
                            for (int i = 0; i < 2; i++) {
                                Region rj = belongsTo.get(i);
                                rj.connects.add(new Connect(new Coordinate(x, y),
                                        belongsTo.get(1 - i)));
                            }
                        }
                    }
                }
            }
        }
    }

    /** Connect rooms and hallways */
    public static void connectRegions(World world, Random r) {
        int index = RandomUtils.uniform(r, world.rms.size());
        List<Connect> conts = new ArrayList<>(world.rms.get(index).connects);
        world.rms.get(index).connected = true;
        while (!conts.isEmpty()) {
            index = RandomUtils.uniform(r, conts.size());
            Connect cont = conts.get(index);
            Coordinate cord = cont.coord;
            if (!cont.connectTo.connected) {
                world.map[cord.x][cord.y] = Tileset.FLOOR;
            }
            for (int i = 0; i < conts.size(); i++) {
                Connect cnt = conts.get(i);
                Coordinate cord1 = cnt.coord;
                Region rj = cnt.connectTo;
                if (rj.connected) {
                    // a slight chance of multi connections to a single region
                    if (RandomUtils.uniform(r, 100) < MULTICONNECTS) {
                        world.map[cord1.x][cord1.y] = Tileset.FLOOR;
                    }
                    // remove this connect from conts
                    conts.remove(i);
                }
            }
            Region rj = cont.connectTo;
            if (!rj.connected) {
                for (int i = 0; i < rj.connects.size(); i++) {
                    conts.add(rj.connects.get(i));
                }
                rj.connected = true;
            }
        }
    }

    /** remove all the dead ends on map
     *
     * @param world
     */
    public static void removeDeadEnds(World world) {
        while (true) {
            boolean noDeadEnds = true;
            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    if (world.map[x][y].equals(Tileset.FLOOR)) {
                        int num = 0;
                        for (int i = 0; i < 4; i++) {
                            Coordinate cor = applyDir(i, 1, new Coordinate(x, y));
                            if (world.map[cor.x][cor.y].equals(Tileset.WALL)) {
                                num++;
                            }
                        }
                        if (num == 3) {
                            world.map[x][y] = Tileset.WALL;
                            noDeadEnds = false;
                        }
                    }
                }
            }
            if (noDeadEnds) {
                break;
            }
        }

        // remove extra walls
        List<Coordinate> toRemove = new ArrayList<>();
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                boolean redundant = true;
                for (int i = 0; i < 8; i++) {
                    Coordinate cord = applyDir(i, 1, new Coordinate(x, y));
                    if (cord.x < 0 || cord.x >= WIDTH || cord.y < 0 || cord.y >= HEIGHT) {
                        continue;
                    }
                    if (!world.map[cord.x][cord.y].equals(Tileset.WALL)) {
                        redundant = false;
                        break;
                    }
                }
                if (redundant) {
                    toRemove.add(new Coordinate(x, y));
                }
            }
        }
        for (int i = 0; i < toRemove.size(); i++) {
            Coordinate cor = toRemove.get(i);
            world.map[cor.x][cor.y] = Tileset.NOTHING;
        }
    }

    /** Randomly add a door to the floor
     *
     * @param world
     * @param r
     */
    private static void addDoor(World world, Random r) {
        List<Coordinate> walls = new ArrayList<>();
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (world.map[x][y].equals(Tileset.WALL)) {
                    boolean hasNothing = false;
                    boolean hasFloor = false;
                    for (int i = 0; i < 4; i++) {
                        Coordinate cor = applyDir(i, 1, new Coordinate(x, y));
                        if (cor.x < 0 || cor.x >= WIDTH || cor.y < 0 || cor.y >= HEIGHT) {
                            hasNothing = true;
                            continue;
                        }
                        if (world.map[cor.x][cor.y].equals(Tileset.FLOOR)) {
                            hasFloor = true;
                        } else if (world.map[cor.x][cor.y].equals(Tileset.NOTHING)) {
                            hasNothing = true;
                        }
                    }
                    if (hasFloor && hasNothing) {
                        walls.add(new Coordinate(x, y));
                    }
                }
            }
        }
        int index = RandomUtils.uniform(r, walls.size());
        Coordinate cor = walls.get(index);
        world.map[cor.x][cor.y] = Tileset.LOCKED_DOOR;
    }
}
