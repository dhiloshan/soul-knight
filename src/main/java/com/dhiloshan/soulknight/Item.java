package com.dhiloshan.soulknight;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.net.URL;

import javax.swing.ImageIcon;

public class Item {
    public int x, y;
    public String type; // "health" or "energy"
    public Image sprite;
    public boolean collected = false;

    public Item(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;

        String path = "";
        if (type.equals("health"))
            path = "/assets/images/misc/health_icon.png";
        else if (type.equals("energy"))
            path = "/assets/images/misc/energy_icon.png";
        else if (type.equals("magic_stone"))
            path = "/assets/images/misc/magic_stone.png";

        URL url = App.class.getResource(path);
        if (url != null) {
            this.sprite = new ImageIcon(url).getImage();
        } 
        else {
            System.err.println("Warning: Resource not found for item type: " + type);
            // note that the sprite remains null
        }
    }
    
    // Description: updates the item
    // Parameters: Graphics2D g2 (the screen)
    // Return: void
    public void update(Graphics2D g2) {
        if (collected)
            return;

        int sx = (int) (x - Data.player.worldX + Data.player.screenX);
        int sy = (int) (y - Data.player.worldY + Data.player.screenY);

        if (sprite != null) {
            g2.drawImage(sprite, sx, sy, 30, 30, null);
        } else {
            // renders the item
            if (type.equals("health"))
                g2.setColor(java.awt.Color.GREEN);
            else if (type.equals("energy"))
                g2.setColor(java.awt.Color.BLUE);
            else
                g2.setColor(java.awt.Color.CYAN); // Magic Stone
            
            g2.fillRect(sx, sy, 30, 30);
        }

        // pick up logic
        if (new Rectangle(x, y, 30, 30)
                .intersects(new Rectangle((int) Data.player.worldX, (int) Data.player.worldY, Data.player.width,
                        Data.player.height))) {
            collected = true;
            if (type.equals("health")) {
                Data.player.health = Math.min(Data.player.health + 1, Data.player.maxHealth);
            } 
            else if (type.equals("energy")) {
                Data.player.energy = Math.min(Data.player.energy + 1, Data.player.maxEnergy);
            }
        }
    }
}
