package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private World world;
    private long seed;
    private String seedInput;
    private boolean mCheck = false;
    private String name = "Unknown";
    private boolean sightCheck = false;
    private int I;
    private int J;


    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT);
        menu();
        boolean inGame = false;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (c == 'N' && !inGame) {
                    inGame = true;
                    newWorld();
                } else if (c == 'L' && !inGame) {
                    inGame = true;
                    loadWorld();
                    ter.renderFrame(world.myWorld);
                } else if (c == 'Q' && !inGame) {
                    System.exit(0);
                } else if (c == ':' && inGame) {
                    mCheck = true;
                    behavior(c);
                } else if (c == 'T' && !inGame) {
                    rename();
                    newWorld();
                } else {
                    behavior(c);
                }
            }
            if (world != null) {
                displayHUD();
                changeSight();
            }
        }
    }
    private void displayHUD() {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
            String curr = world.myWorld[x][y].description();
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.textLeft(1, HEIGHT - 1, curr);
            LocalDateTime time = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedTime = time.format(formatter);
            StdDraw.textRight(WIDTH - 1, HEIGHT - 1, formattedTime);
            StdDraw.text(WIDTH / 2, HEIGHT - 1, name);
            StdDraw.text(WIDTH / 2, 1, "Press V to change sight");
            StdDraw.show();
        }
    }
    private void rename() {
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(0, 0, WIDTH, HEIGHT);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(WIDTH / 2, HEIGHT * 2 / 3, "Please enter your name:");
        StdDraw.show();
        String inputName = "";
        boolean check = false;
        while (!check) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (Character.isLetter(c) || Character.isDigit(c) || c == ' ') {
                    inputName += c;
                    displayName(inputName);
                } else if (c == '\n') {
                    check = true;
                }
            }
        }
        name = inputName;
    }
    private void displayName(String n) {
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(0, 0, WIDTH, HEIGHT);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(WIDTH / 2, HEIGHT * 2 / 3, n);
        StdDraw.text(WIDTH / 2, HEIGHT / 3, "Press return on your keyboard to continue when you finish");
        StdDraw.show();
    }
    private void menu() {
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(0, 0, WIDTH, HEIGHT);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(WIDTH / 2, HEIGHT * 2 / 3, "CS61B: The Game");
        StdDraw.text(WIDTH / 2, HEIGHT  / 3 + 3, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT  / 3 + 2, "Load Game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 3 + 1, "Set Name (T)");
        StdDraw.text(WIDTH / 2, HEIGHT  / 3, "Quit (Q)");
        StdDraw.show();
    }
    private void newWorld() {
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(0, 0, WIDTH, HEIGHT);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(WIDTH / 2, HEIGHT * 2 / 3, "Please enter a seed for this world:");
        StdDraw.show();
        String seedInput1 = "";
        boolean check = false;
        while (!check) {
            if (StdDraw.hasNextKeyTyped()) {
                char input = StdDraw.nextKeyTyped();
                if (Character.isDigit(input)) {
                    seedInput1 += input;
                    displaySeed(seedInput1);
                } else if (Character.toUpperCase(input) == 'S') {
                    try {
                        seed = Long.parseLong(seedInput1);
                        if (seed >= 0) {
                            check = true;
                        } else {
                            StdDraw.text(WIDTH / 2, HEIGHT / 2, "Invalid seed");
                            seedInput = "";
                            displaySeed(seedInput);
                        }
                    } catch (NumberFormatException e) {
                        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Invalid input");
                        seedInput = "";
                        displaySeed(seedInput);
                    }
                }
            }
        }
        this.seedInput = seedInput1;
        world = new World(WIDTH, HEIGHT, seed);
        TETile[][] newWorld = world.born();
        ter.renderFrame(newWorld);
    }
    private void saveWorld() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("SavedWorld.txt"));
            writer.write(seedInput);
            writer.write("\n" + world.player.i);
            writer.write("\n" + world.player.j);
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file: " + e.getMessage());
        }
    }
    private void loadWorld() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("SavedWorld.txt"));
            String line;
            String temp = "";
            while ((line = reader.readLine()) != null) {
                temp += line;
                temp += " ";
            }
            String[] tokens = temp.split("\\s+");
            long z = Long.parseLong(tokens[0]);
            seedInput = tokens[0];
            Integer x =  Integer.parseInt(tokens[1]);
            Integer y =  Integer.parseInt(tokens[2]);
            world = new World(WIDTH, HEIGHT, z);
            world.player.i = x;
            world.player.j = y;
            world.initialWorld();
            world.myWorld[x][y] = Tileset.AVATAR;
            reader.close();
        } catch (IOException e) {
            System.out.println("An error occurred while reading the input: " + e.getMessage());
        }
    }
    private void displaySeed(String s) {
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(0, 0, WIDTH, HEIGHT);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(WIDTH / 2, HEIGHT * 2 / 3, s);
        StdDraw.show();
    }
    private void behavior(char c) {
        switch (c) {
            case 'W':
                world.move(0, 1);
                changeSight();
                break;
            case 'A':
                world.move(-1, 0);
                changeSight();
                break;
            case 'S':
                world.move(0, -1);
                changeSight();
                break;
            case 'D':
                world.move(1, 0);
                changeSight();
                break;
            case 'Q':
                if (mCheck) {
                    saveWorld();
                    mCheck = false;
                    System.exit(0);
                }
                break;
            case 'V':
                sightCheck = !sightCheck;
                changeSight();
                break;
            default:
                break;
        }
    }

    private void changeSight() {
        if (sightCheck) {
            ter.renderFrame(aroundTiles());
        } else {
            ter.renderFrame(world.myWorld);
        }
    }

    private TETile[][] aroundTiles() {
        TETile[][] newTiles = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                newTiles[x][y] = Tileset.NOTHING;
            }
        }
        int x = world.player.i;
        int y = world.player.j;
        int sx = Math.max(0, x - 2);
        int sy = Math.max(0, y - 2);
        int ex = Math.min(WIDTH, x + 3);
        int ey = Math.min(HEIGHT, y + 3);
        for (int i = sx; i < ex; i++) {
            for (int j = sy; j < ey; j++) {
                newTiles[i][j] = world.myWorld[i][j];
            }
        }
        return newTiles;
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
     * In other words, running both of these:
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
        String uppercaseInput = input.toUpperCase();
        char[] inputArray = uppercaseInput.toCharArray();
        String seedInput1 = "";
        boolean pressedN = false;
        boolean inGame = false;
        boolean saveCheck = false;
        for (char c : inputArray) {
            if (c == 'N') {
                pressedN = true;
            } else if (pressedN && Character.isDigit(c)) {
                seedInput1 += Character.getNumericValue(c);
            } else if (c == 'S' && pressedN && !inGame) {
                inGame = true;
                seedInput = seedInput1.toString();
                seed = Long.parseLong(seedInput);
                world = new World(WIDTH, HEIGHT, seed);
                world.born();
                pressedN = false;
            } else if (c == 'L') {
                loadWorld();
            } else if (c == ':') {
                saveCheck = true;
            } else if (c == 'Q' && saveCheck) {
                saveWorld();
            } else if (c == 'W' || c == 'A' || c == 'S' || c == 'D') {
                movementInput(c);
            }
        }
        I = world.player.i;
        J = world.player.j;
        return world.myWorld;
    }
    private void movementInput(char c) {
        switch (c) {
            case 'W':
                world.move(0, 1);
                break;
            case 'A':
                world.move(-1, 0);
                break;
            case 'S':
                world.move(0, -1);
                break;
            case 'D':
                world.move(1, 0);
                break;
            default:
                break;
        }
    }
}


