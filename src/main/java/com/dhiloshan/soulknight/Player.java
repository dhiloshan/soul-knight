package com.dhiloshan.soulknight;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class Player extends Character {

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
        super(new ImageIcon(App.class.getResource("/assets/images/characters/knight-d.gif")).getImage(), width, height,
                sx, sy);

        worldX = sx; // player's starting position in pixels
        worldY = sy;

        screenX = App.screenWidth / 2 - (width + Data.weapon.width) / 2; // ensures the player is centered
        screenY = App.screenHeight / 2 - (height) / 2;

        solidArea = new Rectangle(5, 8, 31, 37);
        speed = 6.0f;
    }

    public float deadzone(float v, float dz) {
        if (Math.abs(v) < dz) {
            return 0f;
        } else {
            return v;
        }
    }

    public void move() {
        if (App.controllerState == null)
            return; // prevent errors

        lx = deadzone(App.controllerState.leftStickX, 0.15f);
        ly = deadzone(App.controllerState.leftStickY, 0.15f);

        float dx = lx * speed;
        float dy = ly * speed;

        collisionOn = false;

        // check Y axis
        float tempLx = lx;
        lx = 0;
        Data.cChecker.checkTile(this);
        if (!collisionOn)
            worldY += dy;

        lx = tempLx;

        // check X axis
        collisionOn = false;
        float tempLy = ly;
        ly = 0;
        Data.cChecker.checkTile(this);
        if (!collisionOn)
            worldX += dx;

        ly = tempLy;

        if (lx < -0.1f)
            isFacingLeft = true;
        else if (lx > 0.1f)
            isFacingLeft = false;

        // handles walking animation

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