package com.dhiloshan.soulknight;

import java.awt.Graphics2D;
import java.util.ArrayList;

public class Data {
    public static HUD hud;
    public static Map map;
    public static TileManager tileM;
    public static CollisionChecker cChecker;
    public static BadPistol weapon;
    public static Player player;
    public static ArrayList<Enemy> enemies = new ArrayList<>();
    public static ArrayList<Item> items = new ArrayList<>();
    public static Enemy trumpetFlower;

    public static void setup(Graphics2D g2) {
        hud = new HUD();
        map = new Map();
        cChecker = new CollisionChecker();
        tileM = new TileManager(null);
        weapon = new BadPistol();

        player = new Player(42, 50, 8200, 7160);

        new Chest(10440, 4880);
    }
}