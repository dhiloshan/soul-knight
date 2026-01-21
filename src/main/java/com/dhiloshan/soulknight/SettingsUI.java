package com.dhiloshan.soulknight;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import com.studiohartman.jamepad.ControllerState;

public class SettingsUI {

    private int width = 800;
    private int height = 680;
    private int x, y;

    private int selectedOption = 0;
    private float musicVol = 0.5f;
    private float sfxVol = 0.8f;

    public SettingsUI() {
        x = (App.screenWidth - width) / 2;
        y = (App.screenHeight - height) / 2;
    }
    
    // Description: updates to see which slider or option the user is on (sound, sfx, or exit)
    // Parameters: the current state of the controller buttons and d-pad
    // Return: returns true when the user wants to exit and false to stay on the mnenu
    public boolean update(ControllerState state) {
        // Navigation (D-Pad Only)
        if (state.dpadUpJustPressed) {
            selectedOption--;
            if (selectedOption < 0)
                selectedOption = 2;
        }
        if (state.dpadDownJustPressed) {
            selectedOption++;
            if (selectedOption > 2)
                selectedOption = 0;
        }

        // adjust volume
        if (selectedOption == 0) { // music
            if (state.dpadRight)
                musicVol = Math.min(1.0f, musicVol + 0.05f); 
            if (state.dpadLeft)
                musicVol = Math.max(0.0f, musicVol - 0.05f);

            AudioManager.getInstance().setMusicVolume(musicVol);
        } else if (selectedOption == 1) { // SFX
            if (state.dpadRight)
                sfxVol = Math.min(1.0f, sfxVol + 0.05f);
            if (state.dpadLeft)
                sfxVol = Math.max(0.0f, sfxVol - 0.05f);

            AudioManager.getInstance().setSfxVolume(sfxVol);
        } else if (selectedOption == 2) { // to exit
            if (state.yJustPressed) 
                return true;
        }

        // exit
        if (state.yJustPressed)
            return true;

        return false;
    }

    public boolean checkClose(int x, int y) {
        return false;
    }
    
    // Description: renders the settings page
    // Parameters: Graphics2D g2 (the screen)
    // Return: void
    public void render(Graphics2D g2) {
        // Overlay
        g2.setColor(new Color(0, 0, 0, 240)); 
        g2.fillRect(0, 0, App.screenWidth, App.screenHeight);

        // Frame
        g2.setColor(new Color(30, 30, 40));
        g2.fillRoundRect(x, y, width, height, 20, 20);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(x, y, width, height, 20, 20);

        // Title
        g2.setFont(new Font("Pixelify Sans", Font.BOLD, 50)); 
        String title = "SETTINGS";
        int tx = x + (width - g2.getFontMetrics().stringWidth(title)) / 2;
        g2.drawString(title, tx, y + 70);

        // Options
        int startY = y + 160;
        int gap = 90;

        drawOption(g2, "Music Volume", musicVol, startY, selectedOption == 0);
        drawOption(g2, "SFX Volume", sfxVol, startY + gap, selectedOption == 1);

        if (selectedOption == 2)
            g2.setColor(Color.YELLOW);
        else
            g2.setColor(Color.GRAY);

        g2.setFont(new Font("Pixelify Sans", Font.BOLD, 30));
        String back = "Back (Y)";
        int bx = x + (width - g2.getFontMetrics().stringWidth(back)) / 2;
        g2.drawString(back, bx, startY + 2 * gap + 10);

        int cy = startY + 3 * gap; 
        g2.setColor(Color.LIGHT_GRAY);
        g2.setFont(new Font("Pixelify Sans", Font.BOLD, 28)); 
        g2.drawString("CONTROLS:", x + 150, cy); 

        g2.setFont(new Font("Pixelify Sans", Font.PLAIN, 22)); // Bigger Text
        int col1 = x + 150;
        int col2 = x + 450;

        g2.drawString("L-Stick: Move", col1, cy + 35);
        g2.drawString("R-Trigger: Shoot", col1, cy + 65);
        g2.drawString("Y: Settings / Back", col1, cy + 95);

        g2.setFont(new Font("Pixelify Sans", Font.PLAIN, 16));
        g2.setColor(Color.LIGHT_GRAY);
        String about1 = "Developed by Dhiloshan | Published: January 21st, 2026";
        String about2 = "Replica of Soul Knight game for Mac (Xbox Controller)";
        int abtY = y + height - 75; 
        int abtX1 = x + (width - g2.getFontMetrics().stringWidth(about1)) / 2;
        g2.drawString(about1, abtX1, abtY);
        int abtX2 = x + (width - g2.getFontMetrics().stringWidth(about2)) / 2;
        g2.drawString(about2, abtX2, abtY + 20);

        // navigation help
        String navHelp = "D-Pad Up/Down: Select  |  D-Pad Left/Right: Adjust";
        g2.setFont(new Font("Pixelify Sans", Font.ITALIC, 20));
        g2.setColor(Color.CYAN);
        int hx = x + (width - g2.getFontMetrics().stringWidth(navHelp)) / 2;
        g2.drawString(navHelp, hx, y + height - 25); 
    }
    
    // Description: draws the current sound or sfx amount as a bar
    // Parameters: Graphics2D g2 (the screen)
    // Return: void
    private void drawOption(Graphics2D g2, String label, float val, int yPos, boolean selected) {
        g2.setFont(new Font("Pixelify Sans", Font.BOLD, 32)); 
        if (selected)
            g2.setColor(Color.YELLOW);
        else
            g2.setColor(Color.WHITE);

        g2.drawString(label, x + 80, yPos);

        int barX = x + 350;
        int barW = 350; 
        int barH = 25;

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(barX, yPos - 20, barW, barH);

        g2.setColor(selected ? Color.CYAN : new Color(0, 100, 255));
        g2.fillRect(barX, yPos - 20, (int) (barW * val), barH);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(barX, yPos - 20, barW, barH);

        g2.drawString((int) (val * 100) + "%", barX + barW + 20, yPos);
    }
}
