package com.dhiloshan.soulknight;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Desktop;
import java.net.URI;
import javax.swing.ImageIcon;
import com.studiohartman.jamepad.ControllerState;

public class ControllerIntro {

    private int width, height;
    private Image bgImage;
    private Image slide1Img, slide2Img;
    private int slideState = 0; // 0=Home, 1=Slide1, 2=Slide2, 3=Slide3
    private boolean showAbout = false;

    public ControllerIntro() {
        width = App.screenWidth;
        height = App.screenHeight;

        try {
            bgImage = new ImageIcon(App.class.getResource("/assets/images/misc/backdrop.jpg")).getImage();
            slide1Img = new ImageIcon(App.class.getResource("/assets/images/misc/intro-1.png")).getImage();
            slide2Img = new ImageIcon(App.class.getResource("/assets/images/misc/intro-2.png")).getImage();
        } catch (Exception e) {
        }
    }


    public boolean update(ControllerState state) {
    	// Returns true when intro is done and game should start
        if (state.yJustPressed) {
            showAbout = !showAbout;
            return false;
        }
        if (showAbout)
            return false;

        if (state.aJustPressed) {
            slideState++;
            if (slideState > 3) {
                return true; // Done
            }
        }
        return false;
    }
    
    // Description: displays the introduction stuff
    // Parameters: Graphics2D g2 (the screen)
    // Return: void
    public void render(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, width, height);
        
        if (slideState == 0) {
            if (bgImage != null) {
                int iw = bgImage.getWidth(null);
                int ih = bgImage.getHeight(null);

                if (iw > 0 && ih > 0) {
                    float scale = (float) width / iw; 

                    float scaledHeight = ih * scale;
                    if (scaledHeight > height * 0.9f) {
                        scale = (height * 0.9f) / ih;
                    }

                    int dw = (int) (iw * scale);
                    int dh = (int) (ih * scale);
                    int dx = (width - dw) / 2;
                    int dy = (height - dh) / 2;

                    g2.drawImage(bgImage, dx, dy, dw, dh, null);
                }
            }
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Pixelify Sans", Font.BOLD, 50));
            String text = "Press A to Start";
            centerText(g2, text, height - 100);

            // about menu
            g2.setFont(new Font("Pixelify Sans", Font.PLAIN, 20));
            g2.setColor(Color.LIGHT_GRAY);
            centerText(g2, "Press Y for About", height - 60);

        } 
        else if (slideState == 1) {
            renderSlide(g2, slide1Img, "The Magic Stone is a sacred relic that maintains the balance of this world.");
        } 
        else if (slideState == 2) {
            renderSlide(g2, slide2Img,
                    "One day, a group of evil creatures stole the Magic Stone, and the world descended into chaos.");
        } 
        else if (slideState == 3) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Pixelify Sans", Font.BOLD, 60));
            String text = "Can you take back the Magic Stone?";
         
            centerText(g2, text, height / 2);

            g2.setFont(new Font("Pixelify Sans", Font.PLAIN, 20));
            centerText(g2, "Press A to Start Gameplay", height - 50);
        }

        // about overlay
        if (showAbout) {
            g2.setColor(new Color(0, 0, 0, 240));
            g2.fillRect(0, 0, width, height);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Pixelify Sans", Font.BOLD, 40));
            centerText(g2, "ABOUT SOUL KNIGHT", height / 3);

            g2.setFont(new Font("Pixelify Sans", Font.PLAIN, 24));
            centerText(g2, "Developed by Dhiloshan", height / 2 - 20);
            centerText(g2, "Published: January 21st, 2026", height / 2 + 20);
            centerText(g2, "Replica of Soul Knight game for Mac", height / 2 + 60);
            centerText(g2, "Playable on Xbox Controller", height / 2 + 90);

            g2.setFont(new Font("Pixelify Sans", Font.ITALIC, 20));
            g2.setColor(Color.YELLOW);
            centerText(g2, "Press Y to Close", height - 100);
        }
    }
    
    // Description: displays the introduction stuff
    // Parameters: Graphics2D g2 (the screen)
    // Return: void
    private void renderSlide(Graphics2D g2, Image img, String text) {
        if (img != null) {
            // Keep aspect ratio, smaller than screen
            int ih = (int) (height * 0.6);
            int iw = (int) ((double) img.getWidth(null) / img.getHeight(null) * ih);
            int ix = (width - iw) / 2;
            int iy = 50;
            g2.drawImage(img, ix, iy, iw, ih, null);
        }

        // Text
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Pixelify Sans", Font.BOLD, 30));

        // Simple manual wrap for long text
        FontMetrics fm = g2.getFontMetrics();
        int y = height - 200;
        int lineHeight = 40;

        // Word Wrap Logic
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        int maxChars = 50; // Roughly fit on screen nicely

        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > maxChars) {
                centerText(g2, currentLine.toString(), y);
                y += lineHeight;
                currentLine.setLength(0);
            }
            if (currentLine.length() > 0)
                currentLine.append(" ");
            currentLine.append(word);
        }
        // Draw last line
        if (currentLine.length() > 0) {
            centerText(g2, currentLine.toString(), y);
        }

        // Prompt
        g2.setFont(new Font("Pixelify Sans", Font.PLAIN, 20));
        centerText(g2, "Press A to Continue", height - 50);
    }

    private void centerText(Graphics2D g2, String text, int y) {
        int x = (width - g2.getFontMetrics().stringWidth(text)) / 2;
        g2.drawString(text, x, y);
    }
}
