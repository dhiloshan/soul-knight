package com.dhiloshan.soulknight;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class GoblinPriest extends Enemy {

    Image idle = new ImageIcon(App.class.getResource("/assets/images/characters/GoblinPriest-d.png")).getImage();
    ArrayList<Bullet> bullets = new ArrayList<>();

    long spawnTime = System.currentTimeMillis();
    long lastAction = 0;
    long actionCooldown = 2000;
    int state = 0; 

    public GoblinPriest(int x, int y) {
        super("Goblin Priest",
                new ImageIcon(App.class.getResource("/assets/images/characters/GoblinPriest-d.png")).getImage(),
                120, 120, x, y);
        this.health = 400; 
        this.speed = 1.2f;
    }

    @Override
    public void update(Graphics2D g2) {
        if (health <= 0)
            return;

        if (System.currentTimeMillis() - spawnTime < 2000) {
            renderSelf(g2);
            return;
        }

        behavior();
        renderSelf(g2);
        checkCollisions();
    }
    
    // Description: controls the behaviour of the boss
	// Parameters: none
    // Return: void
    private void behavior() {
        worldX = Math.max(7640, Math.min(worldX, 7600 + 1600 - 160));
        worldY = Math.max(1960, Math.min(worldY, 1920 + 1200 - 160));

        long currentTime = System.currentTimeMillis();

        // state of boss
        if (currentTime - lastAction > actionCooldown) {
            double r = Math.random();
            if (health < 150 && r < 0.2)
                state = 2; // heal
            else if (r < 0.7) 
                state = 1; // attack
            else
                state = 0; // move

            lastAction = currentTime;
            actionCooldown = 2500 + (int) (Math.random() * 1000);
        }

        double dist = Math.hypot(Data.player.worldX - worldX, Data.player.worldY - worldY);
        
        if(state == 0 && dist > 300) {
        	double angle = Math.atan2(Data.player.worldY - worldY, Data.player.worldX - worldX);
            worldX += (int) (Math.cos(angle) * speed);
            worldY += (int) (Math.sin(angle) * speed);
            isFacingLeft = Math.cos(angle) < 0;
        }
        else if(state == 1 && currentTime % 15 == 0) {
        	if (Math.random() < 0.5)
                shootAtPlayer();
            else
                shootRadial();
        }
        else if(state == 2) {
        	if (health < 400 && currentTime % 20 == 0) {
                health += 1;
            }
        }

        // update bullets
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            b.sx += b.vx;
            b.sy += b.vy;

            if (new Rectangle((int) b.sx, (int) b.sy, b.width, b.height).intersects(
                    new Rectangle((int) (Data.player.worldX + 5), (int) (Data.player.worldY + 8), 31, 37))) {
                int dmg = 2;
                boolean hitHealth = false;
                for (int k = 0; k < dmg; k++) {
                    if (Data.player.shield > 0) {
                        Data.player.shield--;
                        if (Data.player.shield == 0)
                            AudioManager.getInstance().playSfx("shield_break");
                    } else {
                        Data.player.health--;
                        hitHealth = true;
                    }
                }
                if (hitHealth) {
                    AudioManager.getInstance().playSfx("fx_heart");
                    try {
                        App.rumble(0.6f, 0.6f, 500);
                    } catch (Exception e) {
                    }
                }
                bullets.remove(i);
                continue;
            }

            if (b.offScreen()) {
                bullets.remove(i);
            }
        }
    }

    private void shootAtPlayer() {
        double angle = Math.atan2(Data.player.worldY - worldY, Data.player.worldX - worldX);
        angle += (Math.random() * 0.4 - 0.2);
        // Reduced Speed (7 -> 5)
        bullets.add(new Bullet(null, (int) worldX + width / 2, (int) worldY + height / 2, 20, 20,
                Math.cos(angle) * 5, Math.sin(angle) * 5, true));
        App.screenShake = 5; // Add Shake
    }

    private void shootRadial() {
        int count = 6; // bullet count
        for (int i = 0; i < count; i++) {
            double angle = (Math.PI * 2 * i) / count;
            // speed of bullets
            bullets.add(new Bullet(null, (int) worldX + width / 2, (int) worldY + height / 2, 20, 20,
                    Math.cos(angle) * 3.5, Math.sin(angle) * 3.5, true));
        }
        App.screenShake = 5; // Add Shake
    }
    
    // Description: renders the boss
   	// Parameters: Graphics2D g2
    // Return: void
    private void renderSelf(Graphics2D g2) {
        int sx = (int) (worldX - Data.player.worldX + Data.player.screenX);
        int sy = (int) (worldY - Data.player.worldY + Data.player.screenY);

        if (state == 2) {
            g2.setColor(new Color(0, 255, 0, 100));
            g2.fillOval(sx - 10, sy - 10, width + 20, height + 20);
        }

        g2.drawImage(idle, isFacingLeft ? sx + width : sx, sy, isFacingLeft ? -width : width, height, null);

        // draw bullets
        for (Bullet b : bullets) {
            int bx = (int) (b.sx - Data.player.worldX + Data.player.screenX);
            int by = (int) (b.sy - Data.player.worldY + Data.player.screenY);
            g2.setColor(Color.RED);
            g2.fillOval(bx, by, b.width, b.height);
        }

        // Boss Health Bar
        g2.setColor(Color.RED);
        g2.fillRect(sx, sy - 20, width, 10);
        g2.setColor(Color.GREEN);
        g2.fillRect(sx, sy - 20, (int) (width * (health / 400.0f)), 10);

    }
    
    // Description: checks if the bullets collide with the boss
    // Parameters: none
    // Return: void
    private void checkCollisions() {
        if (Data.weapon != null && Data.weapon.bullets != null) {
            for (int i = 0; i < Data.weapon.bullets.size(); i++) {
                Bullet b = Data.weapon.bullets.get(i);
                if (new Rectangle((int) b.sx, (int) b.sy, b.width, b.height)
                        .intersects(new Rectangle((int) worldX, (int) worldY, width, height))) {
                    health -= 5;
                    Data.weapon.bullets.remove(i--);
                }
            }
        }
    }
}
