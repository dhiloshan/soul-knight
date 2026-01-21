/*
Dhiloshan Sayanthan 
Soul Knight Replica
November 7th, 2025
A replica of the mobile game Soul Knight on Macbook that is playable via a controller.
*/

package com.dhiloshan.soulknight;

import com.studiohartman.jamepad.ControllerIndex;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;
import com.studiohartman.jamepad.ControllerUnpluggedException;

import java.util.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;

public class App extends JPanel {

    public static ControllerManager pads;
    public static javax.swing.Timer timer;
    public static ControllerIndex controller;
    public static ControllerState controllerState;

    public static int screenWidth = 1515, screenHeight = 910;
    public static int tileSize = 40;

    public int gameState;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int gameOverState = 3;
    public final int gameWonState = 4;
    public final int menuState = 0;
    public final int introState = 5;
    boolean gameWon = false;
    public static int screenShake = 0;

    Clip background;
    Clip bossMusic;
    boolean inBossLevel = false;
    boolean transitionActive = false;
    int transitionTimer = 0;
    int shieldRegenTimer = 0;
    int introTextTimer = 180; // 3 seconds

    String waveText = "";
    int waveTextTimer = 0;
    int bossBannerTimer = 0;
    long lastSpikeDamage = 0;

    // room system
    static class Room {
        int id;
        Rectangle bounds;
        String type; // cell options are HOME, ENEMY, BOSS
        boolean locked = false;
        boolean cleared = false;
        boolean active = false;

        // track door tiles and their original ids to restore
        java.util.Map<Point, Integer> doorTileBackups = new java.util.HashMap<>();

        int currentWave = 0;
        int maxWaves = 3;

        public Room(int id, int x, int y, int w, int h, String type) {
            this.id = id;
            // convert grid coordinates to world pixels
            this.bounds = new Rectangle(x * App.tileSize, y * App.tileSize, w * App.tileSize, h * App.tileSize);
            this.type = type;
            if (type.equals("HOME") || type.equals("CHEST")) {
                cleared = true;
                maxWaves = 0;
            } else if (type.equals("BOSS")) {
                maxWaves = 1;
            }
        }
    }

    ArrayList<Room> rooms = new ArrayList<>();
    Room currentRoom = null;
    int waveDisplayTimer = 0;

    public static App instance;
    public SettingsUI settingsUI;
    public MainMenu mainMenu;
    public ControllerIntro controllerIntro;

    public App() {
        instance = this;
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.WHITE);

        Data.setup(null);
        settingsUI = new SettingsUI();
        mainMenu = new MainMenu();
        controllerIntro = new ControllerIntro();

        loadController();

        gameState = introState; // start at the intro

        // create the rooms
        rooms.add(new Room(1, 125, 50, 30, 25, "ENEMY"));
        rooms.add(new Room(2, 60, 50, 30, 25, "ENEMY"));
        rooms.add(new Room(3, 190, 48, 40, 30, "BOSS"));
        rooms.add(new Room(4, 125, 110, 30, 25, "ENEMY"));
        rooms.add(new Room(5, 190, 110, 30, 25, "ENEMY"));
        rooms.add(new Room(6, 196, 170, 18, 18, "HOME"));
        rooms.add(new Room(7, 250, 111, 23, 23, "ENEMY"));

        // intro music
        AudioManager.getInstance().playMusic("intro_bg", true);

