package com.dhiloshan.soulknight;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Player extends Character {
	
	public static float lx = 0f;
	public static float ly = 0f;

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


	public void move() {
	    if (App.controllerState == null) return;

	    lx = deadzone(App.controllerState.leftStickX, 0.15f);
	    ly = deadzone(App.controllerState.leftStickY, 0.15f);

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