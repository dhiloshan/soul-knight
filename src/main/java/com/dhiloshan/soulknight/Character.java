package com.dhiloshan.soulknight;

import java.awt.Graphics2D;
import java.awt.Image;

public class Character {
	
	int x, y; // top left corner of the character
	int width, height;
	Image sprite; // image path

	public Character(Image sprite, int width, int height, int sx, int sy) {
		this.width = width;
		this.height = height;
		this.sprite = sprite;
		this.x = sx;
		this.y = sy;
	}
	
	public void render(Graphics2D g) {
		if(sprite == null) { // path doesn't exist
			throw new IllegalStateException("Sprite is null right now.");
		}
		g.drawImage(sprite, x, y, width, height, null);
	}
	

}
