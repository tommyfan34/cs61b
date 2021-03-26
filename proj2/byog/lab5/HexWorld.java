package byog.lab5;
import org.junit.Test;
import static org.junit.Assert.*;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 * tiles is the world to add hexagon to.
 * len is the length of side of hexagon.
 * posX is the x value of the left lower position
 * of the smallest rectangular that contains the hexagon.
 * posY is the y value of the left lower position
 * of the smallest rectangular that contains the hexagon.
 * pat is the TETile pattern.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private static final int LEN = 4;
    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    private static class Coordinate {
        public int x;
        public int y;
        public Coordinate (int a, int b) {
            x = a;
            y = b;
        }
    }

    /** Picks a RANDOM tile with a 33% change of being
     *  a wall, 33% chance of being a flower, and 33%
     *  chance of being empty space.
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.GRASS;
            case 3: return Tileset.TREE;
            case 4: return Tileset.SAND;
            default: return Tileset.NOTHING;
        }
    }

    public static boolean helperDeterm(int len, int posX, int posY, int X, int Y) {
        int deltaX = X - posX;
        int deltaY = Y - posY;
        if (deltaX < 0 || deltaY < 0 || deltaX > 3 * len - 3 || deltaY > 2 * len - 1) {
            return false;
        }
        if (deltaY <= len - 1) {
            if (deltaX >= (len - 1 - deltaY) && deltaX <= (2 * len - 2 + deltaY)) {
                return true;
            } else {
                return false;
            }
        } else {
            if (deltaX >= (deltaY - len) && deltaX <= (4 * len - 3 - deltaY)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static void addHexagon(TETile[][] tiles, int len, int posX, int posY, TETile pat) {
        int height = tiles[0].length;
        int width = tiles.length;

        if ((posX + 3 * len - 2) > width || (posY + 2 * len) > height) {
            throw new RuntimeException("the world is too small");
        }
        for (int i = posX; i < posX + 3 * len - 2; i++) {
            for (int j = posY; j < posY + 2 * len; j++) {
                if (helperDeterm(len, posX, posY, i, j)) {
                    tiles[i][j] = pat;
                }
            }
        }
    }

    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        Coordinate[] cor = new Coordinate[19];

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];

        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        for (int i = 0; i < 3; i++) {
            cor[i] = new Coordinate(0, 2*LEN*(i+1));
        }
        for (int i = 0; i < 4; i++) {
            cor[3+i] = new Coordinate(2*LEN-1, LEN+2*LEN*i);
        }
        for (int i = 0; i < 5; i++) {
            cor[7+i] = new Coordinate(4*LEN-2, 2*LEN*i);
        }
        for (int i = 0; i < 4; i++) {
            cor[12+i] = new Coordinate(6*LEN-3, LEN+2*LEN*i);
        }
        for (int i = 0; i < 3; i++) {
            cor[16+i] = new Coordinate(8*LEN-4, 2*LEN*(i+1));
        }

        for (int i = 0; i < 19; i++) {
            addHexagon(world, LEN, cor[i].x, cor[i].y, randomTile());
        }

        ter.renderFrame(world);
    }

}
