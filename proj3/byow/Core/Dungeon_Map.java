package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @source http://rogueliketutorials.com/about/
 */
public class Dungeon_Map {
    private int WIDTH;
    private int HEIGHT;
    private int MAX_ROOMS;
    private int ROOM_MIN_SIZE;
    private int ROOM_MAX_SIZE;

    public Dungeon_Map (int width, int height, int max_rooms, int min_size, int max_size) {
        WIDTH = width;
        HEIGHT = height;
        MAX_ROOMS = max_rooms;
        ROOM_MIN_SIZE = min_size;
        ROOM_MAX_SIZE = max_size;
    }

    public Player drawDungeon(TETile[][] tiles, Random random) {
        ArrayList<Rect> rooms = new ArrayList<>();
        int number_of_rooms = 0;

        for (int r = 0; r < MAX_ROOMS; r += 1) {
            int w = random.nextInt(ROOM_MAX_SIZE - ROOM_MIN_SIZE) + ROOM_MIN_SIZE;
            int h = random.nextInt(ROOM_MAX_SIZE - ROOM_MIN_SIZE) + ROOM_MIN_SIZE;
            int x = random.nextInt(WIDTH - w - 1);
            int y = random.nextInt(HEIGHT - h - 1);

            Rect new_room = new Rect(x, y, h, w);
            if (rooms.isEmpty()) {
                drawRoom(tiles, new_room);
                rooms.add(new_room);
                number_of_rooms += 1;
            }
            else {
                boolean collide = false;
                for (Rect other_room : rooms) {
                    if (new_room.intersect(other_room)) {
                        collide = true;
                        break;
                    }
                }
                if (!collide) {
                    drawRoom(tiles, new_room);
                    rooms.add(new_room);
                    number_of_rooms += 1;
                }
            }
        }
        drawTunnels(tiles, rooms);
        generateKeyAndExit(tiles, rooms, random);
        return generatePlayer(tiles, rooms);
    }

    // draw a rectangular room with wall surrounding it
    public void drawRoom(TETile[][] tiles, Rect room) {
        for (int x = room.bottomLeftX; x < room.topRightX; x += 1) {
            for (int y = room.bottomLeftY; y < room.topRightY; y += 1) {
                tiles[x][y] = Tileset.WALL;
            }
        }
        for (int x = room.bottomLeftX + 1; x < room.topRightX - 1; x += 1) {
            for (int y = room.bottomLeftY + 1; y < room.topRightY - 1; y += 1) {
                tiles[x][y] = Tileset.FLOOR;
            }
        }
    }

    // draw tunnels to connect all the rooms
    public void drawTunnels(TETile[][] tiles, ArrayList<Rect> lst) {
        int prev_centerx = lst.get(0).centerX();
        int prev_centery = lst.get(0).centerY();
        for (int r = 1; r < lst.size(); r += 1) {
            Rect next_room = lst.get(r);
            int new_centerx = next_room.centerX();
            int new_centery = next_room.centerY();

            draw_h_tunnel(tiles, prev_centerx, new_centerx, prev_centery);
            draw_v_tunnel(tiles, prev_centery, new_centery, new_centerx);

            prev_centerx = new_centerx;
            prev_centery = new_centery;
        }
    }

    // draw a horizontal tunnel that connect room
    public void draw_h_tunnel(TETile[][] tiles, int x1, int x2, int y) {
        int left = Math.min(x1 , x2);
        int right = Math.max(x1, x2);

        // fill out the corner with WALL of two corridor
        if (left - 1 >= 0 && y + 1 <= HEIGHT &&
                tiles[left - 1][y + 1].equals(Tileset.NOTHING)) {
            tiles[left - 1][y + 1] = Tileset.WALL;
        }

        if (left - 1 >= 0 && y - 1 >= 0 &&
                tiles[left - 1][y - 1].equals(Tileset.NOTHING)) {
            tiles[left - 1][y - 1] = Tileset.WALL;
        }

        // draw the corridor and the wall aside it
        for (int x = left; x < right + 1; x += 1) {
            tiles[x][y] = Tileset.FLOOR;
            if (!(y + 1 > HEIGHT) && tiles[x][y + 1].equals(Tileset.NOTHING)) {
                tiles[x][y + 1] = Tileset.WALL;
            }
            if (!(y - 1 < 0) && tiles[x][y - 1].equals(Tileset.NOTHING)) {
                tiles[x][y - 1] = Tileset.WALL;
            }
        }

        // fill out the corner with WALL of two corridor
        if (right + 1 <= WIDTH && y + 1 <= HEIGHT &&
                tiles[right + 1][y + 1].equals(Tileset.NOTHING)) {
            tiles[right + 1][y + 1] = Tileset.WALL;
        }

        if (right - 1 >= 0 && y - 1 >= 0 &&
                tiles[right - 1][y - 1].equals(Tileset.NOTHING)) {
            tiles[right - 1][y - 1] = Tileset.WALL;
        }
    }

    public void draw_v_tunnel(TETile[][] tiles, int y1, int y2, int x) {
        int bottom = Math.min(y1 , y2);
        int top = Math.max(y1, y2);

        // fill out the corner with WALL of two corridor
        if (bottom - 1 >= 0 && x + 1 <= WIDTH &&
                tiles[x + 1][bottom - 1].equals(Tileset.NOTHING)) {
            tiles[x + 1][bottom - 1] = Tileset.WALL;
        }

        if (bottom - 1 >= 0 && x - 1 >= 0 &&
                tiles[x - 1][bottom - 1].equals(Tileset.NOTHING)) {
            tiles[x - 1][bottom - 1] = Tileset.WALL;
        }
        // draw the corridor and the wall aside it
        for (int y = bottom; y < top + 1; y += 1) {
            tiles[x][y] = Tileset.FLOOR;
            if (!(x + 1 > WIDTH) && tiles[x + 1][y].equals(Tileset.NOTHING)) {
                tiles[x + 1][y] = Tileset.WALL;
            }
            if (!(x - 1 < 0) && tiles[x - 1][y].equals(Tileset.NOTHING)) {
                tiles[x - 1][y] = Tileset.WALL;
            }
        }
        // fill out the corner with WALL of two corridor;
        if (top + 1 <= HEIGHT && x + 1 <= WIDTH &&
                tiles[x + 1][top + 1].equals(Tileset.NOTHING)) {
            tiles[x + 1][top + 1] = Tileset.WALL;
        }

        if (top + 1 <= HEIGHT && x - 1 >= 0 &&
                tiles[x - 1][top + 1].equals(Tileset.NOTHING)) {
            tiles[x - 1][top + 1] = Tileset.WALL;
        }
    }

    public Player generatePlayer(TETile[][] tiles, List<Rect> lst) {
        Rect room = lst.get(0);
        Player player = new Player(tiles, room.centerX(), room.centerY());
        return player;
    }

    public void generateKeyAndExit (TETile[][] tiles, List<Rect> lst, Random random) {
        Rect room = lst.get(lst.size() - 1);
        tiles[room.centerX()][room.centerY()] = Tileset.KEY;

        int index = random.nextInt(27) + 1;
        Rect exit = lst.get(index);
        tiles[exit.centerX()][exit.centerY()] = Tileset.EXIT;
    }
}
