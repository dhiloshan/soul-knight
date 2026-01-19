package com.dhiloshan.soulknight;

public class Map {
	public static int maxWorldCol[];
	public static int maxWorldRow[];
	public static int worldWidth;
	public static int worldHeight;
	
	public static int maxScreenCol;
	public static int maxScreenRow;
	
	public Map() {
		maxWorldCol = new int[3];
		maxWorldCol[0] = 280;
		maxWorldRow = new int[3];
		maxWorldRow[0] = 201;
		worldWidth = App.tileSize * maxWorldCol[0];
		worldHeight = App.tileSize * maxWorldRow[0];
		
		maxScreenCol = 36;
		maxScreenRow = 20;
	}
}
