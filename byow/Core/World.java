package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    private static int width = 80;
    private static int height = 30;
    public TETile[][] myWorld;
    private Random rand;
    private class Room {
        private List<List<Integer>> floors;
        private int w;
        private int h;
        private int x;
        private int y;
        private Room() {
            floors = new ArrayList<>();
            w = 0;
            h = 0;
            x = 0;
            y = 0;
        }
    }
    private List<Room> rooms;
    public class Player {
        public int i;
        public int j;
        public Player() {
            i = 0;
            j = 0;
        }
    }
    public Player player;
    public World(int w, int h, long seed) {
        width = w;
        height = h;
        myWorld = new TETile[width][height];
        rand = new Random(seed);
        rooms = new ArrayList<>();
        player = new Player();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                myWorld[x][y] = Tileset.NOTHING;
            }
        }
    }

    public TETile[][] initialWorld() {
        int total = 10 + rand.nextInt(15);
        for (int i = 0; i < total; i++) {
            PlaceRoom();
        }
        for (int i = 0; i < total - 1; i++) {
            FindPath(rooms.get(i), rooms.get(i + 1));
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (myWorld[x][y] == Tileset.FLOOR) {
                    PlaceWalls(x, y);
                }
            }
        }
        return myWorld;
    }

    private void PlaceRoom() {
        Room curr = new Room();
        curr.w = rand.nextInt(3, 8);
        curr.h = rand.nextInt(3, 8);
        curr.x = rand.nextInt(2, width - curr.w - 2);
        curr.y = rand.nextInt(2, height - curr.h - 2);
        for (int x = curr.x; x < curr.x + curr.w; x++) {
            for (int y = curr.y; y < curr.y + curr.h; y++) {
                myWorld[x][y] = Tileset.FLOOR;
                curr.floors.add(List.of(x, y));
            }
        }
        rooms.add(curr);
    }

    private void FindPath(Room room1, Room room2) {
        List<Integer> r1 = room1.floors.get(rand.nextInt(room1.floors.size()));
        List<Integer> r2 = room2.floors.get(rand.nextInt(room2.floors.size()));
        int x1 = r1.get(0);
        int y1 = r1.get(1);
        int x2 = r2.get(0);
        int y2 = r2.get(1);
        if (rand.nextBoolean()) {
            PlaceHallway(x1, y1, x2 - x1, 0);
            PlaceHallway(x2, y1, 0, y2 - y1);
        } else {
            PlaceHallway(x1, y1, 0, y2 - y1);
            PlaceHallway(x1, y2, x2 - x1, 0);
        }
    }

    private void PlaceHallway(int x, int y, int dx, int dy) {
        int sx = Math.min(x, x + dx);
        int ex = Math.max(x, x + dx);
        int sy = Math.min(y, y + dy);
        int ey = Math.max(y, y + dy);
        for (int i = sx; i <= ex; i++) {
            for (int j = sy; j <= ey; j++) {
                if (myWorld[i][j] == Tileset.NOTHING) {
                    myWorld[i][j] = Tileset.FLOOR;
                }
            }
        }
    }

    private void PlaceWalls(int x, int y) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (myWorld[x + i][y + j] == Tileset.NOTHING) {
                    myWorld[x + i][y + j] = Tileset.WALL;
                }
            }
        }
    }

    public TETile[][] born() {
        initialWorld();
        Room bornRoom = rooms.get(rand.nextInt(rooms.size()));
        List<Integer> bornFloor = bornRoom.floors.get(rand.nextInt(bornRoom.floors.size()));
        player.i = bornFloor.get(0);
        player.j = bornFloor.get(1);
        myWorld[player.i][player.j] = Tileset.AVATAR;
        return myWorld;
    }

    public void move(int dx, int dy) {
        int i = player.i + dx;
        int j = player.j + dy;
        if (i > 0 && i < width && j > 0 && j < height && myWorld[i][j] == Tileset.FLOOR) {
            myWorld[player.i][player.j] = Tileset.FLOOR;
            player.i = i;
            player.j = j;
            myWorld[player.i][player.j] = Tileset.AVATAR;
        }
    }

    public static void main(String[] args) {
        Engine engine = new Engine();
        TERenderer ter = new TERenderer();
        ter.initialize(width, height);
        ter.renderFrame(engine.interactWithInputString(args[0]));
    }
}