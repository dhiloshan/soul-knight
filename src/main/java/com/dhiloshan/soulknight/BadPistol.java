package com.dhiloshan.soulknight;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class BadPistol extends Weapon {
    
    Image bulletSprite = new ImageIcon(App.class.getResource("/assets/images/weapons/Bad_Pistol_Bullet.png")).getImage();
    
    ArrayList<Bullet> bullets; 
    int reloadTime = 400;
    
    public BadPistol() {
        super("Bad Pistol", new ImageIcon(App.class.getResource("/assets/images/weapons/Bad_Pistol.png")).getImage(), 27, 19);
        bullets = new ArrayList<Bullet>();
    }
    
    public void render(Graphics2D g2) {
		if (Data.player.isFacingLeft) {
			g2.drawImage(sprite, Data.player.screenX - 5 + width, Data.player.screenY + Data.player.height / 2 + 5, -width, height, null);
		} else {
			g2.drawImage(sprite, Data.player.screenX + Data.player.width - 10, Data.player.screenY + Data.player.height / 2 + 5, width, height, null);
		}
		updateBullets(g2);
	}
    
    public void shoot() {
        if(App.controllerState != null && App.controllerState.rightTrigger > 0.5 && System.currentTimeMillis() - prevShot >= reloadTime) {
            
            int spawnX = (int)Data.player.worldX;
            int spawnY = (int)(Data.player.worldY + Data.player.height / 2 + 2);
            
            if(Data.player.isFacingLeft) {
                bullets.add(new Bullet(bulletSprite, spawnX - 15, spawnY, -30, 15, true));
            }
            else {
                bullets.add(new Bullet(bulletSprite, spawnX + Data.player.width + 15, spawnY, 30, 15, false));
            }
            prevShot = System.currentTimeMillis();
            
            App.rumble(0.3f, 0.3f, 150);
        }
    }
    
    public void updateBullets(Graphics2D g2) {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            
            if(b.dir) b.sx -= 10;
            else b.sx += 10;
            
            if (Math.abs(b.sx - Data.player.worldX) > 1000) {
                bullets.remove(i);
                continue;
            }
            
            int screenX = (int)(b.sx - Data.player.worldX + Data.player.screenX);
            int screenY = (int)(b.sy - Data.player.worldY + Data.player.screenY);
            
            g2.drawImage(b.bulletSprite, screenX, screenY, 30, 15, null);
        }
    }
}