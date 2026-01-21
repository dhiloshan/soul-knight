package com.dhiloshan.soulknight;

import java.awt.Color;
import java.awt.Graphics2D;

import java.util.Scanner;
import javax.swing.ImageIcon;

public class TileManager {

    Graphics2D g2;
    Tile[] tile;
    int cellTileNum[][];

    public TileManager(Graphics2D g2) {
        this.g2 = g2;
        tile = new Tile[12];
        cellTileNum = new int[Map.maxWorldCol[0]][Map.maxWorldRow[0]];

        getTileImage();
        loadCell("/assets/maps/lvl-1/C1.txt");
    }

    // Description: loads all of the tiles 
    // Parameters: none
    // Return: void
    public void getTileImage() {
        tile[0] = new Tile();
        tile[0].image = new ImageIcon(App.class.getResource("/assets/images/map/floor2.png")).getImage();

        tile[1] = new Tile();
        tile[1].image = new ImageIcon(App.class.getResource("/assets/images/map/wall.png")).getImage();
        tile[1].collision = true;

        tile[2] = new Tile();
        tile[2].image = new ImageIcon(App.class.getResource("/assets/images/map/big_tree.png")).getImage();
        tile[2].collision = true;

        tile[3] = new Tile();
        tile[3].image = new ImageIcon(App.class.getResource("/assets/images/map/spikes.png")).getImage();

        tile[4] = new Tile();
        tile[4].image = new ImageIcon(App.class.getResource("/assets/images/map/crate.png")).getImage();
        tile[4].collision = true;

        tile[5] = new Tile();
        tile[5].image = new ImageIcon(App.class.getResource("/assets/images/map/crate2.png")).getImage();
        tile[5].collision = true;

        tile[6] = new Tile();
        tile[6].image = new ImageIcon(App.class.getResource("/assets/images/map/totem.jpeg")).getImage();
        tile[6].collision = true;

        tile[7] = new Tile();
        tile[7].image = new ImageIcon(App.class.getResource("/assets/images/map/unbreakable_crate.png")).getImage();
        tile[7].collision = true;

        tile[8] = new Tile();
        tile[8].image = new ImageIcon(App.class.getResource("/assets/images/map/blank.png")).getImage();

        tile[9] = new Tile();
        tile[9].image = new ImageIcon(App.class.getResource("/assets/images/map/floor2.png")).getImage();

        tile[10] = new Tile();
        tile[10].image = new ImageIcon(App.class.getResource("/assets/images/map/floor3.png")).getImage();

        tile[11] = new Tile();
        tile[11].image = new ImageIcon(App.class.getResource("/assets/images/map/floor4.png")).getImage();
    }
    
    // Description: loads all of the relevant cells onto the screen
    // Parameters: Graphics2D g2 (the screen)
    // Return: void
    public void render(Graphics2D g2) {
        g2.setColor(new Color(34, 115, 69));
        g2.fillRect(0, 0, App.screenWidth, App.screenHeight);

        int worldCol = 0, worldRow = 0;

        while (worldCol < Map.maxWorldCol[0] && worldRow < Map.maxWorldRow[0]) {
            int tileNum = cellTileNum[worldCol][worldRow];

            int worldX = worldCol * App.tileSize;
            int worldY = worldRow * App.tileSize;
            int screenX = worldX - (int) Data.player.worldX + Data.player.screenX;
            int screenY = worldY - (int) Data.player.worldY + Data.player.screenY;

            if (worldX + App.tileSize > Data.player.worldX - Data.player.screenX &&
                    worldX - App.tileSize < Data.player.worldX + Data.player.screenX &&
                    worldY + App.tileSize > Data.player.worldY - Data.player.screenY &&
                    worldY - App.tileSize < Data.player.worldY + Data.player.screenY) {
                g2.drawImage(tile[tileNum].image, screenX, screenY, App.tileSize, App.tileSize, null);
            } /* SIGNIFICANTLY improves game performance by only drawing the tiles necessary
            	on the screen and not the entire world map
            	*/

            worldCol++;
            if (worldCol == Data.map.maxWorldCol[0]) {
                worldCol = 0;
                worldRow++;
            }
        }
    }

    // Description: loads the WORLD map C1.txt into the game
    // Parameters: the file path for the world map
    // Return: void
    public void loadCell(String filePath) {
        Scanner inFile = new Scanner(App.class.getResourceAsStream(filePath));

        int col = 0;
        int row = 0;

        while (inFile.hasNextLine() && row < Data.map.maxWorldRow[0]) {
            String line = inFile.nextLine().trim();
            String[] numbers = line.split(" ");

            for (int i = 0; i < numbers.length; i++) {
                if (col < Data.map.maxWorldCol[0]) {
                    String numStr = numbers[i].trim();
                    if (!numStr.isEmpty()) {
                    	cellTileNum[col][row] = Integer.parseInt(numStr);
                        col++;
                    }
                }
            }
            if (col == Data.map.maxWorldCol[0]) {
                col = 0;
                row++;
            }
        }
        inFile.close();
    }
}