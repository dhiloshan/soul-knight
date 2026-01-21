package com.dhiloshan.soulknight;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

public class HUD {
	private Image healthIcon, shieldIcon, energyIcon, weaponIcon;

	public HUD() {
		try {
			healthIcon = ImageIO.read(App.class.getResource("/assets/images/misc/health_icon.png"));
			shieldIcon = ImageIO.read(App.class.getResource("/assets/images/misc/shield_icon.png"));
			energyIcon = ImageIO.read(App.class.getResource("/assets/images/misc/energy_icon.png"));
			weaponIcon = ImageIO.read(App.class.getResource("/assets/images/weapons/Bad_Pistol.png"));
		} catch (Exception e) {
		}
	}

	int getStatBarWidth(int totalWidth, int curAmount, int maxAmount) { // helper method
		return (int) ((1.0 * curAmount / maxAmount) * totalWidth);
	}
	
	// Description: displays the status bar area
	// Parameters: none
	// Return: void
	public void displayStatBar(Graphics2D g2) {
		int panelX = 16, panelY = 16, panelW = 260, panelH = 120;

		g2.setColor(new Color(205, 170, 135));
		g2.fillRoundRect(panelX + 3, panelY + 3, panelW, panelH, 16, 16);

		g2.setColor(new Color(186, 150, 118));
		g2.fillRoundRect(panelX, panelY, panelW, panelH, 16, 16);

		g2.setColor(new Color(240, 215, 185));
		g2.drawRoundRect(panelX + 1, panelY + 1, panelW - 2, panelH - 2, 16, 16);

		g2.setColor(new Color(92, 64, 51));
		g2.drawRoundRect(panelX, panelY, panelW, panelH, 16, 16);

		if (healthIcon != null)
			g2.drawImage(healthIcon, 24, 26, 28, 28, null);
		if (shieldIcon != null)
			g2.drawImage(shieldIcon, 24, 62, 28, 28, null);
		if (energyIcon != null)
			g2.drawImage(energyIcon, 24, 98, 28, 28, null);

		Color[] empty = { new Color(230, 140, 135), new Color(190, 190, 190), new Color(150, 180, 220) };
		Color[] fill = { new Color(215, 60, 55), new Color(165, 165, 165), new Color(60, 130, 210) };

		int[] cur = { Data.player.health, Data.player.shield, Data.player.energy };
		int[] max = { Data.player.maxHealth, Data.player.maxShield, Data.player.maxEnergy };

		g2.setFont(new Font("Pixelify Sans", Font.BOLD, 18));
		FontMetrics fm = g2.getFontMetrics();

		for (int i = 0; i < 3; i++) { // adds the status bars and fills them in
			int x = 60;
			int y = 26 + i * 36;
			int w = 190;
			int h = 22;

			g2.setColor(empty[i]);
			g2.fillRoundRect(x, y, w, h, 8, 8);

			int fw = getStatBarWidth(w, cur[i], max[i]);
			g2.setColor(fill[i]);
			g2.fillRoundRect(x, y, fw, h, 8, 8);

			g2.setColor(new Color(40, 40, 40));
			g2.drawRoundRect(x, y, w, h, 8, 8);

			// amount of health, armour, or energy left
			String txt = cur[i] + "/" + max[i];
			int tx = x + (w - fm.stringWidth(txt)) / 2;
			int ty = y + (h + fm.getAscent()) / 2 - 1;

			g2.setColor(new Color(0, 0, 0, 150));
			g2.drawString(txt, tx + 1, ty + 1);
			g2.setColor(Color.WHITE);
			g2.drawString(txt, tx, ty);
		}
	}
	
