package com.dhiloshan.soulknight;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Player extends Character {
	
	boolean wasAPressed = false;
	float speed = 6.7f;
	
	int maxHealth = 7, health = maxHealth;
	int maxShield = 6, shield = maxShield;
	int maxEnergy = 180, energy = maxEnergy;

	public Player(int width, int height, int sx, int sy) {
		super( // the actual parameters for the constructor in the superclass, Character
			new ImageIcon(App.class.getResource("/assets/images/characters/knight.gif")).getImage(), 
			width, 
			height,
			sx,
			sy
		);
	}
	
	public float deadzone(float v, float dz) { // very minor joy stick movement shouldn't create player movement
		if(Math.abs(v) < dz) {
			return 0f;
		}
		else {
			return v;
		}
	}
	
	public void displayExternal(Graphics2D g2) throws IOException {
		g2.setColor(new Color(180, 150, 120)); 
		g2.fillRoundRect(16, 16, 250, 160, 30, 30);
		g2.setColor(new Color(92, 64, 51));
		g2.drawRoundRect(16, 16, 250, 160, 30, 30);
		
		g2.drawImage(ImageIO.read(App.class.getResource("/assets/images/misc/heart_icon.png")), 20, 20, null);
		g2.drawImage(ImageIO.read(App.class.getResource("/assets/images/misc/shield_icon.png")), 20, 70, null);
		g2.drawImage(ImageIO.read(App.class.getResource("/assets/images/misc/energy_icon.png")), 20, 120, null);
		
	}
	
	public void move() {
		// -- Move with left joy stick 
		float lx = deadzone(App.s.leftStickX, 0.15f);
		float ly = deadzone(App.s.leftStickY, 0.15f); 

		x += lx * speed;
		y += ly * speed;

		// stay in bound [20, w or height - 20]
		if(x < 20) x = 20;
		else if(x > App.screenWidth - 20) x = App.screenWidth - 20;
		if(y < 20) y = 20;
		else if(y > App.screenHeight - 20) y = App.screenHeight - 20;
		
		// -- if a is pressed ONLY now
		boolean a = App.s.a; 
		if (a && !wasAPressed) { 
			App.rumble(1.0f, 1.0f, 250);
		}
		wasAPressed = a;
	}
}
