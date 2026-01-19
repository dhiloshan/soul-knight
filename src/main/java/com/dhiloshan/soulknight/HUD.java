package com.dhiloshan.soulknight;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.io.IOException;

import javax.imageio.ImageIO;

public class HUD {
	
    int coinAmount = 37;
    String levelText = "1-3";
 

	public HUD() {
		// TODO Auto-generated constructor stub
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

	    int[] cur = { Data.player.health, Data.player.shield, Data.player.energy };
	    int[] max = { Data.player.maxHealth, Data.player.maxShield, Data.player.maxEnergy };

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
	public void drawHUD(Graphics2D g2) {
	    int w = App.screenWidth;
	    int h = App.screenHeight;
	    
	    // --- TOP RIGHT UI ---
	  
	    // pause button 
	    int pauseSize = 50;
	    int px = w - 70;
	    int py = 20;
	    g2.setColor(new Color(120, 100, 80)); // Wood brown border
	    g2.fillRoundRect(px, py, pauseSize, pauseSize, 10, 10);
	    g2.setColor(new Color(160, 130, 100)); // Lighter wood center
	    g2.fillRoundRect(px + 4, py + 4, pauseSize - 8, pauseSize - 8, 5, 5);
	    g2.setColor(new Color(230, 220, 190)); // Beige bars
	    g2.fillRect(px + 16, py + 14, 6, 22);
	    g2.fillRect(px + 28, py + 14, 6, 22);

	    // Coin
	    g2.setFont(new Font("Pixelify Sans", Font.BOLD, 20));
	    String coinStr = String.valueOf(coinAmount);
	    int coinTextW = g2.getFontMetrics().stringWidth(coinStr);
	    
	    // coin text
	    g2.setColor(Color.BLACK);
	    g2.drawString(coinStr, px - 100 - coinTextW, py + 35);
	    
	    // coin icon
	    g2.setColor(new Color(255, 215, 0)); // Gold
	    int cx = px - 20 - coinTextW - 110;
	    int cy = py + 15;
	    g2.fillOval(cx, cy, 24, 24);
	    g2.setColor(new Color(200, 20, 0)); 
	    g2.setStroke(new BasicStroke(2));
	    g2.drawOval(cx, cy, 24, 24);

	    // 3. Minimap Border & Level Text
	    // Placeholder rectangle for map border
	    g2.setColor(new Color(100, 100, 100, 150));
	    g2.setStroke(new BasicStroke(3));
	    g2.drawRect(w - 180, 100, 150, 100);
	    
	    // Level Text (e.g. 1-3) centered below map area
	    g2.setFont(new Font("Pixelify Sans", Font.BOLD, 25));
	    g2.setColor(Color.BLACK);
	    g2.drawString(levelText, w - 135, 250);
	    
	    // joystick appearance
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
	    
	    if(Data.weapon.prevShot >= System.currentTimeMillis() - 45) { // 16 ms per frame (responds when player shoots)
	    	g2.setColor(Color.RED); 
	    	g2.drawLine(atkX, atkY - 20, atkX, atkY + 40);
		    g2.drawLine(atkX - 30, atkY + 10, atkX + 30, atkY + 10); 
	    }
	    
	    // Ability Button
	    int abilX = atkX - 170; 
	    int abilY = atkY;
	    int abilR = 60;
	    
	    g2.setColor(new Color(220, 220, 220));
	    g2.fillOval(abilX - abilR - 10, abilY - 10, abilR * 2 + 20, abilR * 2 + 20);
	    g2.setColor(Color.GRAY);
	    g2.fillOval(abilX - abilR, abilY, abilR * 2, abilR * 2);  
	    
	    // add lightning bolt
	}


}