        timer = new javax.swing.Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
            }
        });
    }

    private void startGame() {
        gameState = playState;
        AudioManager.getInstance().playMusic("level_bg", true);
    }

    // Description:
    // Parameters: none
    // Return: void
    private void checkRoomTransition() {
        if (Data.player == null)
            return;

        Room activeRoom = null;
        for (Room r : rooms) {
            Rectangle inner = new Rectangle(r.bounds.x + 80, r.bounds.y + 80, r.bounds.width - 160,
                    r.bounds.height - 160);
            if (inner.contains(Data.player.worldX, Data.player.worldY)) {
                activeRoom = r;
                break;
            }
        }

        if (activeRoom != null && activeRoom != currentRoom) {
            currentRoom = activeRoom;

            if (currentRoom.type.equals("HOME")) {

            } else {
                // only for enemy or boss cell
                if (!currentRoom.cleared && !currentRoom.locked) {
                    lockRoom(currentRoom);
                }
            }
        }
    }

    // Description: tries to lock a room
    // Parameters: room r (current room)
    // Return: void
    private void lockRoom(Room r) {
        if (r.locked || r.cleared || r.type.equals("HOME") || r.type.equals("CHEST"))
            return;

        int gx = r.bounds.x / tileSize;
        int gy = r.bounds.y / tileSize;
        int gw = r.bounds.width / tileSize;
        int gh = r.bounds.height / tileSize;

        r.doorTileBackups.clear();

        // top & bottom edges
        for (int i = gx; i < gx + gw; i++) {
            checkAndLock(i, gy, r);
            checkAndLock(i, gy + gh - 1, r);
        }
        // left & right edges
        for (int j = gy; j < gy + gh; j++) {
            checkAndLock(gx, j, r);
            checkAndLock(gx + gw - 1, j, r);
        }

        r.locked = true;
        r.active = true;

        if (r.type.equals("ENEMY") || r.type.equals("BOSS")) {
            spawnWave(r, 1);
        }

        if (r.type.equals("BOSS")) {
            inBossLevel = true;
            transitionActive = true;
            transitionTimer = 0;
            bossBannerTimer = 90; // 1.5 seconds @ 60fps
            // Music Switch
            AudioManager.getInstance().playMusic("boss_bg", true);
        }
    }

    private void checkAndLock(int x, int y, Room r) {
        int currentId = Data.tileM.cellTileNum[x][y];
        if (currentId != 1 && currentId != 8) {
            r.doorTileBackups.put(new Point(x, y), currentId);
            Data.tileM.cellTileNum[x][y] = 1; // draw the wall
        }
    }

    // Description: spawns a "wave" of enemies (3 waves total)
    // Parameters: none
    // Return: void
    private void unlockRoom(Room r) {
        r.cleared = true;
        r.locked = false;
        r.active = false;

        // open the doors
        for (java.util.Map.Entry<Point, Integer> entry : r.doorTileBackups.entrySet()) {
            Point p = entry.getKey();
            Data.tileM.cellTileNum[p.x][p.y] = entry.getValue();
        }
        r.doorTileBackups.clear();

        if (r.type.equals("BOSS"))
            waveText = "BOSS DEFEATED";
        else
            waveText = "COMPLETED!";

        waveTextTimer = 120;
    }

    // Description: spawns a "wave" of enemies (3 waves total)
    // Parameters: none
    // Return: void
    private void spawnWave(Room r, int wave) {
        r.currentWave = wave;

        if (r.type.equals("BOSS")) {
            waveText = ""; // Handled by Anime Banner (drawBossBanner)
            waveTextTimer = 0;
            SpawnEnemies.spawnBoss(r);
            return;
        }

        waveText = "ENEMY ATTACK " + wave + " / " + r.maxWaves;
        waveTextTimer = 120;

        // Randomize 4-6 enemies strictly inside room bounds
        int count = 4 + (int) (Math.random() * 3);
        int padding = 2 * tileSize;

        for (int i = 0; i < count; i++) {
            int ex = r.bounds.x + padding + (int) (Math.random() * (r.bounds.width - 2 * padding));
            int ey = r.bounds.y + padding + (int) (Math.random() * (r.bounds.height - 2 * padding));
            Data.enemies.add(new GuardPistolEnemy(ex, ey));
        }

        // place spikes in an enemy room
        if (Math.random() < 0.4) {
            int spikeCount = 2 + (int) (Math.random() * 5); // 2 to 6 spike tiles per rom
            for (int k = 0; k < spikeCount; k++) {
                int tx = (r.bounds.x + padding + (int) (Math.random() * (r.bounds.width - 2 * padding))) / tileSize;
                int ty = (r.bounds.y + padding + (int) (Math.random() * (r.bounds.height - 2 * padding))) / tileSize;

                // only replace floor tiles
                if (tx >= 0 && tx < Map.maxWorldCol[0] && ty >= 0 && ty < Map.maxWorldRow[0]) {
                    int currentId = Data.tileM.cellTileNum[tx][ty];
                    if (currentId == 0 || currentId == 9 || currentId == 10 || currentId == 11) {
                        Data.tileM.cellTileNum[tx][ty] = 3; // Spike
                    }
                }
            }
        }
    }

    // Description: loads the controller into the game
    // Parameters: none
    // Return: void
    public static void loadController() {
        pads = new ControllerManager();
        try {
            pads.initSDLGamepad();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        controller = pads.getControllerIndex(0);
        try {
            if (!controller.canVibrate()) {
                System.out.println("Controller does not support rumble.");
            }
        } catch (ControllerUnpluggedException e) {
            e.printStackTrace();
        }
    }

    void startLoop() {
        timer.start();
    }

    // Description: Update game state
    // Return: void
    // Parameters: None
    public void update() {
        pads.update();
        controllerState = pads.getState(0);

        if (!controllerState.isConnected && gameState != menuState && gameState != introState) {
            repaint();
            return;
        }

        if (gameState == introState) {
            if (controllerIntro.update(controllerState)) {
                startGame();
            }
            repaint();
            return;
        } else if (gameState == pauseState) {
            if (settingsUI.update(controllerState)) {
                gameState = playState; // close settings
            }
            repaint();
            return;
        }

        if (bossBannerTimer > 0)
            bossBannerTimer--;

        if (gameState == menuState) {
            if (controllerState.startJustPressed || controllerState.aJustPressed) {
                startGame();
            }
            repaint();
            return;
        }

        if (Data.player != null && Data.player.health <= 0 && gameState != gameOverState) {
            gameState = gameOverState;

            waveText = "";
            waveTextTimer = 0;
        }

        if (gameState == gameOverState) {
            if (controllerState.aJustPressed || controllerState.startJustPressed) {
                // restart level
                Data.enemies.clear();
                Data.items.clear();
                Data.setup(null);
                inBossLevel = false;
                transitionActive = false;

                AudioManager.getInstance().stopAllMusic();
                startGame();

                // reset logic for rooms
                for (Room r : rooms) {
                    r.locked = false;
                    r.active = false;
                    r.cleared = r.type.equals("HOME");
                    r.doorTileBackups.clear();
                }
                currentRoom = null;
                waveDisplayTimer = 0;

            } else if (controllerState.bJustPressed) {
                // return to title
                Data.enemies.clear();
                Data.items.clear();
                Data.setup(null);
                inBossLevel = false;
                transitionActive = false;
                gameState = menuState;
                gameWon = false;

                for (Room r : rooms) {
                    r.locked = false;
                    r.active = false;
                    r.cleared = r.type.equals("HOME");
                    r.doorTileBackups.clear();
                }
                currentRoom = null;
                waveDisplayTimer = 0;

                AudioManager.getInstance().stopAllMusic();
                AudioManager.getInstance().playMusic("intro_bg", true);
            }
            repaint();
            return;
        }

        // similar to above block of code
        if (gameState == gameWonState) {
            if (controllerState.aJustPressed) {
                // restart level
                Data.enemies.clear();
                Data.items.clear();
                Data.setup(null);
                inBossLevel = false;
                transitionActive = false;

                // audio reset
                AudioManager.getInstance().stopAllMusic();
                startGame();

                for (Room r : rooms) {
                    r.locked = false;
                    r.active = false;
                    r.cleared = r.type.equals("HOME");
                    r.doorTileBackups.clear();
                }
                currentRoom = null;
                waveDisplayTimer = 0;
            }

            repaint();
            return;
        }

        // switch game state
        if (controllerState.yJustPressed) {
            if (gameState == playState)
                gameState = pauseState;
            else if (gameState == pauseState)
                gameState = playState;
        }

        // update logic if player is in play state
        if (gameState == playState) {
            Data.player.move();
            Data.weapon.shoot();
            Data.weapon.updateBullets();

            // armor regeneration
            if (Data.player != null && Data.player.shield < Data.player.maxShield) {
                shieldRegenTimer++;
                if (shieldRegenTimer >= 180) { // slower regen
                    Data.player.shield++;
                    shieldRegenTimer = 0;
                }
            }

            // room logic
            checkRoomTransition();

            if (currentRoom != null && currentRoom.locked && !currentRoom.cleared) {
                // check clear condition
                long enemiesInRoom = Data.enemies.stream().filter(e -> currentRoom.bounds.contains(e.worldX, e.worldY))
                        .count();
                if (enemiesInRoom == 0) {
                    if (currentRoom.type.equals("ENEMY") && currentRoom.currentWave < currentRoom.maxWaves) {
                        currentRoom.currentWave++;
                        spawnWave(currentRoom, currentRoom.currentWave); // Next Wave
                    } else {
                        unlockRoom(currentRoom);
                        if (currentRoom.type.equals("BOSS") && !gameWon) {
                            // show magic stone
                            int cx = currentRoom.bounds.x + currentRoom.bounds.width / 2 - 15;
                            int cy = currentRoom.bounds.y + currentRoom.bounds.height / 2 - 15;
                            Data.items.add(new Item(cx, cy, "magic_stone"));

                            if (bossMusic != null)
                                bossMusic.stop();
                        }
                    }
                }
            }
        }

        repaint();

    }

    public static void rumble(float left, float right, int ms) {
        try {
            controller.doVibration(left, right, ms);
        } catch (Exception e) {
        }
    }

    // Description: draws the elements every frame
    // Return: void
    // Parameters: Graphics g (the screen the user sees)
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        if (gameState == introState) {
            if (controllerIntro != null)
                controllerIntro.render(g2);
            return; // don't render game behind it
        }

        if (gameState == menuState) {
            if (mainMenu != null)
                mainMenu.render(g2);
            return;
        }

        if (screenShake > 0) {
            g2.translate((int) (Math.random() * screenShake * 2 - screenShake),
                    (int) (Math.random() * screenShake * 2 - screenShake));
            screenShake--;
        }

        if (Data.tileM != null)
            Data.tileM.render(g2);

        // update and render items
        for (int i = 0; i < Data.items.size(); i++) {
            Item item = Data.items.get(i);
            item.update(g2);
            if (item.collected) {
                if (item.type.equals("magic_stone")) {
                    gameWon = true;
                    gameState = gameWonState;
                    // stop music when the state changes
                    AudioManager.getInstance().stopAllMusic();
                    if (bossMusic != null)
                        bossMusic.stop();
                    AudioManager.getInstance().playSfx("victory"); // play once
                }
                Data.items.remove(i--);
            }
        }

        // update and render enemies
        for (int i = 0; i < Data.enemies.size(); i++) {
            Enemy e = Data.enemies.get(i);
            if (gameState != gameOverState)
                e.update(g2);
            else
                e.renderVisuals(g2); // Don't move if game over, just render

            if (gameState == playState && e.health <= 0) { // Only die in play state
                // drop energy on enemy death
                if (Data.player != null) {
                    Data.player.energy = Math.min(Data.player.energy + 5, Data.player.maxEnergy);
                }
                Data.enemies.remove(i--);
                continue; // skip update for dead enemy
            }

            // ensures enemies don't spawn in one area
            if (e.worldX < App.tileSize)
                e.worldX = App.tileSize;
            if (e.worldY < App.tileSize)
                e.worldY = App.tileSize;
            if (e.worldX > (Map.maxWorldCol[0] - 2) * App.tileSize)
                e.worldX = (Map.maxWorldCol[0] - 2) * App.tileSize;
            if (e.worldY > (Map.maxWorldRow[0] - 2) * App.tileSize)
                e.worldY = (Map.maxWorldRow[0] - 2) * App.tileSize;
        }

        if (Data.player != null) {
            Data.player.render(g2);

            if (gameState == playState) {
                int c = (int) ((Data.player.worldX + Data.player.width / 2) / App.tileSize);
                int r = (int) ((Data.player.worldY + Data.player.height / 2) / App.tileSize);
                if (c >= 0 && r >= 0 && c < Map.maxWorldCol[0] && r < Map.maxWorldRow[0]) {
                    int tileNum = Data.tileM.cellTileNum[c][r];

                    // render spikes
                    if (tileNum == 3) {
                        if (System.currentTimeMillis() - lastSpikeDamage > 2000) {
                            if (Data.player.shield > 0) {
                                Data.player.shield--;
                            } else {
                                Data.player.health--;
                                AudioManager.getInstance().playSfx("fx_heart");
                            }
                            lastSpikeDamage = System.currentTimeMillis();
                            rumble(0.2f, 0.2f, 200);
                        }
                    }
                }
            }
        }

        if (Data.weapon != null)
            Data.weapon.render(g2);

        if (Data.hud != null) {
            Data.hud.displayStatBar(g2);
            Data.hud.drawHUD(g2);
        }

        // transition overlay
        if (transitionActive) {
            transitionTimer++;
            if (transitionTimer > 150) {
                transitionActive = false;
            }
        }

        // game over screen
        if (gameState == gameOverState) {
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRect(0, 0, screenWidth, screenHeight);

            g2.setColor(Color.RED);
            g2.setFont(new Font("Pixelify Sans", Font.BOLD, 80));
            String text = "GAME OVER";
            int x = (screenWidth - g2.getFontMetrics().stringWidth(text)) / 2;
            int y = screenHeight / 2 - 50;
            g2.drawString(text, x, y);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Pixelify Sans", Font.BOLD, 40));
            text = "Press 'A' or Start to Restart";
            x = (screenWidth - g2.getFontMetrics().stringWidth(text)) / 2;
            y += 100;
            g2.drawString(text, x, y);
        }

        // victory screen
        if (gameState == gameWonState) {
            g2.setColor(new Color(0, 128, 0, 200));
            g2.fillRect(0, 0, screenWidth, screenHeight);

            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Pixelify Sans", Font.BOLD, 100));
            String text = "YOU WON!";
            int x = (screenWidth - g2.getFontMetrics().stringWidth(text)) / 2;
            int y = screenHeight / 2 - 50;
            g2.drawString(text, x, y);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Pixelify Sans", Font.BOLD, 40));
            text = "Press A to Restart";
            x = (screenWidth - g2.getFontMetrics().stringWidth(text)) / 2;
            y += 100;
            g2.drawString(text, x, y);
        }

        // show the intro text
        if (introTextTimer > 0) {
            introTextTimer--;
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Pixelify Sans", Font.BOLD, 60));
            String txt = "FOREST MAP";
            int x = (screenWidth - g2.getFontMetrics().stringWidth(txt)) / 2;
            int y = screenHeight / 2 - 100;
            g2.drawString(txt, x, y);
        }

        // handles wave notification text
        if (gameState != gameOverState && gameState != pauseState && waveTextTimer > 0) {
            waveTextTimer--;
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Pixelify Sans", Font.BOLD, 50));
            FontMetrics fm = g2.getFontMetrics();
            int tx = (screenWidth - fm.stringWidth(waveText)) / 2;
            int ty = screenHeight / 4;
            g2.drawString(waveText, tx, ty);
        }

        if (gameState != gameOverState && gameState != pauseState && currentRoom != null && currentRoom.locked
                && !currentRoom.cleared) {
            if (currentRoom.type.equals("BOSS")) {
                drawBossBanner(g2);
            } else {
                // enemy attack i / 3
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Pixelify Sans", Font.BOLD, 50));
                String txt = "ENEMY ATTACK " + currentRoom.currentWave + " / " + currentRoom.maxWaves;
                int tx = (screenWidth - g2.getFontMetrics().stringWidth(txt)) / 2;
                int ty = screenHeight / 4;
                g2.drawString(txt, tx, ty);
            }
        }

        // completion display
        else if (waveDisplayTimer > 0 && gameState != pauseState) {
            waveDisplayTimer--;
            g2.setFont(new Font("Pixelify Sans", Font.BOLD, 50));
            g2.setColor(Color.WHITE);
            String txt = "";
            if (currentRoom != null && currentRoom.cleared) {
                if (currentRoom.type.equals("BOSS"))
                    txt = "BOSS DEFEATED!";
                else
                    txt = "ENEMY FIGHT COMPLETED!";
            }

            if (!txt.isEmpty()) {
                FontMetrics fm = g2.getFontMetrics();
                int tx = (screenWidth - fm.stringWidth(txt)) / 2;
                int ty = screenHeight / 4;
                g2.drawString(txt, tx, ty);
            }
        }
        drawMinimap(g2);

        if (gameState == pauseState) {
            if (settingsUI != null)
                settingsUI.render(g2);
        }
    }

    // Description: draws the map layout of the game on top right crner
    // Parameters: None
    // Return: void
    public void drawMinimap(Graphics2D g2) {
        int mx = screenWidth - 180;
        int my = 130;
        float scale = 0.5f;

        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRoundRect(mx - 10, my - 10, 170, 170, 10, 10);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(mx - 10, my - 10, 170, 170, 10, 10);

        // draws the edges (the connections between the rooms)
        int[][] connections = { { 2, 1 }, { 1, 3 }, { 1, 4 }, { 4, 5 }, { 5, 6 }, { 5, 7 } };

        g2.setColor(Color.LIGHT_GRAY);
        g2.setStroke(new BasicStroke(4));

        for (int[] pair : connections) {
            Room r1 = null, r2 = null;
            for (Room r : rooms) {
                if (r.id == pair[0])
                    r1 = r;
                if (r.id == pair[1])
                    r2 = r;
            }
            if (r1 != null && r2 != null) {
                int x1 = mx + (int) ((r1.bounds.getCenterX() / App.tileSize) * scale);
                int y1 = my + (int) ((r1.bounds.getCenterY() / App.tileSize) * scale);
                int x2 = mx + (int) ((r2.bounds.getCenterX() / App.tileSize) * scale);
                int y2 = my + (int) ((r2.bounds.getCenterY() / App.tileSize) * scale);
                g2.drawLine(x1, y1, x2, y2);
            }
        }

        // draws the rooms
        for (Room r : rooms) {
            // coordinates of the room on layout
            int rx = mx + (int) ((r.bounds.x / tileSize) * scale);
            int ry = my + (int) ((r.bounds.y / tileSize) * scale);
            int rw = (int) ((r.bounds.width / tileSize) * scale);
            int rh = (int) ((r.bounds.height / tileSize) * scale);

            // colour of the room
            if (r.type.equals("HOME"))
                g2.setColor(new Color(50, 200, 50));
            else if (r.type.equals("BOSS"))
                g2.setColor(new Color(150, 0, 150));
            else
                g2.setColor(Color.RED);

            g2.fillRect(rx, ry, rw, rh);

            if (r == currentRoom) {
                g2.setColor(Color.YELLOW);
                g2.setStroke(new BasicStroke(2));
                g2.drawRect(rx, ry, rw, rh);
            }
        }
    }

    // Description: draws the final boss banner
    // Parameters: None
    // Return: void
    public void drawBossBanner(Graphics2D g2) {
        if (bossBannerTimer <= 0)
            return;

        int h = 120;
        int y = screenHeight / 3;

        // draw an orange parallelogram
        int[] xPoints = { -50, screenWidth + 50, screenWidth - 20, 20 };
        Polygon p = new Polygon();
        p.addPoint(-100, y);
        p.addPoint(screenWidth + 100, y);
        p.addPoint(screenWidth + 50, y + h);
        p.addPoint(-50, y + h);

        g2.setColor(new Color(255, 140, 0, 220));
        g2.fillPolygon(p);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(5));
        g2.drawPolygon(p);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Pixelify Sans", Font.ITALIC | Font.BOLD, 80));
        String text = "BOSS FIGHT";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, (screenWidth - fm.stringWidth(text)) / 2, y + 90);
    }

    public static void main(String[] args) {
        System.out.println("Soul Knight (Swing) started!");
        JFrame frame = new JFrame("Soul Knight");
        App panel = new App();
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Thread(() -> {
                    try {
                        if (pads != null)
                            pads.quitSDLGamepad();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        System.exit(0);
                    }
                }).start();
            }
        });

        frame.setVisible(true);
        panel.startLoop();
    }
}