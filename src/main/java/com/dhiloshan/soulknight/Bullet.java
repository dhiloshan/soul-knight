package com.dhiloshan.soulknight;

import java.awt.Graphics2D;
import java.awt.Image;

public class Bullet {
	Image bulletSprite;
	int sx, sy, width, height;
	boolean dir; // if true, bullet projectile moves leftward
	
	public Bullet(Image bulletSprite, int sx, int sy, int width, int height, boolean dir) {
		this.bulletSprite = bulletSprite;
		this.sx = sx;
		this.sy = sy;
		this.width = width;
		this.height = height;
		this.dir = dir;
	}
	
	public void render(Graphics2D g2) {
		g2.drawImage(bulletSprite, sx, sy, width, height, null);
	}
	
	public boolean offScreen() {
		if(sx + width >= App.screenWidth || sx < 0) {
			return true;
		}
		return false;
	}
	
	public void bulletCollision() { 
		// if it collides with an enemy
	}
}
