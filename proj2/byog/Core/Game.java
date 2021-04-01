package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;

import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
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
    private long seed;
    private int seedTextIndention = 0;
    private static final String PATH = "./game.txt";
    private enum States {
        WELCOME, TO_QUIT, TO_INPUT_SEED, GAME
    }
    private States state;
    private static final int STRINGMODE = 0;
    private static final int KEYBOARDMODE = 1;
    private GameState gameState;
    private MapGenerator.Coordinate player;


    public Game() {
        ter.initialize(byog.Core.MapGenerator.WIDTH, byog.Core.MapGenerator.HEIGHT);
        state = States.WELCOME;
        gameState = new GameState();
        StdDraw.enableDoubleBuffering();
    }

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                processChar(c, KEYBOARDMODE);
            }
            if (state == States.GAME) {
                Font font = new Font("Sans Serif", Font.PLAIN, 16);
                StdDraw.setFont(font);
                ter.renderFrame(gameState.world);
                renderHUD();
            } else if (state == States.WELCOME) {
                drawMenu();
            }
        }
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
        StringCharacterIterator it = new StringCharacterIterator(input);
        char cur;

        while (it.current() != StringCharacterIterator.DONE) {
            cur = it.current();
            processChar(cur, STRINGMODE);
            it.next();
        }
        return gameState.world;
    }

    private GameState loadWorld() {
        File f = new File(PATH);
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

    private void saveWorld() {
        File f = new File(PATH);
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(gameState);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    private void drawMenu() {
        StdDraw.setPenColor(StdDraw.WHITE);
        Font font1 = new Font("Sans Serif", Font.PLAIN, 70);
        Font font2 = new Font("Sans Serif", Font.PLAIN, 50);
        StdDraw.setFont(font1);
        StdDraw.text(40, 25, "CS61B: THE GAME");
        StdDraw.setFont(font2);
        StdDraw.text(40, 20, "New Game (N)");
        StdDraw.text(40, 15, "Load Game (L)");
        StdDraw.text(40, 10, "Quit (Q)");
        StdDraw.show();
    }

    private void processChar(char c, int mode) {
        if (c == 'n' || c == 'N') {
            if (state == States.WELCOME) {
                if (mode == KEYBOARDMODE) {
                    Font font = new Font("Sans Serif", Font.PLAIN, 50);
                    StdDraw.setFont(font);
                    StdDraw.text(30, 5, "Type seed: ");
                }
                seed = 0;
                state = States.TO_INPUT_SEED;
            }
        } else if (Character.isDigit(c)) {
            if (state == States.TO_INPUT_SEED) {
                if (mode == KEYBOARDMODE) {
                    Font font = new Font("Sans Serif", Font.PLAIN, 50);
                    StdDraw.setFont(font);
                    StdDraw.text(40 + 3 * seedTextIndention, 5, String.valueOf(c));
                    seedTextIndention++;
                }
                seed = seed * 10 + c - '0';
            }
        } else if (c == 's' || c == 'S') {
            if (state == States.TO_INPUT_SEED) {
                gameState.rdm = new Random(seed);
                gameState.world = MapGenerator.generateWorld(gameState.rdm);
                player = getPlayer();
                state = States.GAME;
            } else if (state == States.GAME) {
                movePlayer(MapGenerator.SOUTH);
            }
        } else if (c == ':') {
            if (state == States.GAME) {
                state = States.TO_QUIT;
            }
        } else if (c == 'q' || c == 'Q') {
            if (state == States.TO_QUIT) {
                saveWorld();
                System.exit(0);
            } else if (state == States.WELCOME) {
                System.exit(0);
            }
        } else if (c == '\b') {
            if (state == States.TO_INPUT_SEED) {
                if (mode == KEYBOARDMODE) {
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.filledSquare(40 + 3 * seedTextIndention - 3, 5, 2);
                    StdDraw.setPenColor(StdDraw.WHITE);
                }
                if (seedTextIndention != 0) {
                    seedTextIndention--;
                }
                seed /= 10;
            }
        } else if (c == 'l' || c == 'L') {
            if (state == States.WELCOME) {
                gameState = loadWorld();
                player = getPlayer();
                state = States.GAME;
            }
        } else if (c == 'w' || c == 'W') {
            if (state == States.GAME) {
                movePlayer(MapGenerator.NORTH);
            }
        } else if (c == 'a' || c == 'A') {
            if (state == States.GAME) {
                movePlayer(MapGenerator.WEST);
            }
        } else if (c == 'd' || c == 'D') {
            if (state == States.GAME) {
                movePlayer(MapGenerator.EAST);
            }
        }
    }

    private MapGenerator.Coordinate getPlayer() {
        for (int x = 0; x < MapGenerator.WIDTH; x++) {
            for (int y = 0; y < MapGenerator.HEIGHT; y++) {
                if (gameState.world[x][y].equals(Tileset.PLAYER)) {
                    return new MapGenerator.Coordinate(x, y);
                }
            }
        }
        return null;
    }

    private void movePlayer(int direction) {
        MapGenerator.Coordinate newCor = MapGenerator.applyDir(direction, 1, player);
        if (gameState.world[newCor.x][newCor.y].equals(Tileset.FLOOR)) {
            gameState.world[player.x][player.y] = Tileset.FLOOR;
            player = newCor;
            gameState.world[newCor.x][newCor.y] = Tileset.PLAYER;
        }
    }

    private void renderHUD() {
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        String desc = gameState.world[mouseX][mouseY].description();
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.textLeft(2, MapGenerator.HEIGHT - 1, desc);
        StdDraw.show();
        StdDraw.pause(10);
    }
}
