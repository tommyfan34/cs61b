package byog.Core;

import byog.TileEngine.TETile;

import java.io.Serializable;
import java.util.Random;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    public TETile[][] world;
    public Random rdm;

    public GameState() {
        world = null;
        rdm = null;
    }

    public GameState(TETile[][] w, Random r) {
        world = w;
        rdm = r;
    }
}
