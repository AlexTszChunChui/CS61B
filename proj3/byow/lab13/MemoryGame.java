package byow.lab13;

import byow.Core.RandomUtils;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(60, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        this.rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        int x = 0;
        String randomString = "";
        while (x < n) {
            int index = rand.nextInt(CHARACTERS.length);
            randomString += CHARACTERS[index];
            x += 1;
        }
        return randomString;
    }

    public void drawFrame(String s) {
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.CYAN);
        StdDraw.text(this.width / 2, this.height / 2, s);
        StdDraw.text(5, this.height - 1.5, "Round: " + Integer.toString(this.round));
        if (playerTurn) {
            StdDraw.text(this.width / 2, this.height - 1.5, "Type!");
        }
        else {
            StdDraw.text(this.width / 2, this.height - 1.5, "Watch!");
        }

        int dice = rand.nextInt(ENCOURAGEMENT.length);
        StdDraw.text(this.width - 8, this.height - 1.5, ENCOURAGEMENT[dice]);
        StdDraw.line(0, this.height - 2.5, this.width, this.height - 2.5);
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        int x = 0;
        while (x < letters.length()) {
            String letter = Character.toString(letters.charAt(x));
            drawFrame(letter);
            try {
                Thread.sleep(1000);
                drawFrame("");
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            x += 1;
        }
    }

    public String solicitNCharsInput(int n) {
        String typed = "";
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                String new_char = Character.toString(StdDraw.nextKeyTyped());
                drawFrame(new_char);
                typed += new_char;
            }
            if (typed.length() == n) {
                break;
            }
        }
        return typed;
    }

    public void startGame() {

        this.round = 1;
        this.gameOver = false;

        while (!this.gameOver) {
            drawFrame("Round: " + Integer.toString(this.round));
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            String quiz = generateRandomString(round);
            flashSequence(quiz);
            playerTurn = true;
            drawFrame("");
            String player_input = solicitNCharsInput(round);
            if (player_input.equals(quiz)) {
                round += 1;
                playerTurn = false;
            }
            else {
                gameOver = true;
                drawFrame("Game Over! You made it to round: " + Integer.toString(round));
            }
        }
    }

}
