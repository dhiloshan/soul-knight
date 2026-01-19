package com.dhiloshan.soulknight;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;

import javax.swing.ImageIcon;

import java.util.*;
import java.io.*;

public class TileManager {

	Graphics2D g2;
	Tile[] tile;
	int cellTileNum[][];
	
	public TileManager(Graphics2D g2) {
		this.g2 = g2;
		tile = new Tile[10];
		
		cellTileNum = new int[36][20];
		getTileImage();
		loadCell("/assets/maps/lvl-1/C1");
	}
	
	public void getTileImage() {
		tile[0] = new Tile();
		tile[0].image = new ImageIcon(App.class.getResource("/assets/images/map/floor2.png")).getImage(); 
		
		tile[1] = new Tile();
		tile[1].image = new ImageIcon(App.class.getResource("/assets/images/map/wall.png")).getImage(); 

		tile[2] = new Tile();
		tile[2].image = new ImageIcon(App.class.getResource("/assets/images/map/crate.png")).getImage(); 
	}
	
	public void render(Graphics2D g2) {
		g2.setColor(new Color(34, 115, 69));
	    g2.fillRect(0, 0, App.screenWidth, App.screenHeight);
		int col = 0, row = 0, x = 50, y = 90;
		
		while(col < 36 && row < 19) {
			int tileNum = cellTileNum[col][row];
			g2.drawImage(tile[tileNum].image, x, y, 40, 40, null);
			col++;
			x += 40;
			
			if(col == 35) { // max for a row
				col = 0; x = 50;
				row++; y += 40;
			}
		}
	}
	
	public void loadCell(String filePath) {
		Scanner inFile = new Scanner(App.class.getResourceAsStream(filePath));
		int col = 0, row = 0;
		while (col < 36 && row < 19 && inFile.hasNextLine()) {
			String line = inFile.nextLine();
			while(col < 35) {
				String numbers[] = line.split(" ");
				int num = Integer.parseInt(numbers[col]);
				cellTileNum[col][row] = num;
				col++;
			}
			if(col == 35) {
				col = 0;
				row++;
			}
		}
		inFile.close();
	}
}
