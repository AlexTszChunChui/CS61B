package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

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
    public TETile[][] WORLDFRAME;
    public boolean START = false;

    public Engine() {
        ter.initialize(WIDTH, HEIGHT);
        WORLDFRAME = new TETile[WIDTH][HEIGHT];
        fillBoardWithNothing(WORLDFRAME);
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        while (true) {
            ter.drawOpenMenu();
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                System.out.println(c);
                switch(c) {
                    case 'N':
                        getSeed();
                    default:
                }
            }
        }
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
        if (numberOnly.length() < 1) {
            return null;
        }
        SEED = Long.parseLong(numberOnly);
        RANDOM = new Random(SEED);

        Dungeon_Map map = new Dungeon_Map(WIDTH, HEIGHT, 30, 5, 18);
        Player player = map.drawDungeon(WORLDFRAME, RANDOM);


        ter.renderFrame(WORLDFRAME);
        trackPlayerCommand(player);

        return WORLDFRAME;
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

    public void getSeed() {
        String seed = "";
        boolean finished = false;
        while (!finished) {
            ter.drawSetup(seed);
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                String command = Character.toString(c);
                System.out.println(command);
                switch (command) {
                    case "S" :
                        if (seed.length() > 0) {
                            interactWithInputString(seed);
                            finished = true;
                            break;
                        }
                    default:
                        seed += command;
                }

            }
        }
    }

    public void trackPlayerCommand(Player player) {
        while (true) {
            ter.renderFrame(WORLDFRAME);
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (c == 'Q') {
                    break;
                }
                switch(c) {
                    case 'W':
                        player.move(0, 1);
                        break;
                    case 'S':
                        player.move(0, -1);
                        break;
                    case 'A':
                        player.move(-1, 0);
                        break;
                    case 'D':
                        player.move(1, 0);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void preProcess(char c) {

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

    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.ter.initialize(WIDTH, HEIGHT);
        engine.interactWithKeyboard();
    }
}
