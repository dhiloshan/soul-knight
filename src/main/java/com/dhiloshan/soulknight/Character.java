package com.dhiloshan.soulknight;

import java.awt.Graphics2D;
import java.awt.Image;

public class Character {
	
	public int x, y; // top left corner of the character
	public int width, height;
	Image sprite; // image path

	public Character(Image sprite, int width, int height, int sx, int sy) {
		this.sprite = sprite;
		this.width = width;
		this.height = height;
		this.x = sx;
		this.y = sy;
	}

}