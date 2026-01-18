package com.dhiloshan.soulknight;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Player extends Character {

	boolean wasAPressed = false;
	float speed = 6.7f;

	int maxHealth = 7, health = (int) (Math.random() * maxHealth);
	int maxShield = 6, shield = (int) (Math.random() * maxShield);
	int maxEnergy = 180, energy = (int) (Math.random() * maxEnergy);
	
	Image spriteL = new ImageIcon(App.class.getResource("/assets/images/characters/knight-l.gif")).getImage();
	Image spriteR = new ImageIcon(App.class.getResource("/assets/images/characters/knight-r.gif")).getImage();
	boolean isMoving = false;
	boolean leftGif = true;
	
	boolean walkingFrameLeft = true;
	long lastStepTime = 0;
	static final long STEP_MS = 200; // adjust: smaller = faster stepping
 
	public Player(int width, int height, int sx, int sy) {
		super( // the actual parameters for the constructor in the superclass, Character
				new ImageIcon(App.class.getResource("/assets/images/characters/knight-d.gif")).getImage(), width, height,
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
	    int panelX = 16, panelY = 16, panelW = 260, panelH = 120;

	    g2.setColor(new Color(205, 170, 135));
	    g2.fillRoundRect(panelX + 3, panelY + 3, panelW, panelH, 16, 16);

	    g2.setColor(new Color(186, 150, 118));
	    g2.fillRoundRect(panelX, panelY, panelW, panelH, 16, 16);

	    g2.setColor(new Color(240, 215, 185));
	    g2.drawRoundRect(panelX + 1, panelY + 1, panelW - 2, panelH - 2, 16, 16);

	    g2.setColor(new Color(92, 64, 51));
	    g2.drawRoundRect(panelX, panelY, panelW, panelH, 16, 16);

	    g2.drawImage(ImageIO.read(App.class.getResource("/assets/images/misc/health_icon.png")), 24, 26, 28, 28, null);
	    g2.drawImage(ImageIO.read(App.class.getResource("/assets/images/misc/shield_icon.png")), 24, 62, 28, 28, null);
	    g2.drawImage(ImageIO.read(App.class.getResource("/assets/images/misc/energy_icon.png")), 24, 98, 28, 28, null);

	    Color[] empty = {
	        new Color(230, 140, 135),
	        new Color(190, 190, 190),
	        new Color(150, 180, 220)
	    };

	    Color[] fill = {
	        new Color(215, 60, 55),
	        new Color(165, 165, 165),
	        new Color(60, 130, 210)
	    };

	    int[] cur = { health, shield, energy };
	    int[] max = { maxHealth, maxShield, maxEnergy };

	    g2.setFont(new Font("Pixelify Sans", Font.BOLD, 18));
	    FontMetrics fm = g2.getFontMetrics();

	    for (int i = 0; i < 3; i++) {
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

	        String txt = cur[i] + "/" + max[i];
	        int tx = x + (w - fm.stringWidth(txt)) / 2;
	        int ty = y + (h + fm.getAscent()) / 2 - 1;

	        g2.setColor(new Color(0, 0, 0, 150));
	        g2.drawString(txt, tx + 1, ty + 1);
	        g2.setColor(Color.WHITE);
	        g2.drawString(txt, tx, ty);
	    }
	}

	public void move() {
	    if (App.controllerState == null) return;

	    float lx = deadzone(App.controllerState.leftStickX, 0.15f);
	    float ly = deadzone(App.controllerState.leftStickY, 0.15f);

	    float dx = lx * speed;
	    float dy = ly * speed;

	    x += dx;
	    y += dy;

	    x = Math.max(Data.weapon.width, Math.min(x, App.screenWidth - (width + Data.weapon.width + 10)));
	    y = Math.max(Data.weapon.height, Math.min(y, App.screenHeight - (height + Data.weapon.height + 10)));

	    if (lx < -0.1f) isFacingLeft = true;
	    else if (lx > 0.1f) isFacingLeft = false;

	    boolean movingNow = (Math.abs(dx) + Math.abs(dy)) > 0.01f;
	    isMoving = movingNow;

	    if (isMoving) {
	        long now = System.currentTimeMillis();
	        if (now - lastStepTime >= STEP_MS) {
	            walkingFrameLeft = !walkingFrameLeft;
	            lastStepTime = now;
	        }
	    } else {
	        walkingFrameLeft = true;
	    }
	}
	
	public void render(Graphics2D g2) {
	    Image spriteCur;

	    if (!isMoving) {
	        spriteCur = sprite; // standing still
	    } else {
	        spriteCur = walkingFrameLeft ? spriteL : spriteR;
	    }

	    if (isFacingLeft) {
	        g2.drawImage(spriteCur, x + width, y, -width, height, null);
	    } else {
	        g2.drawImage(spriteCur, x, y, width, height, null);
	    }
	}


}