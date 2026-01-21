package com.dhiloshan.soulknight;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Image;
import javax.swing.ImageIcon;

public class MainMenu {

    private Rectangle startBtnRect;
    private int width, height;
    private Image backgroundImage;

    public MainMenu() {
        width = App.screenWidth;
        height = App.screenHeight;

        try {
            backgroundImage = new ImageIcon(App.class.getResource("/assets/images/misc/backdrop.jpg")).getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int btnW = 200;
        int btnH = 60;
        startBtnRect = new Rectangle((width - btnW) / 2, (height - btnH) / 2 + 50, btnW, btnH);
    }

    public void render(Graphics2D g2) {
        // Background
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, width, height, null);
        } else {
            g2.setColor(new Color(30, 30, 40));
            g2.fillRect(0, 0, width, height);
        }

        // Title
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Pixelify Sans", Font.BOLD, 64));
        String title = "SOUL KNIGHT";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, (width - fm.stringWidth(title)) / 2, height / 2 - 50);

        // Start Button
        g2.setColor(new Color(60, 130, 210)); // Button Color
        g2.fillRoundRect(startBtnRect.x, startBtnRect.y, startBtnRect.width, startBtnRect.height, 20, 20);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Pixelify Sans", Font.BOLD, 24));
        fm = g2.getFontMetrics();
        String startText = "Start Game";
        g2.drawString(startText, startBtnRect.x + (startBtnRect.width - fm.stringWidth(startText)) / 2,
                startBtnRect.y + (startBtnRect.height + fm.getAscent()) / 2 - 5);
    }

    public boolean checkStart(int mx, int my) {
        return startBtnRect.contains(mx, my);
    }
}
