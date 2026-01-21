package com.dhiloshan.soulknight;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class BadPistol extends Weapon {

    Image bulletSprite = new ImageIcon(App.class.getResource("/assets/images/weapons/Bad_Pistol_Bullet.png"))
            .getImage();
    ArrayList<Bullet> bullets;
    int reloadTime = 400;

    public BadPistol() {
        super("Bad Pistol", new ImageIcon(App.class.getResource("/assets/images/weapons/Bad_Pistol.png")).getImage(),
                27, 19);
        bullets = new ArrayList<Bullet>();
    }
    
    // Description: renders the bad pistol weapon (360 degrees auto aim)
    // Parameters: Graphics2D g2 (the screen)
    // Return: void
    public void render(Graphics2D g2) {
        // Find angle
        double angle = 0;
        Enemy nearest = getNearestEnemy();
        boolean flip = false;

        if (nearest != null) {
            angle = Math.atan2(
                    (nearest.worldY + nearest.height / 2.0) - (Data.player.worldY + Data.player.height / 2.0),
                    (nearest.worldX + nearest.width / 2.0) - (Data.player.worldX + Data.player.width / 2.0));
            if (Math.abs(angle) > Math.PI / 2)
                flip = true;
        } else {
            flip = Data.player.isFacingLeft;
            angle = flip ? Math.PI : 0;
        }

        AffineTransform backup = g2.getTransform();
        int pivotX = (int) (Data.player.screenX + Data.player.width / 2);
        int pivotY = (int) (Data.player.screenY + Data.player.height / 2 + 5);

        g2.translate(pivotX, pivotY);
        g2.rotate(angle);

        // Offset weapon from center
        int dist = 20;
        if (Math.abs(angle) > Math.PI / 2) {
            g2.scale(1, -1); // Flip vertically if aiming left so weapon isn't upside down
        }
        g2.drawImage(sprite, dist, -height / 2, width, height, null);

        g2.setTransform(backup);
        renderBullets(g2);
    }

    // Description: finds the nearest enemy from the player
    // Parameters: None
    // Return: the Enemy object who is closest to the player
    private Enemy getNearestEnemy() {
        Enemy nearest = null;
        double minDist = 800; // Max auto aim range
        for (Enemy e : Data.enemies) {
            if (e.health <= 0)
                continue;
            
            // Pythagorean theorem to find min dist. b/w two points 
            double dist = Math.hypot(e.worldX - Data.player.worldX, e.worldY - Data.player.worldY);
            if (dist < minDist) {
                minDist = dist;
                nearest = e;
            }
        }
        return nearest;
    }
    
    // Description: shoots a bullet from the weapon (bad pistol)
    // Parameters: None
    // Return: void
    public void shoot() {
        if (App.controllerState != null && App.controllerState.rightTrigger > 0.5
                && System.currentTimeMillis() - prevShot >= reloadTime) {

            if (Data.player.energy >= 1) {
                Data.player.energy -= 1;

                // offset spawn based on facing 
                int offset = Data.player.isFacingLeft ? -20 : 20;
                int spawnX = (int) Data.player.worldX + offset;
                int spawnY = (int) (Data.player.worldY + Data.player.height / 2 + 2);

                Enemy nearest = getNearestEnemy();
                double vx, vy;
                double speed = 15.0;

                if (nearest != null) {
                    double angle = Math.atan2((nearest.worldY + nearest.height / 2.0) - spawnY,
                            (nearest.worldX + nearest.width / 2.0) - spawnX);
                    vx = Math.cos(angle) * speed;
                    vy = Math.sin(angle) * speed;
                } 
                else {
                    double angle = 0;
                    // manual aim if right stick is moved
                    if (Math.abs(App.controllerState.rightStickX) > 0.2
                            || Math.abs(App.controllerState.rightStickY) > 0.2) {
                        angle = Math.atan2(App.controllerState.rightStickY, App.controllerState.rightStickX);
                        vx = Math.cos(angle) * speed;
                        vy = Math.sin(angle) * speed;
                    } 
                    else {
                        // default facing direction
                        vx = Data.player.isFacingLeft ? -speed : speed;
                        vy = 0;
                    }
                }

                bullets.add(new Bullet(bulletSprite, spawnX, spawnY, 30, 15, vx, vy));
                AudioManager.getInstance().playSfx("fx_gun_1");
                prevShot = System.currentTimeMillis();
                try {
                    App.rumble(0.3f, 0.3f, 150);
                } catch (Exception e) {
                }
            }
        }
    }
    
    // Description: updates the bullet position to continue in their linear direction
    // Parameters: None
    // Return: void
    public void updateBullets() {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            
            b.sx += b.vx;
            b.sy += b.vy;

            if (Math.abs(b.sx - Data.player.worldX) > 1000 || b.offScreen()) {
                bullets.remove(i);
            }
        }
    }
    
    // Description: displays all the bullets
    // Parameters: Graphics2D g2 (the screen)
    // Return: void)
    public void renderBullets(Graphics2D g2) {
        for (Bullet b : bullets) {
            int screenX = (int) (b.sx - Data.player.worldX + Data.player.screenX);
            int screenY = (int) (b.sy - Data.player.worldY + Data.player.screenY);
            
            AffineTransform backup = g2.getTransform(); // data type allows for translation and rotation (360 degrees auto aim required)
            g2.translate(screenX + b.width / 2, screenY + b.height / 2);
            g2.rotate(Math.atan2(b.vy, b.vx));
            g2.drawImage(b.sprite, -b.width / 2, -b.height / 2, b.width, b.height, null);
            g2.setTransform(backup);
        }
    }
}