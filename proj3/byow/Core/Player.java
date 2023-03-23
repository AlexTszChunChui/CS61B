package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;

public class Player implements Serializable {
    int x;
    int y;
    TETile[][] MAP;
    TETile IMAGE;

    public Player (TETile[][] tiles, int x, int y) {
        this.x = x;
        this.y = y;
        this.MAP = tiles;
        this.IMAGE = Tileset.AVATAR;

        tiles[x][y] = IMAGE;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void move(int dx, int dy) {
        if (MAP[x + dx][y + dy].character() == Tileset.FLOOR.character()) {
            MAP[x + dx][y + dy] = IMAGE;
            MAP[x][y] = Tileset.FLOOR;
            this.x += dx;
            this.y += dy;
        }
    }
}
