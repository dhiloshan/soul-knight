package com.dhiloshan.soulknight;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class Chest extends Enemy {

    public Chest(int x, int y) {
        super("Chest",
                new ImageIcon(App.class.getResource("/assets/images/map/crate.png")).getImage(),
                40, 40, x, y);
        this.health = 1; // one hit to open
    }

    
    // Description: adds a health or energy unit if crate is broken
    // Parameters: Graphics2D g2
    // Return: void
    @Override
    public void update(Graphics2D g2) {
        if (health <= 0) {
            // spawn items
            Data.items.add(new Item(worldX, worldY + 10, "health"));
            Data.items.add(new Item(worldX + 30, worldY + 10, "energy"));

            if (health == 0)
                health = -1; 
            return;
        }

        int sx = (int) (worldX - Data.player.worldX + Data.player.screenX);
        int sy = (int) (worldY - Data.player.worldY + Data.player.screenY);

        g2.drawImage(sprite, sx, sy, width, height, null);

        checkCollisions();
    }
    
    private void checkCollisions() {
        if (Data.weapon != null && Data.weapon.bullets != null) {
            for (int i = 0; i < Data.weapon.bullets.size(); i++) {
                Bullet b = Data.weapon.bullets.get(i);
                if (new Rectangle((int) b.sx, (int) b.sy, b.width, b.height)
                        .intersects(new Rectangle((int) worldX, (int) worldY, width, height))) {
                    health = 0; // destroy the crate
                    Data.weapon.bullets.remove(i);
                    break;
                }
            }
        }
    }
}
