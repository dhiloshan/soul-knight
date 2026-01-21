package com.dhiloshan.soulknight;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;
import javax.swing.ImageIcon;

public class GuardPistolEnemy extends Enemy {

    Image idle = new ImageIcon(App.class.getResource("/assets/images/characters/GuardPistol-d.png")).getImage();
    Image walkL = new ImageIcon(App.class.getResource("/assets/images/characters/GuardPistol-l.png")).getImage();
    Image walkR = new ImageIcon(App.class.getResource("/assets/images/characters/GuardPistol-r.png")).getImage();
    Image weaponSprite = new ImageIcon(App.class.getResource("/assets/images/weapons/Bad_Pistol.png")).getImage();
    Image bulletSprite = new ImageIcon(App.class.getResource("/assets/images/weapons/Bad_Pistol_Bullet.png"))
            .getImage();

    ArrayList<Bullet> bullets = new ArrayList<>();
    ArrayList<Point> path = new ArrayList<>();
    long lastPathCalc = 0, lastStep = 0, lastShot = 0;
    boolean walkingFrameLeft = true, isMoving = false;
    int reloadTime = 800;

    public GuardPistolEnemy(int x, int y) {
        super("Guard Pistol",
                new ImageIcon(App.class.getResource("/assets/images/characters/GuardPistol-d.png")).getImage(), 42, 50,
                x, y);
        this.speed = 1.2f;
        this.health = 10;
    }

    @Override
    public void update(Graphics2D g2) {
        if (health <= 0)
            return;
        behavior();
        renderVisuals(g2);
        checkCollisions();
    }
    
    // Description: controls the behaviour of the guard pistol enemy
 	// Parameters: none
    // Return: void
    private void behavior() {
        double dist = Math.hypot(Data.player.worldX - worldX, Data.player.worldY - worldY);
        if (System.currentTimeMillis() - lastPathCalc > 500 && dist < 1000) {
            calculatePath();
            lastPathCalc = System.currentTimeMillis();
        }

        isMoving = false;
        if (!path.isEmpty()) {
            Point n = path.get(0);
            double tx = n.x * App.tileSize, ty = n.y * App.tileSize;
            if (Math.abs(worldX - tx) < 10 && Math.abs(worldY - ty) < 10)
                path.remove(0);
            else {
                double a = Math.atan2(ty - worldY, tx - worldX);
                worldX += Math.cos(a) * speed;
                worldY += Math.sin(a) * speed;
                isMoving = true;
                isFacingLeft = Math.cos(a) < 0;
            }
        }

        if (dist < 600 && System.currentTimeMillis() - lastShot > reloadTime && Math.random() < 0.08)
            shoot();

        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            b.sx += b.vx;
            b.sy += b.vy;
            if (new Rectangle((int) b.sx, (int) b.sy, b.width, b.height).intersects(
                    new Rectangle((int) (Data.player.worldX + 5), (int) (Data.player.worldY + 8), 31, 37))) {
                if (Data.player.shield > 0) {
                    Data.player.shield--;
                    if (Data.player.shield == 0)
                        AudioManager.getInstance().playSfx("shield_break");
                } else {
                    Data.player.health--;
                    AudioManager.getInstance().playSfx("fx_heart");
                    try {
                        App.rumble(0.5f, 0.5f, 500);
                    } catch (Exception e) {
                    }
                }
                bullets.remove(i);
                continue;
            }
            if (Math.abs(b.sx - worldX) > 1000 || b.offScreen())
                bullets.remove(i);
        }
    }
    
    // Description: finds the specific path required for the enemies using A* search
  	// Parameters: none
    // Return: void
    private void calculatePath() {
        int sc = (int) ((worldX + width / 2) / App.tileSize);
        int sr = (int) ((worldY + height / 2) / App.tileSize);
        int ec = (int) ((Data.player.worldX + Data.player.width / 2) / App.tileSize);
        int er = (int) ((Data.player.worldY + Data.player.height / 2) / App.tileSize);
        
        if (sc >= 0 && sr >= 0 && ec < Data.map.maxWorldCol[0] && er < Data.map.maxWorldRow[0]) {
            ArrayList<Point> p = findPath(sc, sr, ec, er);
            if (p != null)
                path = p;
        }
    }
    
    private void shoot() {
        double a = Math.atan2((Data.player.worldY + Data.player.height / 2.0) - (worldY + height / 2.0),
                (Data.player.worldX + Data.player.width / 2.0) - (worldX + width / 2.0)) + (Math.random() - 0.5) * 0.2;
       
        bullets.add(new Bullet(null, (int) worldX, (int) worldY + height / 2, 20, 20, Math.cos(a) * 5,
                Math.sin(a) * 5, true));
        
        lastShot = System.currentTimeMillis();
    }
    
    
    // Description: renders the enemies and their bullets
    // Parameters: Graphics2D g2 (the screen)
    // Return: void
    @Override
    public void renderVisuals(Graphics2D g2) {
        if (isMoving && System.currentTimeMillis() - lastStep >= 200) {
            walkingFrameLeft = !walkingFrameLeft;
            lastStep = System.currentTimeMillis();
        }
        Image img = isMoving ? (walkingFrameLeft ? walkL : walkR) : idle;
        
        int sx = (int) (worldX - Data.player.worldX + Data.player.screenX),
                sy = (int) (worldY - Data.player.worldY + Data.player.screenY);

        g2.drawImage(img, isFacingLeft ? sx + width : sx, sy, isFacingLeft ? -width : width, height, null);

        double a = Math.atan2(Data.player.worldY - worldY, Data.player.worldX - worldX);
        AffineTransform old = g2.getTransform();
        g2.translate(sx + width / 2, sy + height / 2 + 5);
        g2.rotate(a);
        
        if (Math.abs(a) > Math.PI / 2)
            g2.scale(1, -1);
        
        g2.drawImage(weaponSprite, 15, -9, 27, 19, null);
        g2.setTransform(old);

        for (Bullet b : bullets) {
            int bx = (int) (b.sx - Data.player.worldX + Data.player.screenX);
            int by = (int) (b.sy - Data.player.worldY + Data.player.screenY);

            if (b.isEnemyBullet) {
                g2.setColor(Color.RED);
                g2.fillOval(bx, by, b.width, b.height);
                g2.setColor(Color.WHITE);
                g2.fillOval(bx + 4, by + 4, b.width - 8, b.height - 8);
            } 
            else {
                old = g2.getTransform();
                
                g2.translate(bx + b.width / 2, by + b.height / 2);
                g2.rotate(Math.atan2(b.vy, b.vx));
                g2.drawImage(b.sprite, -b.width / 2, -b.height / 2, b.width, b.height, null);
                g2.setTransform(old);
            }
        }
        g2.setColor(Color.RED);
        g2.fillRect(sx, sy - 10, width, 5);
        g2.setColor(Color.GREEN);
        g2.fillRect(sx, sy - 10, (int) (width * (health / 10.0f)), 5);
    }
    
    // Description: checks if the enemies bullets collide with player
    // Parameters: Graphics2D g2 (the screen)
    // Return: void
    private void checkCollisions() {
        if (Data.weapon != null && Data.weapon.bullets != null) {
            for (int i = 0; i < Data.weapon.bullets.size(); i++) {
                Bullet b = Data.weapon.bullets.get(i);
                if (new Rectangle((int) b.sx, (int) b.sy, b.width, b.height)
                        .intersects(new Rectangle((int) worldX, (int) worldY, width, height))) {
                    health -= 5;
                    Data.weapon.bullets.remove(i);
                    i--;
                }
            }
        }
    }
}
