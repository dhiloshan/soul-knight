package com.dhiloshan.soulknight;

import java.awt.Graphics2D;
import java.awt.Image;

public class Bullet {

    public Image sprite;
    public double sx, sy;
    public int width, height;
    public double vx, vy;
    public boolean dir;

    // constructor for bullet for different use cases
    public Bullet(Image sprite, int sx, int sy, int width, int height, double vx, double vy) {
        this(sprite, sx, sy, width, height, vx, vy, false);
    }

    public Bullet(Image sprite, int sx, int sy, int width, int height, double vx, double vy, boolean isEnemyBullet) {
        this.sprite = sprite;
        this.sx = sx;
        this.sy = sy;
        this.width = width;
        this.height = height;
        this.vx = vx;
        this.vy = vy;
        this.isEnemyBullet = isEnemyBullet;
    }

    public boolean isEnemyBullet = false;

    // Description: draws the bullet on the screen
    // Parameters: None
    // Return: Graphics2D g2 (the screen)
    public void render(Graphics2D g2) {
        if (isEnemyBullet) {
            g2.setColor(java.awt.Color.RED);
            g2.fillOval((int) sx, (int) sy, width, height);
            g2.setColor(java.awt.Color.WHITE);
            // smaller white circle inside
            g2.fillOval((int) sx + width / 4, (int) sy + height / 4, width / 2, height / 2);
        } else {
            g2.drawImage(sprite, (int) sx, (int) sy, width, height, null);
        }
    }

    // Description: checks if the bullet collides with an item, map object, or
    // character
    // Parameters: None
    // Return: a boolean for whether the bullet is colliding with something else
    public boolean checkCollision() {
        int c = (int) ((sx + width / 2) / App.tileSize);
        int r = (int) ((sy + height / 2) / App.tileSize);

        if (c < 0 || r < 0 || c >= Map.maxWorldCol[0] || r >= Map.maxWorldRow[0]) {
            return true;
        }

        int tileNum = Data.tileM.cellTileNum[c][r];
        if (Data.tileM.tile[tileNum] != null && Data.tileM.tile[tileNum].collision) {
            if (tileNum == 4 || tileNum == 5) {
                // break the crate
                Data.tileM.cellTileNum[c][r] = 0; // set to floor tile
                AudioManager.getInstance().playSfx("crate_break");

                // 30% to get health or energy
                if (Math.random() < 0.3) {
                    // 20% of the time it is health, 80% of the time it is energy
                    Data.items.add(new Item((int) sx, (int) sy, Math.random() < 0.2 ? "health" : "energy"));
                }
                return true; // Bullet destroyed
            }
            return true; // Wall collided
        }
        return false;
    }

    public boolean offScreen() {
        return checkCollision();
    }
}