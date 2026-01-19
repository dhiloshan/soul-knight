package com.dhiloshan.soulknight;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class MapGenerator {

    // Map Dimensions (Massive world)
    static final int WIDTH = 280;
    static final int HEIGHT = 230;
    static int[][] map = new int[WIDTH][HEIGHT];
    static Random random = new Random();

    // Tile IDs based on your list
    static final int FLOOR_VARS[] = {0, 9, 10, 11};
    static final int WALL = 1;
    static final int TREE = 2;
    static final int SPIKES = 3;
    static final int CRATE = 4;
    static final int CRATE_2 = 5;
    static final int TOTEM = 6;
    static final int WALL_UNBREAKABLE = 7; // Rare wall variant
    static final int BLANK = 8;

    public static void main(String[] args) {
        generateLevel1_5();
    }

    public static void generateLevel1_5() {
        // 1. Initialize world with BLANK (Transparent) space
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                map[x][y] = BLANK;
            }
        }

        // 2. Define Room Layout (Based on your 1-5 Screenshot)
        // Coordinates: x, y, width, height
        
        // Start Room (Green House) - Bottom Center
        createRoom(120, 180, 40, 40, false); 

        // Vertical Path UP
        createPath(135, 160, 10, 20);

        // Room 1 (Enemy Battle) - Center
        createRoom(120, 120, 40, 40, true);

        // Path RIGHT -> Hire Room
        createPath(160, 135, 40, 10);
        
        // Hire Room (!) - Right side
        createRoom(200, 125, 30, 30, false);

        // Path LEFT -> Room 2
        createPath(80, 135, 40, 10);

        // Room 2 (Enemy Battle) - Left Side
        createRoom(40, 120, 40, 40, true);

        // Path UP -> Junction Room
        createPath(55, 80, 10, 40);

        // Room 3 (Junction) - Top Left
        createRoom(40, 40, 40, 40, true);

        // Path LEFT -> Chest Room
        createPath(10, 55, 30, 10);

        // Chest Room (Orange) - Far Left
        createRoom(5, 45, 25, 30, false);

        // Path RIGHT -> Boss Room
        createPath(80, 55, 40, 10);

        // Boss Room (Purple Face) - Top Center (Massive)
        createRoom(120, 30, 60, 60, true);

        // 3. Export to Text File
        exportMap("C1.txt");
    }

    // Helper to draw a room with walls, floors, and decor
    private static void createRoom(int x, int y, int w, int h, boolean hasEnemies) {
        for (int i = x; i < x + w; i++) {
            for (int j = y; j < y + h; j++) {
                // Bounds check
                if (i >= WIDTH || j >= HEIGHT) continue;

                // Borders are Walls
                if (i == x || i == x + w - 1 || j == y || j == y + h - 1) {
                    // 5% chance for unbreakable wall variant
                    map[i][j] = (random.nextInt(100) < 5) ? WALL_UNBREAKABLE : WALL;
                } else {
                    // Inside is Floor
                    map[i][j] = FLOOR_VARS[random.nextInt(FLOOR_VARS.length)];

                    // Add random decor (Obstacles) inside the room
                    // Don't block the immediate entrance (simple buffer logic can be added here)
                    if (random.nextInt(100) < 8) { 
                        int roll = random.nextInt(100);
                        if (roll < 60) map[i][j] = CRATE;        // Common
                        else if (roll < 85) map[i][j] = CRATE_2; // Common
                        else if (roll < 95 && hasEnemies) map[i][j] = SPIKES; // Occasional trap
                        else if (roll < 98) map[i][j] = TREE;    // Rare
                        else map[i][j] = TOTEM;                  // Very Rare
                    }
                }
            }
        }
    }

    // Helper to draw paths (Connectors) that punch through walls
    private static void createPath(int x, int y, int w, int h) {
        for (int i = x; i < x + w; i++) {
            for (int j = y; j < y + h; j++) {
                if (i >= WIDTH || j >= HEIGHT) continue;
                
                // Borders of path are walls
                if (i == x || i == x + w - 1 || j == y || j == y + h - 1) {
                    // Only place a wall if it's currently BLANK (don't overwrite existing floors/doors)
                    if (map[i][j] == BLANK) {
                        map[i][j] = WALL;
                    }
                } else {
                    // Inside path is Floor (Carve through existing walls)
                    map[i][j] = FLOOR_VARS[random.nextInt(FLOOR_VARS.length)];
                }
            }
        }
    }

    private static void exportMap(String filename) {
        try (FileWriter writer = new FileWriter("src/main/resources/assets/maps/lvl-1/" + filename)) {
            // Note: Loops are swapped (y then x) to print rows line by line
            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                    writer.write(map[x][y] + (x == WIDTH - 1 ? "" : " "));
                }
                writer.write("\n");
            }
            System.out.println("Map generated successfully: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}