	// Description: draws the heads-up display
	// Parameters: Graphics2D g2 (the screen
	// Return: void
	public void drawHUD(Graphics2D g2) {
		int w = App.screenWidth;
		int h = App.screenHeight;

		// pause button
		int pauseSize = 50;
		int px = w - 70;
		int py = 20;

		g2.setFont(new Font("Pixelify Sans", Font.BOLD, 15));
		g2.drawString("Press Y to Pause", px - 75, py + 10);

		g2.setColor(new Color(120, 100, 80));
		g2.fillRoundRect(px, py + 30, pauseSize, pauseSize, 10, 10);
		g2.setColor(new Color(160, 130, 100));
		g2.fillRoundRect(px + 4, py + 34, pauseSize - 8, pauseSize - 8, 5, 5);
		// pause icon
		g2.setColor(new Color(230, 220, 190));
		g2.fillRect(px + 16, py + 44, 6, 22);
		g2.fillRect(px + 28, py + 44, 6, 22);

		// show joystick
		int joyX = 150;
		int joyY = h - 150;

		// Outer ring
		g2.setColor(new Color(220, 220, 220));
		g2.fillOval(joyX - 90, joyY - 90, 180, 180);
		g2.setColor(Color.GRAY);
		g2.fillOval(joyX - 80, joyY - 80, 160, 160);

		int joyPosX = Math.round((Data.player.lx + 1) * 30 + 40); // from 0 to 2 determine center
		int joyPosY = Math.round((Data.player.ly + 1) * 30 + 40);

		// Inner Stick
		g2.setColor(new Color(255, 255, 255, 150));
		g2.fillOval(joyPosX + 45, joyPosY + 655, 70, 70);

		int atkX = w - 150;
		int atkY = h - 150;
		int atkR = 80;

		// Attack button
		g2.setColor(new Color(220, 220, 220));
		g2.fillOval(atkX - atkR - 10, atkY - atkR, atkR * 2 + 20, atkR * 2 + 20);
		g2.setColor(Color.GRAY);
		g2.fillOval(atkX - atkR, atkY - atkR + 10, atkR * 2, atkR * 2);

		// symbol inside the attack button
		g2.setColor(Color.WHITE);
		g2.drawOval(atkX - 20, atkY - 10, 40, 40);
		g2.drawLine(atkX, atkY - 20, atkX, atkY + 40);
		g2.drawLine(atkX - 30, atkY + 10, atkX + 30, atkY + 10);

		if (Data.weapon.prevShot >= System.currentTimeMillis() - 45) { // 16 ms per frame (responds when player shoots)
			g2.setColor(Color.RED);
			g2.drawLine(atkX, atkY - 20, atkX, atkY + 40);
			g2.drawLine(atkX - 30, atkY + 10, atkX + 30, atkY + 10);
		}

		int weaponX = atkX;
		int weaponY = atkY - 180;
		int weaponR = 50;

		g2.setColor(new Color(220, 220, 220));
		g2.fillOval(weaponX - weaponR - 5, weaponY - weaponR - 5, weaponR * 2 + 10, weaponR * 2 + 10);
		g2.setColor(new Color(80, 80, 80));
		g2.fillOval(weaponX - weaponR, weaponY - weaponR, weaponR * 2, weaponR * 2);

		if (weaponIcon != null)
			g2.drawImage(weaponIcon, weaponX - 25, weaponY - 25, 50, 50, null);

		int diamondX = weaponX + 35;
		int diamondY = weaponY - 35;
		int diamondSize = 24;

		g2.setColor(new Color(60, 130, 210));
		int[] xPoints = { diamondX, diamondX + diamondSize / 2, diamondX, diamondX - diamondSize / 2 };
		int[] yPoints = { diamondY - diamondSize / 2, diamondY, diamondY + diamondSize / 2, diamondY };
		g2.fillPolygon(xPoints, yPoints, 4);

		g2.setColor(Color.WHITE);
		g2.drawPolygon(xPoints, yPoints, 4);

		g2.setFont(new Font("Pixelify Sans", Font.BOLD, 14));
		String energyCost = "1";
		FontMetrics fm2 = g2.getFontMetrics();
		int ecX = diamondX - fm2.stringWidth(energyCost) / 2;
		int ecY = diamondY + fm2.getAscent() / 2 - 2;
		g2.drawString(energyCost, ecX, ecY);
	}

	public void renderPauseScreen(Graphics2D g2) {
		// transparent background to darken the game
		g2.setColor(new Color(0, 0, 0, 150));
		g2.fillRect(0, 0, App.screenWidth, App.screenHeight);

		int px = 100, py = 100;
		g2.setColor(new Color(120, 100, 80));
		g2.fillRoundRect(px, py, App.screenWidth - 2 * px, App.screenHeight - 2 * py, 10, 10);
		g2.setColor(new Color(160, 130, 100));
		g2.fillRoundRect(px - 4, py - 4, App.screenWidth - 2 * px, App.screenHeight - 2 * py, 10, 10);
	}

}
