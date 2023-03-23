package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Engine implements Serializable {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 140;
    public static final int HEIGHT = 80;
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File SAVE = new File(CWD, "save");
    public boolean START = false;
    public boolean FOV = false;
    public Random RANDOM;
    public TETile[][] WORLDFRAME;
    public Player PLAYER;
    public String INPUT = "";
    public GameTime TIME;

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
            if (START) {
                if (TIME.timesUp()) {
                    gameOver();
                } else if (PLAYER.escaped()) {
                    gameComplete();
                } else {
                    renderGamePlay(TIME.timesUsed());
                }
            }
            char c = inputSource.getNextKey();
            if (c != '\0') {
                action(c);
            }
        }
    }

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
        } else if (c == 'Q' && !START) {
            System.exit(0);
        } else {
            switch (c) {
                case 'W':
                    if (START) {
                        PLAYER.move(0, 1);
                    }
                    break;
                case 'S':
                    if (START) {
                        PLAYER.move(0, -1);
                    } else {
                        drawMap(INPUT);
                        START = true;
                        TIME = new GameTime(180);
                    }
                    break;
                case 'A':
                    if (START) {
                        PLAYER.move(-1, 0);
                    }
                    break;
                case 'D':
                    if (START) {
                        PLAYER.move(1, 0);
                    }
                    break;
                case 'N':
                    if (!START) {
                        ter.drawSetup(INPUT);
                    }
                    break;
                case 'L':
                    if (!START) {
                        loadGame();
                    }
                    else {
                        FOV = !FOV;
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
        long seed = Long.parseLong(numberOnly);
        RANDOM = new Random(seed);

        Dungeon_Map map = new Dungeon_Map(WIDTH, HEIGHT, 100, 5, 18);
        PLAYER = map.drawDungeon(WORLDFRAME, RANDOM);

    }

    public void renderGamePlay(long timeSpent) {
        if (!FOV) {
            ter.renderFrame(WORLDFRAME, timeSpent);
        }
        else {
            ter.renderFrameWithSight(WORLDFRAME, PLAYER , timeSpent);
        }
    }

    public void gameOver() {
        reset();
        ter.drawOpenMenu();
    }

    public void gameComplete() {
        ter.drawEndingScreen(TIME.timesUsed());
        reset();
        try {
            Thread.sleep(15000);
            ter.drawOpenMenu();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

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
        reset();

        fillBoardWithNothing(WORLDFRAME);
    }

    private void reset() {
        WORLDFRAME = new TETile[WIDTH][HEIGHT];
        RANDOM = null;
        INPUT = "";
        START = false;
        PLAYER = null;
    }

    public void loadGame() {
        if (!SAVE.exists()) {
            System.exit(0);
        }
        PlayerSave save = (PlayerSave) FileManagement.readObject(SAVE);
        this.WORLDFRAME = save.MAP;
        this.PLAYER = new Player(WORLDFRAME, save.player_x(), save.player_y());
        this.RANDOM = save.RANDOM;
        this.INPUT = save.INPUT;
        this.TIME = save.TIME;

        TIME.restart();
        this.START = true;
    }

    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.ter.initialize(WIDTH, HEIGHT);
        engine.interactWithKeyboard();
    }
}
