package com.dhiloshan.soulknight;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

public class Character {

	public int worldX, worldY;
	public int width, height;
	Image sprite; // image path
	public boolean isFacingLeft = false;
	public Rectangle solidArea;
	public boolean collisionOn = false;
	public float speed;

	public float lx = 0f;
	public float ly = 0f;

	public Character(Image sprite, int width, int height, int sx, int sy) {
		this.sprite = sprite;
		this.width = width;
		this.height = height;
		this.worldX = sx;
		this.worldY = sy;
	}

	public void render(Graphics2D g2) {
		if (isFacingLeft) {
			g2.drawImage(sprite, worldX + width, worldY, -width, height, null);
		} else {
			g2.drawImage(sprite, worldX, worldY, width, height, null);
		}
	}
}