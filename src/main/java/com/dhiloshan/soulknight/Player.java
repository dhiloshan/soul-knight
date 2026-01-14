package com.dhiloshan.soulknight;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Player extends Character {

	boolean wasAPressed = false;
	public boolean isFacingLeft = false;
	float speed = 6.7f;

	int maxHealth = 7, health = (int) (Math.random() * maxHealth);
	int maxShield = 6, shield = (int) (Math.random() * maxShield);
	int maxEnergy = 180, energy = (int) (Math.random() * maxEnergy);
 
	public Player(int width, int height, int sx, int sy) {
		super( // the actual parameters for the constructor in the superclass, Character
				new ImageIcon(App.class.getResource("/assets/images/characters/knight.gif")).getImage(), width, height,
				sx, sy);
	}

	public float deadzone(float v, float dz) { // very minor joy stick movement shouldn't create player movement
		if (Math.abs(v) < dz) {
			return 0f;
		} else {
			return v;
		}
	}

	int getStatBarWidth(int totalWidth, int curAmount, int maxAmount) { // helper method
		return (int) ((1.0 * curAmount / maxAmount) * totalWidth);
	}

	public void displayStatBar(Graphics2D g2) throws IOException {
		g2.setColor(new Color(180, 150, 120));
		g2.fillRoundRect(16, 16, 250, 160, 20, 20);
		g2.setColor(new Color(92, 64, 51));
		g2.drawRoundRect(16, 16, 250, 160, 20, 20);
		
		// ORDER: health, shield, energy

		g2.drawImage(ImageIO.read(App.class.getResource("/assets/images/misc/heart_icon.png")), 15, 20, 50, 40, null);
		g2.drawImage(ImageIO.read(App.class.getResource("/assets/images/misc/shield_icon.png")), 20, 70, 50, 40, null);
		g2.drawImage(ImageIO.read(App.class.getResource("/assets/images/misc/energy_icon.png")), 20, 120, 50, 40, null);

		
		Color[] emptyTop = { new Color(224, 165, 157), new Color(200, 200, 200), new Color(160, 190, 230) };

		Color[] emptyBottom = { new Color(209, 139, 132), new Color(170, 170, 170), new Color(130, 165, 210) };

		Color[] fillTop = { new Color(214, 47, 17), new Color(170, 170, 170), new Color(70, 120, 220) };

		Color[] fillBottom = { new Color(156, 31, 17), new Color(130, 130, 130), new Color(50, 90, 180) };

		int[] curVals = { health, shield, energy };
		int[] maxVals = { maxHealth, maxShield, maxEnergy };

		// -- Status Bars
		for (int i = 0; i < 3; i++) {
			g2.setColor(emptyTop[i]);
			int sy = 25 + i * 50;
			g2.fillRoundRect(70, sy, 180, 20, 10, 10); // empty part

			g2.setColor(emptyBottom[i]);
			g2.fillRoundRect(70, sy + 20, 180, 20, 10, 10); // bottom empty part

			g2.setColor(Color.BLACK);	
			g2.drawRoundRect(70, sy, 180, 40, 10, 10); // outer border

			int barWidth = getStatBarWidth(180, curVals[i], maxVals[i]);
			g2.setColor(fillTop[i]);
			g2.fillRoundRect(70, sy, barWidth, 40, 10, 10); // filled part

			g2.setColor(fillBottom[i]);
			g2.fillRoundRect(70, sy + 20, barWidth, 20, 10, 10); // bottom filled part

			g2.setColor(Color.BLACK);
			g2.drawRoundRect(70, sy, barWidth, 40, 10, 10); // inner border

			String status = curVals[i] + "/" + maxVals[i];
			g2.setColor(Color.WHITE);
			g2.setFont(new Font("Pixelify Sans", Font.BOLD, 22));
			g2.drawString(status, 140, 53 + i * 50);
		}
	}

	public void move() {
		float lx = deadzone(App.controllerState.leftStickX, 0.15f); 
		float ly = deadzone(App.controllerState.leftStickY, 0.15f);

		x += lx * speed; 
		y += ly * speed;

		// stay in bound [20, w or height - 20]
		if (x < 20)
			x = 20;
		else if (x > App.screenWidth - 20)
			x = App.screenWidth - 20;
		if (y < 20)
			y = 20;
		else if (y > App.screenHeight - 20)
			y = App.screenHeight - 20;

		// -- if a is pressed ONLY now
		if (App.controllerState != null && App.controllerState.aJustPressed) {
			App.rumble(0.2f, 0.2f, 10000);
		}
		
		// stop vibration
		if(App.controllerState != null && App.controllerState.xJustPressed) {
			App.rumble(0.0f, 0.0f, 0);
		}
		
		if(!isFacingLeft && App.controllerState.leftStickX < -0.1) {
			isFacingLeft = true;
		}
		else if(isFacingLeft && App.controllerState.leftStickX > 0.1) { // 0.10 is threshold
			isFacingLeft = false;
		}
	}
	
	public void drawCharacter(Graphics2D g2) {
		if(isFacingLeft) {
			g2.drawImage(sprite, x + width, y, -width, height, null);
		}
		else {
			g2.drawImage(sprite, x, y, width, height, null);
		}
	}


}