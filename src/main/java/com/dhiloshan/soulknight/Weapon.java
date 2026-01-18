package com.dhiloshan.soulknight;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Weapon {
	
	protected Image sprite;
	protected String weaponName;
	protected long prevShot = 0L; // time stamp of previous shot in milliseconds
	protected int height = 0;
	protected int width;
	
	public Weapon(String weaponName, Image sprite, int width, int height) {
		this.weaponName = weaponName;
		this.sprite = sprite;
		this.width = width;
		this.height = height;
	}
	
}