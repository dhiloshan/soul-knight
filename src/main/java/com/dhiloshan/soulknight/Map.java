package com.dhiloshan.soulknight;

import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;

public class Map {
	
	Image[] tile;
	
	
	public Map() {
		
	}
	
	private void tilePreprocess(int idx) {
		// floor
		tile[0] = new ImageIcon(App.class.getResource("/assets/images/map/floor2.png")).getImage();
		// wall
		tile[1] = new ImageIcon(App.class.getResource("/assets/images/map/wall.png")).getImage();
		
	}
	
	public void render(Graphics2D g2) {
		int cw = 28, ch = cw; // cell dimensions
		int cx = 0, cy = 0; // current starting x and y position
		/*
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map[i].length; j++) {
				g2.drawImage(cellRender(map[i][j]), cx, cy, cw, ch, null);
				cx += cw;
			}
			cy += ch; cx = 0;
		}
		*/
		
		g2.drawRect(1300, 10, 200, 200); // map overview
	}

}
