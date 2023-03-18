package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.lab12.HexWorld;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 150;
    public static final int HEIGHT = 80;
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File SAVE = new File(CWD, "save");
    public long SEED;
    public Random RANDOM;


    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        // Split the user input into seed and other command
        String numberOnly = input.replaceAll("[^0-9]", "");
        SEED= Long.parseLong(numberOnly);
        RANDOM = new Random(SEED);

        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        fillBoardWithNothing(finalWorldFrame);
        Dungeon_Map map = new Dungeon_Map(WIDTH, HEIGHT, 30, 5, 18);
        map.drawDungeon(finalWorldFrame, RANDOM);
        return finalWorldFrame;
    }

    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = engine.interactWithInputString("N1006S");
        engine.ter.renderFrame(world);
    }

    public static void fillBoardWithNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    public static void autoSave(TETile[][] tiles) {
        if (!SAVE.exists()) {
            try {
                SAVE.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
