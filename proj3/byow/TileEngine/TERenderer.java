package byow.TileEngine;

import byow.Core.Player;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;

/**
 * Utility class for rendering tiles. You do not need to modify this file. You're welcome
 * to, but be careful. We strongly recommend getting everything else working before
 * messing with this renderer, unless you're trying to do something fancy like
 * allowing scrolling of the screen or tracking the avatar or something similar.
 */
public class TERenderer implements Serializable {
    private static final int TILE_SIZE = 16;
    private int width;
    private int height;
    private int xOffset;
    private int yOffset;

    /**
     * Same functionality as the other initialization method. The only difference is that the xOff
     * and yOff parameters will change where the renderFrame method starts drawing. For example,
     * if you select w = 60, h = 30, xOff = 3, yOff = 4 and then call renderFrame with a
     * TETile[50][25] array, the renderer will leave 3 tiles blank on the left, 7 tiles blank
     * on the right, 4 tiles blank on the bottom, and 1 tile blank on the top.
     * @param w width of the window in tiles
     * @param h height of the window in tiles.
     */
    public void initialize(int w, int h, int xOff, int yOff) {
        this.width = w;
        this.height = h + 2;
        this.xOffset = xOff;
        this.yOffset = yOff;
        StdDraw.setCanvasSize(width * TILE_SIZE, height * TILE_SIZE);
        Font font = new Font("Monaco", Font.BOLD, TILE_SIZE - 2);
        StdDraw.setFont(font);      
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

        StdDraw.clear(new Color(0, 0, 0));

        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    /**
     * Initializes StdDraw parameters and launches the StdDraw window. w and h are the
     * width and height of the world in number of tiles. If the TETile[][] array that you
     * pass to renderFrame is smaller than this, then extra blank space will be left
     * on the right and top edges of the frame. For example, if you select w = 60 and
     * h = 30, this method will create a 60 tile wide by 30 tile tall window. If
     * you then subsequently call renderFrame with a TETile[50][25] array, it will
     * leave 10 tiles blank on the right side and 5 tiles blank on the top side. If
     * you want to leave extra space on the left or bottom instead, use the other
     * initializatiom method.
     * @param w width of the window in tiles
     * @param h height of the window in tiles.
     */
    public void initialize(int w, int h) {
        initialize(w, h, 0, 0);
    }

    /**
     * Takes in a 2d array of TETile objects and renders the 2d array to the screen, starting from
     * xOffset and yOffset.
     *
     * If the array is an NxM array, then the element displayed at positions would be as follows,
     * given in units of tiles.
     *
     *              positions   xOffset |xOffset+1|xOffset+2| .... |xOffset+world.length
     *                     
     * startY+world[0].length   [0][M-1] | [1][M-1] | [2][M-1] | .... | [N-1][M-1]
     *                    ...    ......  |  ......  |  ......  | .... | ......
     *               startY+2    [0][2]  |  [1][2]  |  [2][2]  | .... | [N-1][2]
     *               startY+1    [0][1]  |  [1][1]  |  [2][1]  | .... | [N-1][1]
     *                 startY    [0][0]  |  [1][0]  |  [2][0]  | .... | [N-1][0]
     *
     * By varying xOffset, yOffset, and the size of the screen when initialized, you can leave
     * empty space in different places to leave room for other information, such as a GUI.
     * This method assumes that the xScale and yScale have been set such that the max x
     * value is the width of the screen in tiles, and the max y value is the height of
     * the screen in tiles.
     * @param world the 2D TETile[][] array to render
     */
    public void renderFrame(TETile[][] world, Player player, long time) {
        int numXTiles = world.length;
        int numYTiles = world[0].length;
        Font font = new Font("Monaco", Font.BOLD, TILE_SIZE - 2);
        StdDraw.setFont(font);
        StdDraw.clear(new Color(0, 0, 0));
        for (int x = 0; x < numXTiles; x += 1) {
            for (int y = 0; y < numYTiles; y += 1) {
                if (world[x][y] == null) {
                    throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y
                            + " is null.");
                }
                world[x][y].draw(x + xOffset, y + yOffset);
            }
        }
        drawHug(world, time, player);
        StdDraw.show();
    }

