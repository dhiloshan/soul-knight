package com.dhiloshan.soulknight;

import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Player extends Character {
    
    public static float lx = 0f;
    public static float ly = 0f;
    
    public final int screenX;
    public final int screenY;

    boolean wasAPressed = false;
    

    int maxHealth = 7, health = 7;
    int maxShield = 6, shield = 6;
    int maxEnergy = 180, energy = 180;
    
    Image spriteL = new ImageIcon(App.class.getResource("/assets/images/characters/knight-l.gif")).getImage();
    Image spriteR = new ImageIcon(App.class.getResource("/assets/images/characters/knight-r.gif")).getImage();
    boolean isMoving = false;
    boolean walkingFrameLeft = true;
    long lastStepTime = 0;
    static final long STEP_MS = 200; 
    
    public Player(int width, int height, int sx, int sy) {
        super(new ImageIcon(App.class.getResource("/assets/images/characters/knight-d.gif")).getImage(), width, height, sx, sy);
        
        worldX = App.tileSize * 140;
        worldY = App.tileSize * 180;
        
        screenX = App.screenWidth / 2 - App.tileSize / 2;
        screenY = App.screenHeight / 2 - App.tileSize / 2;
        speed = 4f;
    }

    public float deadzone(float v, float dz) { 
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

        worldX += dx;
        worldY += dy;

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
            spriteCur = sprite; 
        } else {
            spriteCur = walkingFrameLeft ? spriteL : spriteR;
        }

        if (isFacingLeft) {
            g2.drawImage(spriteCur, screenX + width, screenY, -width, height, null);
        } else {
            g2.drawImage(spriteCur, screenX, screenY, width, height, null);
        }
    }
}