package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    /**
     * Fills the given 2D array of tiles with RANDOM tiles.
     * @param tiles
     */
    public static void fillBoardWithNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    /** Picks a RANDOM tile with a 33% change of being
     *  a wall, 33% chance of being a flower, and 33%
     *  chance of being empty space.
     */
    private static TETile randomBiome() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0: return Tileset.GRASS;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.SAND;
            case 3: return Tileset.MOUNTAIN;
            case 4: return Tileset.TREE;
            default: return Tileset.NOTHING;
        }
    }

    public static void drawRow(TETile[][] tiles, Position p, TETile tile, int length) {
        for (int dx = 0; dx < length; dx += 1) {
            tiles[p.x + dx][p.y] = tile;
        }
    }

    public static void addHexagonHelper(TETile[][] tiles, Position p, TETile tile, int b, int t) {
        Position startOfRow = p.shift(b, 0);
        drawRow(tiles, startOfRow, tile, t);

        /** print nextRow */
        if (b > 0) {
            Position nextRow = p.shift(0, -1);
            addHexagonHelper(tiles, nextRow, tile,b - 1, t + 2);
        }


        /** print reflection */
        Position startOfReflectedRow = startOfRow.shift(0, -(2 * b + 1));
        drawRow(tiles, startOfReflectedRow, tile, t);
    }

    public static void addHexagon(TETile[][] tiles, Position p, TETile t, int size) {
        if (size < 2) {
            return;
        }
        addHexagonHelper(tiles, p, t, size -1 , size);
    }

    /**
     * Gets the position of the bottom neighbor of a hexagon at position p.
     * N is the size of the hexagons we are tessellating.
     */
    public static Position getBottomNeighbor(Position p, int n) {
        return p.shift(0, -2 * n);
    }

    /**
     * Gets the position of the top right neighbor of a hexagon at position p.
     * N is the size of the hexagons we are tessellating.
     */
    public static Position getTopRightNeighbor(Position p, int n) {
        return p.shift(2 * n -1, n);
    }
    /**
     * Gets the position of the bottom right neighbor of a hexagon at position p.
     * N is the size of the hexagons we are tessellating.
     */
    public static Position getBottomRightNeighbor(Position p, int n) {
        return p.shift(2 * n - 1, -n);
    }

    /**
     * Adds a column of hexagons, each of whose biomes are chosen randomly
     * to the world at position P. Each of the hexagons are of size SIZE
     */
    public static void addHexColumn(TETile[][] tiles, Position p, int size, int num) {
        if (num < 1) return;
        addHexagon(tiles, p, randomBiome(), size);

        if (num > 1) {
            Position bottoNeighbor = getBottomNeighbor(p, size);
            addHexColumn(tiles, bottoNeighbor, size, num - 1);
        }
    }

    private static class Position {
        int x;
        int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Position shift(int dx, int dy) {
            return new Position(this.x + dx, this.y + dy);
        }
    }

    public static void drawWorld(TETile[][] tiles, Position p, int hexSize, int tessSize) {
        // Draw the first hexagon
        addHexColumn(tiles, p , hexSize, tessSize);

        // Expand up and to the right
        for (int i = 1; i < tessSize; i += 1) {
            p = getTopRightNeighbor(p, hexSize);
            addHexColumn(tiles, p , hexSize, tessSize + i);
        }

        // Expand down and to the right
        for (int i = tessSize - 2; i >= 0; i -= 1) {
            p = getBottomRightNeighbor(p, hexSize);
            addHexColumn(tiles, p , hexSize, tessSize + i);
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        fillBoardWithNothing(world);
        Position anchor = new Position(12, 34);
        drawWorld(world, anchor, 3, 4);

        //ter.renderFrame(world, 0);
    }
}