    public void renderFrameWithSight(TETile[][] world, Player player, long time) {
        int numXTiles = world.length;
        int numYTiles = world[0].length;
        Font font = new Font("Monaco", Font.BOLD, TILE_SIZE - 2);
        StdDraw.setFont(font);
        StdDraw.clear(new Color(0, 0, 0));
        int playerX = player.getX();
        int playerY = player.getY();
        for (int x = playerX - 2 ; x < playerX + 3; x += 1) {
            if (x >= 0 && x < numXTiles) {
                for (int y = playerY - 2;  y < playerY + 3; y += 1) {
                    if (y >= 0 && y < numYTiles) {
                        if (world[x][y] == null) {
                            throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y
                                    + " is null.");
                        }
                        world[x][y].draw(x + xOffset, y + yOffset);
                    }
                }
            }

        }
        drawHug(world, time, player);
        StdDraw.show();
    }

    public void drawOpenMenu() {
        StdDraw.clear(new Color(0, 0, 0));
        StdDraw.setPenColor(Color.CYAN);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.text(this.width / 2, this.height / 2, "New Game(n)");
        StdDraw.text(this.width / 2, this.height / 2 - 3, "Load Game(l)");
        StdDraw.text(this.width / 2, this.height / 2 - 6, "Control(c)");
        StdDraw.text(this.width / 2, this.height / 2 - 9, "Quit to Desktop(q)");
        StdDraw.show();
    }

    public void drawSetup(String input) {
        StdDraw.clear(new Color(0, 0, 0));
        StdDraw.setPenColor(Color.CYAN);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.text(this.width / 2, this.height / 2 + 2, "Please Enter a random seed for generate a dungeon");
        StdDraw.text(this.width / 2, this.height / 2, "Your Seed: " + input);
        StdDraw.show();
    }

    public void drawHug(TETile[][] world, long time, Player player) {
        Font font = new Font("Monaco", Font.BOLD, 35);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.CYAN);

        StdDraw.line(0 + xOffset, this.height - 3 + yOffset,
                this.width + xOffset, this.height - 3 + yOffset);
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        if (mouseX < width && mouseY < height - 2) {
            StdDraw.text(0 + 4.5, this.height - 2 + yOffset, world[mouseX][mouseY].description());
        }

        String remainingTime = String.format("%s:%s", time / 60, time % 60);
        StdDraw.text(width - 4.5, this.height - 2 + yOffset, remainingTime);
        if (!player.findKey()) {
            StdDraw.text(width / 2, this.height - 2 + yOffset, "Find the Key first!");
        } else {
            StdDraw.text(width / 2, this.height - 2 + yOffset, "You have found the key, Now find the exit!");
        }
    }

    public void drawEndingScreen(long time) {
        StdDraw.clear(new Color(0, 0, 0));
        StdDraw.setPenColor(Color.CYAN);
        Font font = new Font("Monaco", Font.BOLD, 40);
        StdDraw.text(this.width / 2, this.height / 2 + 1,
                "Congratulations! You escaped the dungeon! You are a master adventurer!");

        StdDraw.text(this.width / 2, this.height / 2 - 1.5,
                String.format("You've only spend %s:%s", time / 60, time % 60));
        StdDraw.show();
    }

    public void drawControlScreen() {
        StdDraw.clear(new Color(0, 0, 0));
        StdDraw.setPenColor(Color.CYAN);
        Font font = new Font("Monaco", Font.BOLD, 25);
        StdDraw.setFont(font);
        StdDraw.text(this.width / 2, this.height / 2, "Up (w)");
        StdDraw.text(this.width / 2, this.height / 2 - 2, "Down (s)");
        StdDraw.text(this.width / 2, this.height / 2 - 4, "Left (a)");
        StdDraw.text(this.width / 2, this.height / 2 - 6, "Right (d)");
        StdDraw.text(this.width / 2, this.height / 2 - 8, "Map sight / Player sight (l)");
        StdDraw.text(this.width / 2, this.height / 2 - 10, "Quit to Menu & Save (q)");
        StdDraw.text(this.width / 2, this.height / 2 - 15, "Back to Main menu (ESC)");
        StdDraw.show();
    }
}
