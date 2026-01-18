package com.dhiloshan.soulknight;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class BadPistol extends Weapon {
	Image bulletSprite = new ImageIcon(App.class.getResource("/assets/images/weapons/Bad_Pistol_Bullet.png")).getImage();
	String weaponName;
	ArrayList<Bullet> bullets; // stores the x pos (top-left corner)
	int reloadTime = 400;
	
	public BadPistol() {
		super ("Bad Pistol", new ImageIcon(App.class.getResource("/assets/images/weapons/Bad_Pistol.png")).getImage(), 27, 19);
		bullets = new ArrayList<Bullet>();
	}
	
	public void render(Graphics2D g2) {
		if(Data.player.isFacingLeft) {
			g2.drawImage(sprite, Data.player.x + 10, Data.player.y + Data.player.height / 2 + 5, -width, height, null);
		}
		else {
			g2.drawImage(sprite, Data.player.x + Data.player.width - 10, Data.player.y + Data.player.height / 2 + 5, width, height, null);
		}
	}
	
	public void shoot() { // spawn bullets here
		if(App.controllerState != null && App.controllerState.rightTrigger > 0.5 && System.currentTimeMillis() - prevShot >= reloadTime) {
			if(Data.player.isFacingLeft) {
				bullets.add(new Bullet(bulletSprite, Data.player.x - 15, Data.player.y + Data.player.height / 2 + 2, -30, 15, true));
			}
			else {
				bullets.add(new Bullet(bulletSprite, Data.player.x + Data.player.width + 15, Data.player.y + Data.player.height / 2 + 2, 30, 15, false));
			}
			prevShot = System.currentTimeMillis();
		}
		System.out.println(bullets.size());
	}
	
	public void updateBullets(Graphics2D g2) {
	    for (int i = bullets.size() - 1; i >= 0; i--) {
	        Bullet b = bullets.get(i);
	        if(b.dir) b.sx -= 10;
	        else b.sx += 10;
	        if (b.offScreen()) bullets.remove(i);
	        b.render(g2);
	    }
	}
}
