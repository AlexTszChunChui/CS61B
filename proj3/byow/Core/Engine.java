package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

public class Engine implements Serializable {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 150;
    public static final int HEIGHT = 80;
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File SAVE = new File(CWD, "save");
    public long SEED;
    public Random RANDOM;
    public TETile[][] WORLDFRAME;
    public Player PLAYER;
    public boolean START = false;
    public String INPUT = "";

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
        if (!START) {
            ter.drawOpenMenu();
        }
        InputSource inputSource = new KeyboardInputSource();
        while (inputSource.possibleNextInput()) {
            char c = inputSource.getNextKey();
            action(c);
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
        input = input.toUpperCase();
        drawMap(input);
        String command = input.replaceAll("N[0-9]+S", "");
        InputSource inputSource = new StringInputSource(command);
        while (inputSource.possibleNextInput()) {
            char c = inputSource.getNextKey();
            if (c == 'Q') {
                action(c);
                break;
            }
            action(c);
        }
        interactWithKeyboard();
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

    public void action(char c) {
        if (c == 'Q' && START) {
            save();
            ter.drawOpenMenu();
        }
        else {
            switch (c) {
                case 'W':
                    if (START) {
                        PLAYER.move(0, 1);
                        //ter.renderFrame(WORLDFRAME);
                    }
                    break;
                case 'S':
                    if (START) {
                        PLAYER.move(0, -1);
                        //ter.renderFrame(WORLDFRAME);
                    } else {
                        drawMap(INPUT);
                        START = true;
                    }
                    break;
                case 'A':
                    if (START) {
                        PLAYER.move(-1, 0);
                        //ter.renderFrame(WORLDFRAME);
                    }
                    break;
                case 'D':
                    if (START) {
                        PLAYER.move(1, 0);
                        //ter.renderFrame(WORLDFRAME);
                    }
                    break;
                case 'N':
                    if (!START) {
                        //ter.drawSetup(INPUT);
                    }
                    break;
                case 'L':
                    if (!START) {
                        loadGame();
                    }
                    break;
                default:
                    if (!START) {
                        INPUT += c;
                        ter.drawSetup(INPUT);
                    }
                    break;
            }
        }
    }

    public void drawMap(String input) {
        String numberOnly = input.replaceAll("[^0-9]", "");
        if (numberOnly.length() < 1) {
            return;
        }
        SEED = Long.parseLong(numberOnly);
        RANDOM = new Random(SEED);

        Dungeon_Map map = new Dungeon_Map(WIDTH, HEIGHT, 30, 5, 18);
        PLAYER = map.drawDungeon(WORLDFRAME, RANDOM);

        ter.renderFrame(WORLDFRAME);
    }


    public void save() {
        if (!SAVE.exists()) {
            try {
                SAVE.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        PlayerSave save = new PlayerSave(this, PLAYER);
        FileManagement.writeObject(SAVE, save);
        WORLDFRAME = new TETile[WIDTH][HEIGHT];
        RANDOM = null;
        INPUT = "";
        START = false;
        PLAYER = null;

        fillBoardWithNothing(WORLDFRAME);
    }

    public void loadGame() {
        if (!SAVE.exists()) {

        }
        PlayerSave save = (PlayerSave) FileManagement.readObject(SAVE);
        this.WORLDFRAME = save.MAP;
        this.PLAYER = new Player(WORLDFRAME, save.player_x(), save.player_y());
        this.RANDOM = save.RANDOM;
        this.INPUT = save.INPUT;
        this.SEED = save.SEED;
        this.START = true;
        //ter.renderFrame(WORLDFRAME);

    }

    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.ter.initialize(WIDTH, HEIGHT);
        engine.interactWithKeyboard();
    }
}
