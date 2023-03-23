package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import java.io.Serializable;
import java.util.Random;

public class PlayerSave implements Serializable {

    public TETile[][] MAP;
    public long SEED;
    public Random RANDOM;
    public String INPUT;
    public User USER;
    public GameTime TIME;

    public PlayerSave(Engine engine, Player player) {
        this.MAP = engine.WORLDFRAME;
        this.RANDOM = engine.RANDOM;
        this.INPUT = engine.INPUT;
        this.USER = new User(player.x, player.y, player.IMAGE);
        this.TIME = engine.TIME;
    }

    public int player_x () {
        return USER.x;
    }

    public int player_y () {
        return USER.y;
    }


    private class User implements Serializable {
        int x;
        int y;

        User(int x, int y, TETile image) {
            this.x = x;
            this.y = y;
        }
    }
}
