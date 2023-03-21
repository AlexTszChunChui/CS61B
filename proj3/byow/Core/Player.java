package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Player {
    int x;
    int y;
    TETile[][] MAP;
    TETile IMAGE;

    public Player (TETile[][] tiles, int x, int y) {
        this.x = x;
        this.y = y;
        this.MAP = tiles;
        this.IMAGE = Tileset.TREE;

        tiles[x][y] = IMAGE;
    }

    public void move(int dx, int dy) {
        if (MAP[x + dx][y + dy].equals(Tileset.FLOOR)) {
            MAP[x + dx][y + dy] = IMAGE;
            MAP[x][y] = Tileset.FLOOR;
            this.x += dx;
            this.y += dy;
        }
    }
}
